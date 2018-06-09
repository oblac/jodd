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

package jodd.madvoc.config;

import jodd.madvoc.ActionHandler;
import jodd.madvoc.filter.ActionFilter;
import jodd.madvoc.interceptor.ActionInterceptor;
import jodd.madvoc.result.ActionResult;

import java.lang.reflect.Method;

/**
 * Action runtime configuration and shared run-time data, used internally.
 */
public class ActionRuntime {

	// configuration
	private final ActionHandler actionHandler;
	private final Class actionClass;
	private final Method actionClassMethod;
	private final Class<? extends ActionResult> actionResult;
	private final Class<? extends ActionResult> defaultActionResult;
	private final String actionPath;
	private final String actionMethod;
	private final String resultBasePath;
	private final boolean async;
	private final boolean auth;

	// scope data
	private final ScopeData scopeData;
	private final MethodParam[] methodParams;

	private final boolean hasArguments;

	// run-time data
	private RouteChunk routeChunk;
	private final ActionFilter[] filters;
	private final ActionInterceptor[] interceptors;

	public ActionRuntime(
			final ActionHandler actionHandler,
			final Class actionClass,
			final Method actionClassMethod,
			final ActionFilter[] filters,
			final ActionInterceptor[] interceptors,
			final ActionDefinition actionDefinition,
			final Class<? extends ActionResult> actionResult,
			final Class<? extends ActionResult> defaultActionResult,
			final boolean async,
			final boolean auth,
			final ScopeData scopeData,
			final MethodParam[] methodParams
	) {
		this.actionHandler = actionHandler;
		this.actionClass = actionClass;
		this.actionClassMethod = actionClassMethod;
//		this.actionClassMethod = methodOfDeclaredClass(actionClass, actionClassMethod);
		this.actionPath = actionDefinition.actionPath();
		this.actionMethod = actionDefinition.actionMethod() == null ? null : actionDefinition.actionMethod().toUpperCase();
		this.resultBasePath = actionDefinition.resultBasePath();
		this.hasArguments = actionClassMethod != null && (actionClassMethod.getParameterTypes().length != 0);
		this.actionResult = actionResult;
		this.defaultActionResult = defaultActionResult;
		this.async = async;
		this.auth = auth;

		this.scopeData = scopeData;

		this.filters = filters;
		this.interceptors = interceptors;
		this.methodParams = methodParams;
	}

/*

	/**
	 * When wrapper proxy is used, the method will not belong to a type.
	 * This method finds the corresponding method in the type.
	private Method methodOfDeclaredClass(final Class type, final Method method) {
		if (method.getDeclaringClass() == type) {
			return method;
		}

		final Method[] allMethods = type.getDeclaredMethods();
		for (final Method newMethod : allMethods) {
			if (!newMethod.getName().equals(method.getName())) {
				continue;
			}
			if (!newMethod.getReturnType().equals(method.getReturnType())) {
				continue;
			}
			if (!Arrays.equals(newMethod.getParameterTypes(), method.getParameterTypes())) {
				continue;
			}
			return newMethod;
		}
		return method;
	}
*/

	// ---------------------------------------------------------------- getters

	/**
	 * Returns {@code true} if action handler is defined.
	 */
	public boolean isActionHandlerDefined() {
		return actionHandler != null;
	}

	/**
	 * Returns action handler.
	 */
	public ActionHandler getActionHandler() {
		return actionHandler;
	}

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
	 * Returns action method.
	 */
	public String getActionMethod() {
		return actionMethod;
	}

	/**
	 * Returns action result base path.
	 */
	public String getResultBasePath() {
		return resultBasePath;
	}

	/**
	 * Returns interceptor instances.
	 */
	public ActionInterceptor[] getInterceptors() {
		return interceptors;
	}

	/**
	 * Returns filters instances.
	 */
	public ActionFilter[] getFilters() {
		return filters;
	}

	/**
	 * Returns <code>true</code> if action is asynchronous.
	 */
	public boolean isAsync() {
		return async;
	}

	public boolean isAuthenticated() {
		return auth;
	}

	/**
	 * Returns method parameters information, or <code>null</code> if method has no params.
	 */
	public MethodParam[] getMethodParams() {
		return methodParams;
	}

	/**
	 * Returns action result class that will render the result.
	 * may be <code>null</code>.
	 */
	public Class<? extends ActionResult> getActionResult() {
		return actionResult;
	}

	/**
	 * Returns default action result.
	 */
	public Class<? extends ActionResult> getDefaultActionResult() {
		return defaultActionResult;
	}

	/**
	 * Returns {@code true} if action has arguments.
	 */
	public boolean hasArguments() {
		return hasArguments;
	}

	/**
	 * Returns scope data.
	 */
	public ScopeData getScopeData() {
		return scopeData;
	}

	// ---------------------------------------------------------------- bind

	/**
	 * Binds a route chunk to this configuration.
	 */
	public void bind(final RouteChunk routeChunk) {
		this.routeChunk = routeChunk;
	}

	/**
	 * Returns route chunk associated with this configuration.
	 */
	public RouteChunk getRouteChunk() {
		return routeChunk;
	}

	// ---------------------------------------------------------------- to string

	/**
	 * Returns action string in form 'actionClass#actionMethod'.
	 */
	public String createActionString() {
		if (actionHandler != null) {
			return actionHandler.getClass().getName();
		}
		String className = actionClass.getName();

		final int ndx = className.indexOf("$$");

		if (ndx != -1) {
			className = className.substring(0, ndx);
		}

		return className + '#' + actionClassMethod.getName();
	}

	@Override
	public String toString() {
		return "action: " + actionPath + (actionMethod == null ? "" : '#' + actionMethod) + "  -->  " + createActionString();
	}

}