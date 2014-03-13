// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.madvoc.component.MadvocController;
import jodd.madvoc.filter.ActionFilter;
import jodd.madvoc.interceptor.ActionInterceptor;
import jodd.exception.ExceptionUtil;
import jodd.madvoc.result.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;


/**
 * Encapsulates single action invocation and acts as an action proxy.
 * It invokes all assigned action interceptors during action invocation and
 * specifies the result after action method invocation.
 */
public class ActionRequest {

	protected final MadvocController madvocController;
	protected final ActionConfig config;
	protected final String actionPath;
	protected HttpServletRequest servletRequest;
	protected HttpServletResponse servletResponse;
	protected Result result;

	protected final Object[] params;
	protected final int totalInterceptors;
	protected int interceptorIndex;
	protected int filterIndex;
	protected int totalFilters;

	protected int execState;		// execution state

	protected Object action;

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
		return config;
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

	// ---------------------------------------------------------------- ctor

	/**
	 * Creates new action request and action object.
	 */
	public ActionRequest(MadvocController madvocController, String actionPath, ActionConfig config, Object action, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
		this.madvocController = madvocController;
		this.actionPath = actionPath;
		this.config = config;
		this.servletRequest = servletRequest;
		this.servletResponse = servletResponse;
		totalInterceptors = (this.config.interceptors != null ? this.config.interceptors.length : 0);
		interceptorIndex = 0;
		totalFilters = (this.config.filters != null ? this.config.filters.length : 0);
		filterIndex = 0;
		execState = 0;
		this.action = action;
		this.result = findResult();
		this.params = createActionMethodArguments();
	}

	/**
	 * Returns result field value if such exist. If field exists
	 * and it's value is <code>null</code> it will be created.
	 */
	protected Result findResult() {
		Field resultField = config.resultField;
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
	 * Creates action method arguments.
	 */
	protected Object[] createActionMethodArguments() {
		if (!config.hasArguments) {
			return null;
		}
		Class[] types = config.getActionClassMethod().getParameterTypes();

		Object[] params = new Object[types.length];

		for (int i = 0; i < params.length; i++) {
			Class type = types[i];

			try {
				if (type.getEnclosingClass() == null) {
					// static class
					params[i] = type.newInstance();
				} else {
					// member class
					Constructor ctor = type.getDeclaredConstructor(config.getActionClass());
					params[i] = ctor.newInstance(action);
				}
			} catch (Exception ex) {
				throw new MadvocException(ex);
			}
		}
		return params;
	}

	// ---------------------------------------------------------------- invoke

	/**
	 * Invokes the action and returns action result value object.
	 * Invokes all interceptors before and after action invocation.
	 */
	public Object invoke() throws Exception {
		if (execState >= 2) {
			throw new MadvocException("Action already invoked: " + config.actionPath);
		}

		if (execState == 0) {
			// filters
			if (filterIndex < totalFilters) {
				ActionFilter filter = config.filters[filterIndex];
				filterIndex++;
				return filter.invoke(this);
			}
		}

		execState = 1;

		Object actionResult = invokeAction();

		if (execState == 2) {
			// all interceptor finished the job
			if (interceptorIndex > 0) {
				interceptorIndex--;
			} else {
				madvocController.render(this, actionResult);
				execState = 3;
			}
		} else if (execState == 1) {
			// some interceptor interrupted the flow
			if (interceptorIndex > 1) {
				interceptorIndex--;
			} else {
				madvocController.render(this, actionResult);
				execState = 3;
			}
		}

		return actionResult;
	}

	/**
	 * Invokes all {@link jodd.madvoc.interceptor.ActionInterceptor action interceptors}
	 * and the action method, returns action result object.
	 */
	protected Object invokeAction() throws Exception {
		// interceptors
		if (interceptorIndex < totalInterceptors) {
			ActionInterceptor interceptor = config.interceptors[interceptorIndex];
			interceptorIndex++;
			return interceptor.invoke(this);
		}

		// action
		execState = 2;

		return invokeActionMethod();
	}

	/**
	 * Invokes action method after starting all interceptors.
	 * After method invocation, all interceptors will finish, in opposite order. 
	 */
	protected Object invokeActionMethod() throws Exception {
		try {
			return config.actionClassMethod.invoke(action, params);
		} catch(InvocationTargetException itex) {
			throw ExceptionUtil.extractTargetException(itex);
		}
	}

}