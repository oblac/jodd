// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.madvoc.MadvocException;
import jodd.madvoc.result.ActionResult;
import jodd.petite.meta.PetiteInject;

import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/**
 * Manager for Madvoc results.
 */
public class ResultsManager {

	@PetiteInject
 	protected MadvocContextInjector madvocContextInjector;

	@PetiteInject
	protected ServletContextInjector servletContextInjector;

	@PetiteInject
	protected MadvocController madvocController;

	public ResultsManager() {
		this.stringResults = new HashMap<String, ActionResult>();
		this.allResults = new HashMap<Class<? extends ActionResult>, ActionResult>();
	}

	// ---------------------------------------------------------------- container

	protected final Map<String, ActionResult> stringResults;
	protected final Map<Class<? extends ActionResult>, ActionResult> allResults;

	/**
	 * Returns all action results.
	 */
	public Set<ActionResult> getAllActionResults() {
		Set<ActionResult> set = new HashSet<ActionResult>(stringResults.size() + allResults.size());
		set.addAll(stringResults.values());
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
	 * Registers new action result instance.
	 */
	public ActionResult register(ActionResult result) {
		String resultType = result.getResultType();

		if (resultType != null) {
			ActionResult existing = lookup(resultType);
			if (existing != null) {
				Class resultClass = result.getClass();
				if (existing.getClass().equals(resultClass) == false) {
					throw new MadvocException("Duplicate Madvoc result: " + result);
				}
				result = existing;
			} else {
				stringResults.put(result.getResultType(), result);
			}
		}

		allResults.put(result.getClass(), result);

		madvocContextInjector.injectMadvocContext(result);
		madvocContextInjector.injectMadvocParams(result);

		return result;
	}

	/**
	 * Returns registered action instance for result class.
	 * @see #lookup(String)
	 */
	public ActionResult lookup(Class<? extends ActionResult> resultClass) {
		return allResults.get(resultClass);
	}

	/**
	 * Returns an action result for specified result type.
	 * Returns <code>null</code> if result type is not registered.
	 * @see #lookup(Class)
	 */
	public ActionResult lookup(String resultType) {
		return stringResults.get(resultType);
	}

	// ---------------------------------------------------------------- init

	/**
	 * Initializes action result.
	 */
	protected void initializeResult(ActionResult result) {
		servletContextInjector.injectContext(result, madvocController.getApplicationContext());
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