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
import jodd.madvoc.MadvocConfig;
import jodd.madvoc.MadvocException;
import jodd.madvoc.config.ActionRuntime;
import jodd.madvoc.injector.Target;
import jodd.madvoc.meta.RenderWith;
import jodd.madvoc.result.ActionResult;
import jodd.madvoc.result.Chain;
import jodd.madvoc.result.Forward;
import jodd.madvoc.result.Move;
import jodd.madvoc.result.PermanentRedirect;
import jodd.madvoc.result.Redirect;
import jodd.petite.meta.PetiteInject;
import jodd.util.ClassUtil;
import jodd.util.StringPool;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Manager for Madvoc result handlers.
 */
public class ResultsManager {

	private static final Logger log = LoggerFactory.getLogger(ResultsManager.class);

	@PetiteInject
	protected ContextInjectorComponent contextInjectorComponent;
	@PetiteInject
	protected MadvocConfig madvocConfig;

	protected String defaultResultName;

	public ResultsManager() {
		this.stringResultsFactories = new HashMap<>();
		this.allResults = new HashMap<>();

		// defaults
		registerResultName("move", Move::to);
		registerResultName("chain", Chain::to);
		registerResultName("redirect", Redirect::to);
		registerResultName("url", PermanentRedirect::to);
		registerResultName("dispatch", Forward::to);

		setDefaultResultName("dispatch");
	}

	// ---------------------------------------------------------------- container

	protected final Map<String, Function<String, Object>> stringResultsFactories;
	protected final Map<Class<? extends ActionResult>, ActionResult> allResults;

	/**
	 * Returns all action results.
	 */
	public Set<ActionResult> getAllActionResults() {
		Set<ActionResult> set = new HashSet<>(allResults.size());
		set.addAll(allResults.values());
		return set;
	}

	public void registerResultName(String name, Function<String, Object> factory) {
		stringResultsFactories.put(name, factory);
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

		allResults.put(actionResultClass, result);

		// + init

		initializeResult(result);

		return result;
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
	 * result object.
	 */
	public ActionResult lookup(ActionRequest actionRequest, Object resultObject) {
		// + use result value
		if (resultObject == null || resultObject instanceof String) {
			// string results

			String resultName = defaultResultName;
			String resultValue = StringPool.EMPTY;

			if (resultObject != null) {
				resultValue = resultObject.toString();

				// first check result value
				int columnIndex = resultValue.indexOf(':');

				if (columnIndex != -1) {
					resultName = resultValue.substring(0, columnIndex);

					resultValue = resultValue.substring(columnIndex + 1);
				}
			}

			Function<String, Object> function = stringResultsFactories.get(resultName);

			if (function == null) {
				throw new MadvocException("Invalid result name:" + resultName);
			}

			resultObject = function.apply(resultValue);
		}

		ActionResult actionResultHandler = null;

		// + read @RenderWith value on method
		if (actionResultHandler == null) {

			ActionRuntime actionRuntime = actionRequest.getActionRuntime();

			Class<? extends ActionResult> actionResultClass = actionRuntime.actionResult();
			if (actionResultClass != null) {
				actionResultHandler = lookupAndRegisterIfMissing(actionResultClass);
			}
		}

		// + use @RenderWith value on resulting object if exist
		if (actionResultHandler == null) {
			RenderWith renderWith = resultObject.getClass().getAnnotation(RenderWith.class);

			if (renderWith != null) {
				actionResultHandler = lookupAndRegisterIfMissing(renderWith.value());
			}
		}

		// use annotation configuration
		if (actionResultHandler == null) {
			ActionConfig actionConfig = actionRequest.getActionRuntime().actionConfig();

			Class<? extends ActionResult> actionResultClass = actionConfig.getActionResult();
			if (actionResultClass != null) {
				actionResultHandler = lookupAndRegisterIfMissing(actionResultClass);
			}
		}

		if (actionResultHandler == null) {
			throw new MadvocException("ActionResult not found for: " + resultObject);
		}

		// set action result object into action request!
		actionRequest.setActionResult(resultObject);

		return actionResultHandler;
	}


	// ---------------------------------------------------------------- init

	public String getDefaultResultName() {
		return defaultResultName;
	}

	public void setDefaultResultName(String defaultResultName) {
		this.defaultResultName = defaultResultName;
	}

	/**
	 * Initializes action result.
	 */
	protected void initializeResult(ActionResult result) {
		contextInjectorComponent.injectContext(new Target(result));
	}

	// ---------------------------------------------------------------- create

	/**
	 * Creates new {@link jodd.madvoc.result.ActionResult}.
	 */
	protected ActionResult createResult(Class<? extends ActionResult> actionResultClass) {
		try {
			return ClassUtil.newInstance(actionResultClass);
		} catch (Exception ex) {
			throw new MadvocException("Invalid Madvoc result: " + actionResultClass, ex);
		}
	}

}