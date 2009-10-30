// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.madvoc.interceptor.ActionInterceptor;
import java.lang.reflect.Method;

/**
 * Action configuration and shared run-time data, used internally.
 */
public class ActionConfig {

	// configuration
	public final Class actionClass;
	public final Method actionMethod;
	public final String actionPath;
	public final Class<? extends ActionInterceptor>[] interceptorClasses;
	public final Class<?>[] actionParamTypes;

	public ActionConfig(Class actionClass, Method actionMethod, Class<? extends ActionInterceptor>[] interceptors, String actionPath) {
		this.actionClass = actionClass;
		this.actionMethod = actionMethod;
		this.actionPath = actionPath;
		this.interceptorClasses = interceptors;
		Class<?>[] paramTypes = actionMethod.getParameterTypes();
		this.actionParamTypes = paramTypes.length != 0 ? paramTypes : null;
	}

	// run-time data
	public boolean initialized;
	public ActionInterceptor[] interceptors;

	// ---------------------------------------------------------------- getters

	public Class getActionClass() {
		return actionClass;
	}

	public Method getActionMethod() {
		return actionMethod;
	}

	public String getActionPath() {
		return actionPath;
	}

	public Class<? extends ActionInterceptor>[] getInterceptorClasses() {
		return interceptorClasses;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void initialized() {
		initialized = true;
	}

	public ActionInterceptor[] getInterceptors() {
		return interceptors;
	}

	public Class<?>[] getActionParamTypes() {
		return actionParamTypes;
	}

	// ---------------------------------------------------------------- to string

	/**
	 * Returns action string in form 'actionClass#actionMethod'.
	 */
	public String getActionString() {
		return actionClass.getName() + '#' + actionMethod.getName();
	}

	@Override
	public String toString() {
		return "action: " + actionPath + "  ->  " + getActionString();
	}
}
