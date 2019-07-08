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

import jodd.cache.TypeCache;
import jodd.log.Logger;
import jodd.log.LoggerFactory;
import jodd.madvoc.ActionRequest;
import jodd.madvoc.MadvocException;
import jodd.madvoc.config.ActionRuntime;
import jodd.madvoc.meta.RenderWith;
import jodd.madvoc.result.ActionResult;
import jodd.petite.meta.PetiteInject;
import jodd.util.ClassUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * Manager for Madvoc result handlers.
 */
public class ResultsManager {

	private static final Logger log = LoggerFactory.getLogger(ResultsManager.class);

	@PetiteInject
	protected ContextInjectorComponent contextInjectorComponent;

	public ResultsManager() {
		this.allResults = TypeCache.createDefault();
	}

	// ---------------------------------------------------------------- container

	protected final TypeCache<ActionResult> allResults;

	/**
	 * Returns all action results as new set.
	 */
	public Set<ActionResult> getAllActionResults() {
		final Set<ActionResult> set = new HashSet<>(allResults.size());
		allResults.forEachValue(set::add);
		return set;
	}

	/**
	 * Registers an action result handler and returns created {@link jodd.madvoc.result.ActionResult} if
	 * result with same type doesn't exist. Otherwise, returns existing result and created one will be ignored.
	 */
	public ActionResult register(final Class<? extends ActionResult> resultClass) {
		return register(createResult(resultClass));
	}

	/**
	 * Registers new action result instance. If action result of the same class is already
	 * registered, registration will be skipped. If result for the same result type or
	 * same target class exist, it will be replaced! However, default Jodd results will
	 * <i>never</i> replace other results. After the registration, results are initialized.
	 */
	protected ActionResult register(final ActionResult result) {
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
	private ActionResult lookupAndRegisterIfMissing(final Class<? extends ActionResult> actionResultClass) {
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
	public ActionResult lookup(final ActionRequest actionRequest, final Object resultObject) {

		ActionResult actionResultHandler = null;

		// + read @RenderWith value on method
		{
			final ActionRuntime actionRuntime = actionRequest.getActionRuntime();

			final Class<? extends ActionResult> actionResultClass = actionRuntime.getActionResult();

			if (actionResultClass != null) {
				actionResultHandler = lookupAndRegisterIfMissing(actionResultClass);
			}
		}

		// + use @RenderWith value on resulting object if exist
		if (actionResultHandler == null && resultObject != null) {
			final RenderWith renderWith = resultObject.getClass().getAnnotation(RenderWith.class);

			if (renderWith != null) {
				actionResultHandler = lookupAndRegisterIfMissing(renderWith.value());
			}
			else if (resultObject instanceof ActionResult) {
				// special case - returned value is already the ActionResult
				actionResultHandler = (ActionResult) resultObject;
			}
		}

		// + use action configuration
		if (actionResultHandler == null) {
			final ActionRuntime actionRuntime = actionRequest.getActionRuntime();

			final Class<? extends ActionResult> actionResultClass = actionRuntime.getDefaultActionResult();

			if (actionResultClass != null) {
				actionResultHandler = lookupAndRegisterIfMissing(actionResultClass);
			}
		}

		if (actionResultHandler == null) {
			throw new MadvocException("ActionResult not found for: " + resultObject);
		}

		// set action result object into action request!
		actionRequest.bindActionResult(resultObject);

		return actionResultHandler;
	}


	// ---------------------------------------------------------------- init

	/**
	 * Initializes action result.
	 */
	protected void initializeResult(final ActionResult result) {
		contextInjectorComponent.injectContext(result);
	}

	// ---------------------------------------------------------------- create

	/**
	 * Creates new {@link jodd.madvoc.result.ActionResult}.
	 */
	protected ActionResult createResult(final Class<? extends ActionResult> actionResultClass) {
		try {
			return ClassUtil.newInstance(actionResultClass);
		} catch (Exception ex) {
			throw new MadvocException("Invalid Madvoc result: " + actionResultClass, ex);
		}
	}

}