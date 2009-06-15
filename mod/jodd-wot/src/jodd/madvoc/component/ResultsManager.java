// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.madvoc.MadvocException;
import jodd.madvoc.result.ActionResult;

import java.util.Map;
import java.util.HashMap;

/**
 * Manager for Madvoc results.
 */
public class ResultsManager {

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
		ActionResult result = createResult(resultClass);
		ActionResult existing = lookup(result.getType());
		if (existing != null) {
			if (existing.getClass().equals(resultClass) == false) {
				throw new MadvocException("Madvoc result with the same result type '" + result.getType() + "' already registered: '"
						+ resultClass.getSimpleName() + "'.");
			}
			result = existing;
		} else {
			results.put(result.getType(), result);
		}
		return result;
	}

	/**
	 * Returns currently registered action for result class.
	 */
/*
	public ActionResult lookup(Class<? extends ActionResult> resultClass) {
		ActionResult result = createResult(resultClass);
		return results.get(result.getType());
	}
*/

	/**
	 * Returns an action result handler for specified result type.
	 * Returns <code>null</code> if result type is not previously registered.
	 */
	public ActionResult lookup(String resultType) {
		return results.get(resultType);
	}



	// ---------------------------------------------------------------- create

	/**
	 * Creates new {@link jodd.madvoc.result.ActionResult}.
	 */
	protected ActionResult createResult(Class<? extends ActionResult> actionResultClass) {
		try {
			return actionResultClass.newInstance();
		} catch (InstantiationException iex) {
			throw new MadvocException("Unable to create Madvoc action result '" + actionResultClass + "'.", iex);
		} catch (IllegalAccessException iaex) {
			throw new MadvocException("Not enough rights to create Madvoc action result '" + actionResultClass + "'.", iaex);
		}
	}
}
