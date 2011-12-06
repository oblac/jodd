// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

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
	public final Class<? extends ActionInterceptor>[] interceptorClasses;
	//public final Class<?>[] actionParamTypes;

	// run-time data
	protected ActionConfigSet actionConfigSet;
	public boolean initialized;
	public ActionInterceptor[] interceptors;

	public ActionConfig(
			Class actionClass,
			Method actionClassMethod,
			Class<? extends ActionInterceptor>[] interceptors,
			String actionPath,
			String actionMethod,
			String actionPathExtension) {

		this.actionClass = actionClass;
		this.actionClassMethod = actionClassMethod;
		this.actionPath = actionPath;
		this.actionMethod = actionMethod;
		this.actionPathExtension = actionPathExtension;
		this.interceptorClasses = interceptors;

		this.pathEndsWithExtension = actionPathExtension != null && actionPath.endsWith('.' + actionPathExtension);
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
	 * Returns interceptors classes.
	 */
	public Class<? extends ActionInterceptor>[] getInterceptorClasses() {
		return interceptorClasses;
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
