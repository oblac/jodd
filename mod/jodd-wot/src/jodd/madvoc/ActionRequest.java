// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.madvoc.interceptor.ActionInterceptor;
import jodd.exception.ExceptionUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;


/**
 * Encapsulates single action invocation and acts as an action proxy.
 * It invokes all assigned action interceptors during action invocation and
 * specifies the result after action method invocation.
 */
public class ActionRequest {

	protected final ActionConfig config;
	protected HttpServletRequest serlvetRequest;
	protected HttpServletResponse servletResponse;

	protected final int totalInterceptors;
	protected int interceptorIndex;
	protected Object action;

	protected boolean executed;

	protected String nextActionPath;
	protected ActionRequest previousActionRequest;

	// ---------------------------------------------------------------- accessors

	public HttpServletRequest getHttpServletRequest() {
		return serlvetRequest;
	}

	public void setHttpServletRequest(HttpServletRequest request) {
		this.serlvetRequest = request;
	}

	public HttpServletResponse getHttpServletResponse() {
		return servletResponse;
	}

	public void setHttpServletResponse(HttpServletResponse response) {
		this.servletResponse = response;
	}

	public ActionConfig getActionConfig() {
		return config;
	}

	public Object getAction() {
		return action;
	}

	public String getActionPath() {
		return config.actionPath;
	}

	public boolean isExecuted() {
		return executed;
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

	public ActionRequest getPreviousActionRequest() {
		return previousActionRequest;
	}

	public void setPreviousActionRequest(ActionRequest previousActionRequest) {
		this.previousActionRequest = previousActionRequest;
	}

	// ---------------------------------------------------------------- ctor

	/**
	 * Creates new action request and action object.
	 */
	public ActionRequest(ActionConfig config, Object action, HttpServletRequest serlvetRequest, HttpServletResponse servletResponse) {
		this.config = config;
		this.serlvetRequest = serlvetRequest;
		this.servletResponse = servletResponse;
		totalInterceptors = (this.config.interceptors != null ? this.config.interceptors.length : 0);
		interceptorIndex = 0;
		this.action = action;
	}


	// ---------------------------------------------------------------- invoke

	/**
	 * Invokes the action and returns action result value object.
	 * Invokes all interceptors before and after action invocation.
	 */
	public Object invoke() throws Exception {
		if (executed == true) {
			throw new MadvocException("Action '" + config.actionPath + "' has already been invoked.");
		}
		// interceptors
		if (interceptorIndex < totalInterceptors) {
			ActionInterceptor interceptor = config.interceptors[interceptorIndex];
			interceptorIndex++;
			return interceptor.intercept(this);
		}

		// action
		Object actionInvocationResult = invokeAction();
		executed = true;
		return actionInvocationResult;
	}

	/**
	 * Invokes action method after starting all interceptors.
	 * After method invocation, all interceptors will finish, in opposite order. 
	 */
	protected Object invokeAction() throws Exception {
		try {
			return config.actionMethod.invoke(action);
		} catch(InvocationTargetException itex) {
			throw ExceptionUtil.exctractTargetException(itex);
		}
	}


}