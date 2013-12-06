// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.madvoc.ActionRequest;
import jodd.madvoc.MadvocException;
import jodd.madvoc.result.ActionResult;
import jodd.petite.meta.PetiteInject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.HashMap;

/**
 * Manager for Madvoc results.
 */
public class ResultsManager {

	@PetiteInject
 	protected MadvocContextInjector madvocContextInjector;

	@PetiteInject
	protected ServletContextInjector servletContextInjector;

	public ResultsManager() {
		this.results = new HashMap<String, ActionResult>();
	}

	// ---------------------------------------------------------------- container

	protected final Map<String, ActionResult> results;

	/**
	 * Returns all action results. Should be used with care.
	 */
	public Map<String, ActionResult> getAllActionResults() {
		return results;
	}

	/**
	 * Registers an action result handler and returns created {@link jodd.madvoc.result.ActionResult} if
	 * result with same type doesn't exist. Otherwise, returns existing result and created one will be ignored.
	 */
	public ActionResult register(Class<? extends ActionResult> resultClass) {
		return register(createResult(resultClass));
	}

	/**
	 * Registers new action result instance.
	 */
	public ActionResult register(ActionResult result) {
		ActionResult existing = lookup(result.getType());
		if (existing != null) {
			Class resultClass = result.getClass();
			if (existing.getClass().equals(resultClass) == false) {
				throw new MadvocException(
						"Madvoc result with the same result type '" + result.getType() +
						"' already registered: " + resultClass.getSimpleName());
			}
			result = existing;
		} else {
			results.put(result.getType(), result);
		}

		madvocContextInjector.injectMadvocContext(result);
		madvocContextInjector.injectMadvocParams(result);

		return result;
	}

	/**
	 * Returns currently registered action for result class.
	 */
	public ActionResult lookup(Class<? extends ActionResult> resultClass) {
		for (ActionResult actionResult : results.values()) {
			if (actionResult.getClass() == resultClass) {
				return actionResult;
			}
		}
		return null;
	}

	/**
	 * Returns an action result handler for specified result type.
	 * Returns <code>null</code> if result type is not previously registered.
	 */
	public ActionResult lookup(String resultType) {
		return results.get(resultType);
	}

	// ---------------------------------------------------------------- init

	/**
	 * Initializes action result.
	 */
	protected void initializeResult(ActionResult result, ActionRequest actionRequest) {
		HttpServletRequest httpServletRequest = actionRequest.getHttpServletRequest();
		HttpServletResponse httpServletResponse = actionRequest.getHttpServletResponse();

		servletContextInjector.injectContext(result, httpServletRequest, httpServletResponse);

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
			throw new MadvocException("Unable to create Madvoc action result: " + actionResultClass, ex);
		}
	}
}
