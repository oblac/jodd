// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.madvoc.component;

import jodd.log.Logger;
import jodd.log.LoggerFactory;
import jodd.madvoc.ActionConfig;
import jodd.madvoc.ActionRequest;
import jodd.madvoc.MadvocException;
import jodd.madvoc.injector.Target;
import jodd.madvoc.meta.RenderWith;
import jodd.madvoc.result.ActionResult;
import jodd.madvoc.result.Result;
import jodd.petite.meta.PetiteInject;
import jodd.typeconverter.TypeConverterManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Manager for Madvoc result handlers.
 */
public class ResultsManager {

	private static final Logger log = LoggerFactory.getLogger(ResultsManager.class);

	@PetiteInject
	protected ContextInjectorComponent contextInjectorComponent;
	@PetiteInject
	protected MadvocConfig madvocConfig;

	public ResultsManager() {
		this.stringResults = new HashMap<>();
		this.allResults = new HashMap<>();
		this.typeResults = new HashMap<>();
	}

	// ---------------------------------------------------------------- container

	protected final Map<String, ActionResult> stringResults;
	protected final Map<Class, ActionResult> typeResults;
	protected final Map<Class<? extends ActionResult>, ActionResult> allResults;

	/**
	 * Returns all action results.
	 */
	public Set<ActionResult> getAllActionResults() {
		Set<ActionResult> set = new HashSet<>(allResults.size());
		set.addAll(allResults.values());
		return set;
	}

	/**
	 * Registers an action result handler and returns created {@link jodd.madvoc.result.ActionResult} if
	 * result with same type doesn't exist. Otherwise, returns existing result and created one will be ignored.
	 */
	public ActionResult register(Class<? extends ActionResult> resultClass) {
		return register(createResult(resultClass));
	}

	/**
	 * Registers new action result instance. If action result of the same class is already
	 * registered, registration will be skipped. If result for the same result type or
	 * same target class exist, it will be replaced! However, default Jodd results will
	 * <i>never</i> replace other results. After the registration, results are initialized.
	 */
	protected ActionResult register(ActionResult result) {
		Class<? extends ActionResult> actionResultClass = result.getClass();

		// check existing

		ActionResult existingResult = allResults.get(actionResultClass);

		if (existingResult != null) {
			if (log.isDebugEnabled()) {
				log.debug("ActionResult already registered: " + actionResultClass);
			}
			return existingResult;
		}

		// + string hook

		String resultName = result.getResultName();

		if (resultName != null) {
			existingResult = stringResults.get(resultName);

			if (existingResult != null) {
				// the same result name exist
				if (!resultMayReplaceExistingOne(actionResultClass)) {
					if (log.isDebugEnabled()) {
						log.debug("ActionResult already registered: " + actionResultClass);
					}
					return existingResult;
				}

				// allow only one action result per result type
				allResults.remove(existingResult.getClass());
			}

			if (log.isInfoEnabled()) {
				log.debug("ActionResult registered: " + resultName + " -> " + actionResultClass);
			}

			stringResults.put(resultName, result);
		}

		// + type result

		Class resultValueType = result.getResultValueType();

		if (resultValueType != null && resultValueType != String.class) {
			existingResult = typeResults.get(resultValueType);

			if (existingResult != null) {
				if (!resultMayReplaceExistingOne(actionResultClass)) {
					if (log.isDebugEnabled()) {
						log.debug("ActionResult already registered: " + actionResultClass);
					}
					return existingResult;
				}

				// allow only one action result per result type
				allResults.remove(existingResult.getClass());
			}

			if (log.isInfoEnabled()) {
				log.debug("ActionResult registered: " + resultValueType + " -> " + actionResultClass);
			}

			typeResults.put(resultValueType, result);
		}

		// + all results

		if (log.isInfoEnabled()) {
			log.debug("ActionResult registered: " + actionResultClass);
		}

		allResults.put(actionResultClass, result);

		// + init

		initializeResult(result);

		return result;
	}

	/**
	 * Returns <code>true</code> if action result can replace existing one.
	 * This rule makes sure that Jodd's default results never replace custom
	 * results. This rule is important since result are found on classpath
	 * and can be registered without any order.
	 */
	protected boolean resultMayReplaceExistingOne(Class<? extends ActionResult> actionResultClass) {
		String packageName = actionResultClass.getPackage().getName();
		return !packageName.startsWith("jodd.");
	}

	// ---------------------------------------------------------------- lookup

	/**
	 * Lookups for action result and {@link #register(Class) registers} it if missing.
	 */
	private ActionResult lookupAndRegisterIfMissing(Class<? extends ActionResult> actionResultClass) {
		ActionResult actionResult = allResults.get(actionResultClass);

		if (actionResult == null) {
			actionResult = register(actionResultClass);
		}

		return actionResult;
	}

	/**
	 * Lookups for {@link jodd.madvoc.result.ActionResult action result handler}
	 * based on current {@link jodd.madvoc.ActionRequest action request} and action method
	 * result object. Lookup performs the following in given order:
	 * <ul>
	 *     <li>if result object is <code>null</code>, check if {@link jodd.madvoc.result.Result} is used</li>
	 *     <li>check result definition in <code>@Action</code> annotation</li>
	 *     <li>if result is not a <code>String</code>, check if it is annotated with {@link jodd.madvoc.meta.RenderWith} annotation</li>
	 *     <li>if result is not a <code>String</code>, find ActionResult for matching result object type</li>
	 *     <li>if action result still not found, call <code>toString</code> on result object and parse it</li>
	 * </ul>
	 */
	public ActionResult lookup(ActionRequest actionRequest, Object resultObject) {
		ActionResult actionResult = null;

		// + special class: result
		if (resultObject == null) {
			Result result = actionRequest.getResult();

			if (result != null) {
				// read Result, if used; if not, values will be null
				Class<? extends ActionResult> actionResultClass = result.getActionResult();

				resultObject = result.getResultValue();
				if (resultObject == null) {
					resultObject = result.value();
				}

				if (actionResultClass != null) {
					actionResult = lookupAndRegisterIfMissing(actionResultClass);
				}
			}
		}

		if (actionResult == null) {
			// + still not found, read @Action value
			ActionConfig actionConfig = actionRequest.getActionConfig();

			Class<? extends ActionResult> actionResultClass = actionConfig.getActionResult();
			if (actionResultClass != null) {
				actionResult = lookupAndRegisterIfMissing(actionResultClass);
			}
		}

		if (actionResult == null && resultObject != null) {
			Class resultType = resultObject.getClass();

			if (resultType != String.class) {
				// + still not found, read @RenderWith value if exist
				RenderWith renderWith = resultObject.getClass().getAnnotation(RenderWith.class);

				if (renderWith != null) {
					actionResult = lookupAndRegisterIfMissing(renderWith.value());
				}

				if (actionResult == null) {
					// + still not found, lookup for type
					actionResult = typeResults.get(resultObject.getClass());
				}
			}
		}

		if (actionResult == null) {
			// + still not found, toString()

			ActionResult defaultActionResult = lookupAndRegisterIfMissing(madvocConfig.getDefaultActionResult());

			if (stringResults.isEmpty()) {
				// no string results registered, carry on with the defaults.
				actionResult = defaultActionResult;
			}
			else {
				String resultValue = resultObject != null ? resultObject.toString() : null;
				String resultName = null;

				// first check result value
				if (resultValue != null) {
					int columnIndex = resultValue.indexOf(':');

					if (columnIndex != -1) {
						resultName = resultValue.substring(0, columnIndex);

						resultValue = resultValue.substring(columnIndex + 1);
					}
				}

				if (resultName != null) {
					actionResult = stringResults.get(resultName);
				}
				else {
					actionResult = defaultActionResult;
				}

				if (actionResult.getResultName() != null) {
					// convert remaining of the string to result object
					// only when action result is string result
					try {
						Class targetClass = actionResult.getResultValueType();
						if (targetClass == null || targetClass == String.class) {
							resultObject = resultValue;
						}
						else {
							resultObject = TypeConverterManager.get().convertType(resultValue, targetClass);
						}
					} catch (Exception ex) {
						resultObject = resultValue;
					}
				}
			}
		}

		// set action result object into action request!
		actionRequest.setActionResult(resultObject);

		return actionResult;
	}


	// ---------------------------------------------------------------- init

	/**
	 * Initializes action result.
	 */
	protected void initializeResult(ActionResult result) {
		contextInjectorComponent.injectContext(new Target(result));

		result.init();
	}

	// ---------------------------------------------------------------- create

	/**
	 * Creates new {@link jodd.madvoc.result.ActionResult}.
	 */
	protected ActionResult createResult(Class<? extends ActionResult> actionResultClass) {
		try {
			return actionResultClass.newInstance();
		} catch (Exception ex) {
			throw new MadvocException("Invalid Madvoc result: " + actionResultClass, ex);
		}
	}

}