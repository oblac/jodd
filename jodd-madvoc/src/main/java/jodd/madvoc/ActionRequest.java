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
import jodd.madvoc.config.ActionRuntime;
import jodd.madvoc.config.Targets;
import jodd.servlet.ServletUtil;
import jodd.util.StringPool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static jodd.exception.ExceptionUtil.unwrapThrowable;
import static jodd.exception.ExceptionUtil.wrapToException;


/**
 * Encapsulates single action invocation and acts as an action proxy.
 * It invokes all assigned action interceptors during action invocation and
 * specifies the result after action method invocation.
 */
public class ActionRequest {

	protected final MadvocController madvocController;
	protected final ActionRuntime actionRuntime;
	protected final String actionPath;
	protected final String[] actionPathChunks;
	protected HttpServletRequest servletRequest;
	protected HttpServletResponse servletResponse;

	protected final Targets targets;
	protected final ActionWrapper[] executionArray;
	protected int executionIndex;

	protected Object action;
	protected Object actionResult;

	protected String nextActionPath;

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
	public void bind(final HttpServletRequest request) {
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
	public void bind(final HttpServletResponse response) {
		this.servletResponse = response;
	}

	/**
	 * Returns {@link ActionRuntime action runtime} configuration.
	 */
	public ActionRuntime getActionRuntime() {
		return actionRuntime;
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
	public void setNextActionPath(final String nextActionPath) {
		this.nextActionPath = nextActionPath;
	}

	/**
	 * Returns all injection targets.
	 */
	public Targets getTargets() {
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
	public void bindActionResult(final Object actionResult) {
		this.actionResult = actionResult;
	}

	/**
	 * Returns chunks of action path. Action path is split on {@code /}. For example,
	 * the path {@code "/hello/world"} would return 2 chunks: {@code hello} and {@code world}.
	 */
	public String[] getActionPathChunks() {
		return actionPathChunks;
	}

	// ---------------------------------------------------------------- ctor

	/**
	 * Creates new action request and initializes it.
	 */
	public ActionRequest(
		final MadvocController madvocController,
		final String actionPath,
		final String[] actionPathChunks,
		final ActionRuntime actionRuntime,
		final Object action,
		final HttpServletRequest servletRequest,
		final HttpServletResponse servletResponse) {

		this.madvocController = madvocController;
		this.actionPath = actionPath;
		this.actionPathChunks = actionPathChunks;
		this.actionRuntime = actionRuntime;
		this.servletRequest = servletRequest;
		this.servletResponse = servletResponse;
		this.action = action;
		this.targets = new Targets(this);

		this.executionIndex = 0;
		this.executionArray = createExecutionArray();
	}

	/**
	 * Creates execution array that will invoke all filters, actions and results
	 * in correct order.
	 */
	protected ActionWrapper[] createExecutionArray() {
		int totalInterceptors = (this.actionRuntime.getInterceptors() != null ? this.actionRuntime.getInterceptors().length : 0);
		int totalFilters = (this.actionRuntime.getFilters() != null ? this.actionRuntime.getFilters().length : 0);

		ActionWrapper[] executionArray = new ActionWrapper[totalFilters + 1 + totalInterceptors + 1];

		// filters

		int index = 0;

		if (totalFilters > 0) {
			System.arraycopy(actionRuntime.getFilters(), 0, executionArray, index, totalFilters);
			index += totalFilters;
		}

		// result is executed AFTER the action AND interceptors

		executionArray[index++] = actionRequest -> {
			Object actionResult = actionRequest.invoke();

			ActionRequest.this.madvocController.render(ActionRequest.this, actionResult);

			return actionResult;
		};

		// interceptors

		if (totalInterceptors > 0) {
			System.arraycopy(actionRuntime.getInterceptors(), 0, executionArray, index, totalInterceptors);
			index += totalInterceptors;
		}

		// action

		executionArray[index] = actionRequest -> {
			actionResult = invokeActionMethod();
			return actionResult;
		};

		return executionArray;
	}

	// ---------------------------------------------------------------- invoke

	/**
	 * Invokes the action and returns action result value object.
	 * Invokes all interceptors before and after action invocation.
	 */
	public Object invoke() throws Exception {
		return executionArray[executionIndex++].apply(this);
	}

	/**
	 * Invokes action method after starting all interceptors.
	 * After method invocation, all interceptors will finish, in opposite order. 
	 */
	protected Object invokeActionMethod() throws Exception {
		if (actionRuntime.isActionHandlerDefined()) {
			actionRuntime.getActionHandler().handle(this);
			return null;
		}

		final Object[] params = targets.extractParametersValues();

		try {
			return actionRuntime.getActionClassMethod().invoke(action, params);
		} catch(InvocationTargetException itex) {
			throw wrapToException(unwrapThrowable(itex));
		}
	}

	// ---------------------------------------------------------------- special

	private String requestBody;

	/**
	 * Reads request body only once and returns it to user.
	 */
	public String readRequestBody() {
		if (requestBody == null) {
			try {
				requestBody = ServletUtil.readRequestBodyFromStream(getHttpServletRequest());
			} catch (IOException ioex) {
				requestBody = StringPool.EMPTY;
			}
		}
		return requestBody;
	}

}