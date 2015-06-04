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

package jodd.madvoc;

import jodd.madvoc.component.MadvocController;
import jodd.madvoc.injector.Target;
import jodd.exception.ExceptionUtil;
import jodd.madvoc.meta.Out;
import jodd.madvoc.result.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;


/**
 * Encapsulates single action invocation and acts as an action proxy.
 * It invokes all assigned action interceptors during action invocation and
 * specifies the result after action method invocation.
 */
public class ActionRequest {

	protected final MadvocController madvocController;
	protected final ActionConfig actionConfig;
	protected final String actionPath;
	protected HttpServletRequest servletRequest;
	protected HttpServletResponse servletResponse;
	protected Result result;

	protected final Target[] targets;
	protected final ActionWrapper[] executionArray;
	protected int executionIndex;

	protected Object action;
	protected Object actionResult;

	protected String nextActionPath;
	protected ActionRequest previousActionRequest;

	// ---------------------------------------------------------------- accessors

	/**
	 * Returns servlet request.
	 */
	public HttpServletRequest getHttpServletRequest() {
		return servletRequest;
	}

	/**
	 * Specifies new servlet request, in case of wrapping it.
	 */
	public void setHttpServletRequest(HttpServletRequest request) {
		this.servletRequest = request;
	}

	/**
	 * Returns servlet response.
	 */
	public HttpServletResponse getHttpServletResponse() {
		return servletResponse;
	}

	/**
	 * Specifies new servlet response, in case of wrapping it.
	 */
	public void setHttpServletResponse(HttpServletResponse response) {
		this.servletResponse = response;
	}

	/**
	 * Returns {@link ActionConfig action configuration}.
	 */
	public ActionConfig getActionConfig() {
		return actionConfig;
	}

	/**
	 * Returns action object.
	 */
	public Object getAction() {
		return action;
	}

	/**
	 * Returns action path.
	 */
	public String getActionPath() {
		return actionPath;
	}

	/**
	 * Returns next request string for action chaining.
	 */
	public String getNextActionPath() {
		return nextActionPath;
	}

	/**
	 * Specifies the next action path, that will be chained to current action request.
	 */
	public void setNextActionPath(String nextActionPath) {
		this.nextActionPath = nextActionPath;
	}

	/**
	 * Returns previous action request in chain, if there was one.
	 */
	public ActionRequest getPreviousActionRequest() {
		return previousActionRequest;
	}

	/**
	 * Sets previous action request in chain.
	 */
	public void setPreviousActionRequest(ActionRequest previousActionRequest) {
		this.previousActionRequest = previousActionRequest;
	}

	/**
	 * Returns result object if exist in action, otherwise returns <code>null</code>.
	 */
	public Result getResult() {
		return result;
	}

	/**
	 * Returns all injection targets.
	 */
	public Target[] getTargets() {
		return targets;
	}

	/**
	 * Returns action result object.
	 */
	public Object getActionResult() {
		return actionResult;
	}

	/**
	 * Sets action result object.
	 */
	public void setActionResult(Object actionResult) {
		this.actionResult = actionResult;
	}

	// ---------------------------------------------------------------- ctor

	/**
	 * Creates new action request and initializes it.
	 */
	public ActionRequest(
		MadvocController madvocController,
			String actionPath,
			ActionConfig actionConfig,
			Object action,
			HttpServletRequest servletRequest,
			HttpServletResponse servletResponse) {

		this.madvocController = madvocController;
		this.actionPath = actionPath;
		this.actionConfig = actionConfig;
		this.servletRequest = servletRequest;
		this.servletResponse = servletResponse;
		this.action = action;
		this.result = findResult();
		this.targets = makeTargets();

		this.executionIndex = 0;
		this.executionArray = createExecutionArray();
	}

	/**
	 * Creates execution array that will invoke all filters, actions and results
	 * in correct order.
	 */
	protected ActionWrapper[] createExecutionArray() {
		int totalInterceptors = (this.actionConfig.interceptors != null ? this.actionConfig.interceptors.length : 0);
		int totalFilters = (this.actionConfig.filters != null ? this.actionConfig.filters.length : 0);

		ActionWrapper[] executionArray = new ActionWrapper[totalFilters + 1 + totalInterceptors + 1];

		// filters

		int index = 0;

		if (totalFilters > 0) {
			System.arraycopy(actionConfig.filters, 0, executionArray, index, totalFilters);
			index += totalFilters;
		}

		// result is executed AFTER the action AND interceptors

		executionArray[index++] = new BaseActionWrapper() {
			public Object invoke(ActionRequest actionRequest) throws Exception {
				Object actionResult = actionRequest.invoke();

				ActionRequest.this.madvocController.render(ActionRequest.this, actionResult);

				return actionResult;
			}
		};

		// interceptors

		if (totalInterceptors > 0) {
			System.arraycopy(actionConfig.interceptors, 0, executionArray, index, totalInterceptors);
			index += totalInterceptors;
		}

		// action

		executionArray[index] = new BaseActionWrapper() {
			public Object invoke(ActionRequest actionRequest) throws Exception {
				actionResult = invokeActionMethod();
				return actionResult;
			}
		};

		return executionArray;
	}

	/**
	 * Returns result field value if such exist. If field exists
	 * and it's value is <code>null</code> it will be created.
	 */
	protected Result findResult() {
		Field resultField = actionConfig.resultField;
		if (resultField != null) {
			try {
				Result result = (Result) resultField.get(action);

				if (result == null) {
					result = (Result) resultField.getType().newInstance();
					resultField.set(action, result);
				}

				return result;
			} catch (Exception ignore) {
				return null;
			}
		}
		return null;
	}

	/**
	 * Joins action and parameters into one array of Targets.
	 */
	protected Target[] makeTargets() {
		if (actionConfig.hasArguments == false) {
			return new Target[] {new Target(action)};
		}

		ActionConfig.MethodParam[] methodParams = actionConfig.getMethodParams();
		Target[] target = new Target[methodParams.length + 1];

		target[0] = new Target(action);

		for (int i = 0; i < methodParams.length; i++) {
			ActionConfig.MethodParam mp = methodParams[i];

			Class type = mp.getType();

			Target t;

			if (mp.getAnnotationType() == null) {
				// parameter is NOT annotated
				t = new Target(createActionMethodArgument(type));
			}
			else if (mp.getAnnotationType() == Out.class) {
				// parameter is annotated with *only* OUT annotation
				// we need to create the output AND to save the type
				t = new Target(createActionMethodArgument(type), type);
			}
			else {
				// parameter is annotated with any IN annotation
				t = new Target(type) {
					@Override
					protected void createValueInstance() {
						value = createActionMethodArgument(type);
					}
				};
			}

			target[i + 1] = t;
		}
		return target;
	}

	/**
	 * Creates action method arguments.
	 */
	@SuppressWarnings({"unchecked", "NullArgumentToVariableArgMethod"})
	protected Object createActionMethodArgument(Class type) {
		try {
			if (type.getEnclosingClass() == null || Modifier.isStatic(type.getModifiers())) {
				// regular or static class
				Constructor ctor = type.getDeclaredConstructor(null);
				ctor.setAccessible(true);
				return ctor.newInstance();
			} else {
				// member class
				Constructor ctor = type.getDeclaredConstructor(type.getDeclaringClass());
				ctor.setAccessible(true);
				return ctor.newInstance(action);
			}
		} catch (Exception ex) {
			throw new MadvocException(ex);
		}
	}

	// ---------------------------------------------------------------- invoke

	/**
	 * Invokes the action and returns action result value object.
	 * Invokes all interceptors before and after action invocation.
	 */
	public Object invoke() throws Exception {
		return executionArray[executionIndex++].invoke(this);
	}

	/**
	 * Invokes action method after starting all interceptors.
	 * After method invocation, all interceptors will finish, in opposite order. 
	 */
	protected Object invokeActionMethod() throws Exception {
		Object[] params = extractParametersFromTargets();
		try {
			return actionConfig.actionClassMethod.invoke(action, params);
		} catch(InvocationTargetException itex) {
			throw ExceptionUtil.extractTargetException(itex);
		}
	}

	/**
	 * Collects all parameters from target into an array.
	 */
	protected Object[] extractParametersFromTargets() {
		if (targets == null) {
			return null;
		}

		Object[] values = new Object[targets.length - 1];

		for (int i = 1; i < targets.length; i++) {
			values[i - 1] = targets[i].getValue();
		}

		return values;
	}

}