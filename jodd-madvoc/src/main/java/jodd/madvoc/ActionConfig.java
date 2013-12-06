// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.madvoc.filter.ActionFilter;
import jodd.madvoc.interceptor.ActionInterceptor;

import java.lang.reflect.Method;

/**
 * Action configuration and shared run-time data, used internally.
 */
public class ActionConfig {

	// configuration
	public final Class actionClass;
	public final Method actionClassMethod;
	public final String actionPath;
	public final String actionMethod;
	public final String actionPathExtension;
	public final boolean pathEndsWithExtension;
	public final String resultType;
	//public final Class<?>[] actionParamTypes;

	// run-time data
	protected ActionConfigSet actionConfigSet;
	public boolean initialized;
	public final ActionFilter[] filters;
	public final ActionInterceptor[] interceptors;

	public ActionConfig(
			Class actionClass,
			Method actionClassMethod,
			ActionFilter[] filters,
			ActionInterceptor[] interceptors,
			String actionPath,
			String actionMethod,
			String actionPathExtension,
	        String resultType)
	{

		this.actionClass = actionClass;
		this.actionClassMethod = actionClassMethod;
		this.actionPath = actionPath;
		this.actionMethod = actionMethod;
		this.actionPathExtension = actionPathExtension;

		this.filters = filters;
		this.interceptors = interceptors;

		this.pathEndsWithExtension = actionPathExtension != null && actionPath.endsWith('.' + actionPathExtension);
		this.resultType = resultType;

//		Class<?>[] paramTypes = actionMethod.getParameterTypes();
//		this.actionParamTypes = paramTypes.length != 0 ? paramTypes : null;
	}

	// ---------------------------------------------------------------- getters

	/**
	 * Returns action class.
	 */
	public Class getActionClass() {
		return actionClass;
	}

	/**
	 * Returns action class method.
	 */
	public Method getActionClassMethod() {
		return actionClassMethod;
	}

	/**
	 * Returns action path.
	 */
	public String getActionPath() {
		return actionPath;
	}

	/**
	 * Returns action path extension.
	 */
	public String getActionPathExtension() {
		return actionPathExtension;
	}

	/**
	 * Returns <code>true</code> if {@link #getActionPath() action path}
	 * ends with {@link #getActionPathExtension() action path extension}.
	 */
	public boolean isPathEndsWithExtension() {
		return pathEndsWithExtension;
	}

	/**
	 * Returns action method.
	 */
	public String getActionMethod() {
		return actionMethod;
	}

	/**
	 * Returns <code>true</code> if class is initialized.
	 */
	public boolean isInitialized() {
		return initialized;
	}

	/**
	 * Marks configuration as initialized.
	 */
	public void initialized() {
		initialized = true;
	}

	/**
	 * Returns interceptor instances.
	 */
	public ActionInterceptor[] getInterceptors() {
		return interceptors;
	}

//	public Class<?>[] getActionParamTypes() {
//		return actionParamTypes;
//	}

	public ActionConfigSet getActionConfigSet() {
		return actionConfigSet;
	}

	/**
	 * Returns result type or <code>null</code> if not specified.
	 */
	public String getResultType() {
		return resultType;
	}

	// ---------------------------------------------------------------- to string

	/**
	 * Returns action string in form 'actionClass#actionMethod'.
	 */
	public String getActionString() {
		return actionClass.getName() + '#' + actionClassMethod.getName();
	}

	@Override
	public String toString() {
		return "action: " + actionPath + (actionMethod == null ? "" : '#' + actionMethod) + "  -->  " + getActionString();
	}

}
