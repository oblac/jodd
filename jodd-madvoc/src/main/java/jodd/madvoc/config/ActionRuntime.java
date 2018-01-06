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

import jodd.madvoc.ActionConfig;
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
	private final String actionPath;
	private final String actionMethod;
	private final String resultBasePath;
	private final boolean async;

	// scope data information matrix: [scope-type][target-index]
	private final ScopeData[][] scopeData;
	private final MethodParam[] methodParams;

	private final boolean hasArguments;

	// run-time data
	private RouteChunk routeChunk;
	private final ActionFilter[] filters;
	private final ActionInterceptor[] interceptors;
	private final ActionConfig actionConfig;

	public ActionRuntime(
			ActionHandler actionHandler,
			Class actionClass,
			Method actionClassMethod,
			ActionFilter[] filters,
			ActionInterceptor[] interceptors,
			ActionDefinition actionDefinition,
			Class<? extends ActionResult> actionResult,
			boolean async,
			ScopeData[][] scopeData,
			MethodParam[] methodParams,
			ActionConfig actionConfig
			)
	{
		this.actionHandler = actionHandler;
		this.actionClass = actionClass;
		this.actionClassMethod = actionClassMethod;
		this.actionPath = actionDefinition.actionPath();
		this.actionMethod = actionDefinition.actionMethod() == null ? null : actionDefinition.actionMethod().toUpperCase();
		this.resultBasePath = actionDefinition.resultBasePath();
		this.hasArguments = actionClassMethod != null && (actionClassMethod.getParameterTypes().length != 0);
		this.actionResult = actionResult;
		this.async = async;

		this.scopeData = scopeData;

		this.filters = filters;
		this.interceptors = interceptors;
		this.methodParams = methodParams;
		this.actionConfig = actionConfig;
	}

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
	public ActionHandler actionHandler() {
		return actionHandler;
	}

	/**
	 * Returns action class.
	 */
	public Class actionClass() {
		return actionClass;
	}

	/**
	 * Returns action class method.
	 */
	public Method actionClassMethod() {
		return actionClassMethod;
	}

	/**
	 * Returns action path.
	 */
	public String actionPath() {
		return actionPath;
	}

	/**
	 * Returns action method.
	 */
	public String actionMethod() {
		return actionMethod;
	}

	/**
	 * Returns action result base path.
	 */
	public String resultBasePath() {
		return resultBasePath;
	}

	/**
	 * Returns interceptor instances.
	 */
	public ActionInterceptor[] interceptors() {
		return interceptors;
	}

	/**
	 * Returns filters instances.
	 */
	public ActionFilter[] filters() {
		return filters;
	}

	/**
	 * Returns <code>true</code> if action is asynchronous.
	 */
	public boolean async() {
		return async;
	}

	/**
	 * Returns method parameters information, or <code>null</code> if method has no params.
	 */
	public MethodParam[] methodParams() {
		return methodParams;
	}

	/**
	 * Returns action result class that will render the result.
	 * may be <code>null</code>.
	 */
	public Class<? extends ActionResult> actionResult() {
		return actionResult;
	}

	/**
	 * Returns {@code true} if action has arguments.
	 */
	public boolean hasArguments() {
		return hasArguments;
	}

	public ScopeData[][] scopeData() {
		return scopeData;
	}

	public ActionConfig actionConfig() {
		return actionConfig;
	}

	// ---------------------------------------------------------------- bind

	/**
	 * Binds a route chunk to this configuration.
	 */
	public void bind(RouteChunk routeChunk) {
		this.routeChunk = routeChunk;
	}

	/**
	 * Returns route chunk associated with this configuration.
	 */
	public RouteChunk routeChunk() {
		return routeChunk;
	}

	// ---------------------------------------------------------------- to string

	/**
	 * Returns action string in form 'actionClass#actionMethod'.
	 */
	public String actionString() {
		if (actionHandler != null) {
			return actionHandler.getClass().getName();
		}
		String className = actionClass.getName();

		int ndx = className.indexOf("$$");

		if (ndx != -1) {
			className = className.substring(0, ndx);
		}

		return className + '#' + actionClassMethod.getName();
	}

	@Override
	public String toString() {
		return "action: " + actionPath + (actionMethod == null ? "" : '#' + actionMethod) + "  -->  " + actionString();
	}

}