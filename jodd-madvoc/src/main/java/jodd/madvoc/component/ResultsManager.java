// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.log.Logger;
import jodd.log.LoggerFactory;
import jodd.madvoc.MadvocException;
import jodd.madvoc.injector.Target;
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

	private static final Logger log = LoggerFactory.getLogger(ResultsManager.class);

	@PetiteInject
	protected ContextInjectorComponent contextInjectorComponent;

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

		boolean existingResult = false;

		if (resultType != null) {
			ActionResult existing = lookup(resultType);
			if (existing != null) {
				Class<? extends ActionResult> resultClass = result.getClass();
				Class<? extends ActionResult> existingClass = existing.getClass();
				if (existingClass.equals(resultClass) == false) {
					// existing class is different from current class
					if (resultClass.getPackage().equals(ActionResult.class.getPackage())) {
						Class<? extends ActionResult> temp = existingClass;
						existingClass = resultClass;
						resultClass = temp;
						result = lookup(resultClass);
						existingResult = true;
					}
					if ((existingClass.getPackage().equals(ActionResult.class.getPackage()) == false)) {
						// only throw exception if there are more then one replacement
						throw new MadvocException("Duplicate result: " + result);
					} else {
						if (log.isDebugEnabled()) {
							log.debug(existingClass.getSimpleName() + " result replaced with " + resultClass.getSimpleName());
						}
					}
				}
				allResults.remove(existingClass);	// remove existing as it will be replaced
			}
			stringResults.put(result.getResultType(), result);
		}

		allResults.put(result.getClass(), result);

		if (!existingResult) {
			initializeResult(result);
		}

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