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

package jodd.madvoc.component;

import jodd.madvoc.ActionConfig;
import jodd.madvoc.ActionRequest;
import jodd.madvoc.MadvocException;
import jodd.madvoc.result.ActionResult;
import jodd.petite.meta.PetiteInject;
import jodd.servlet.ServletUtil;
import jodd.log.Logger;
import jodd.log.LoggerFactory;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Madvoc controller invokes actions for action path and renders action results.
 * It also builds action objects and result paths. It handles initialization of
 * interceptors and results.
 */
public class MadvocController {

	private static final Logger log = LoggerFactory.getLogger(MadvocController.class);

	@PetiteInject
	protected MadvocConfig madvocConfig;

	@PetiteInject
	protected ActionsManager actionsManager;

	@PetiteInject
	protected ActionPathRewriter actionPathRewriter;

	@PetiteInject
	protected ResultsManager resultsManager;

	protected ServletContext applicationContext;

	protected Executor executor;

	/**
	 * Initializes controller by providing application context.
	 * Application context can be <code>null</code>
	 * if application is not started in web environment (eg tests).
	 */
	public void init(ServletContext servletContext) {
		this.applicationContext = servletContext;

		if (actionsManager.isAsyncModeOn()) {
			executor = createAsyncExecutor();
		}
	}

	/**
	 * Creates async executor.
	 */
	protected Executor createAsyncExecutor() {
		MadvocConfig.AsyncConfig asyncConfig = madvocConfig.getAsyncConfig();

		return new ThreadPoolExecutor(
				asyncConfig.getCorePoolSize(),
				asyncConfig.getMaximumPoolSize(),
				asyncConfig.getKeepAliveTimeMillis(),
				TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>(asyncConfig.getQueueCapacity()));
	}

	/**
	 * Returns application context set during the initialization.
	 */
	public ServletContext getApplicationContext() {
		return applicationContext;
	}

	// ---------------------------------------------------------------- invoke


	/**
	 * Invokes action registered to provided action path, Provides action chaining, by invoking the next action request.
	 * Returns <code>null</code> if action path is consumed and has been invoked by this controller; otherwise
	 * the action path string is returned (it might be different than original one, provided in arguments).
	 * On first invoke, initializes the action configuration before further proceeding.
	 */
	public String invoke(String actionPath, HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws Exception {
		ActionRequest actionRequest = null;

		boolean characterEncodingSet = false;

		while (actionPath != null) {
			if (log.isDebugEnabled()) {
				log.debug("Action path: " + actionPath);
			}

			// build action path
			String httpMethod = servletRequest.getMethod().toUpperCase();

			actionPath = actionPathRewriter.rewrite(servletRequest, actionPath, httpMethod);

			// resolve action configuration
			ActionConfig actionConfig = actionsManager.lookup(actionPath, httpMethod);
			if (actionConfig == null) {
				return actionPath;
			}
			if (log.isDebugEnabled()) {
				log.debug("Invoking action path '" + actionPath + "' using " + actionConfig.actionClass.getSimpleName());
			}

			// set character encoding
			if (!characterEncodingSet && madvocConfig.isApplyCharacterEncoding()) {

				String encoding = madvocConfig.getEncoding();

				if (encoding != null) {
					servletRequest.setCharacterEncoding(encoding);
					servletResponse.setCharacterEncoding(encoding);
				}

				characterEncodingSet = true;
			}

			// create action object
			Object action = createAction(actionConfig.actionClass);

			// create action request
			ActionRequest previousRequest = actionRequest;
			actionRequest = createActionRequest(actionPath, actionConfig, action, servletRequest, servletResponse);
			actionRequest.setPreviousActionRequest(previousRequest);

			// invoke and render
			if (actionConfig.isAsync()) {
				AsyncContext asyncContext = servletRequest.startAsync();
				executor.execute(new ActionRequestInvoker(asyncContext, actionRequest));
			} else {
				actionRequest.invoke();
			}

			actionPath = actionRequest.getNextActionPath();
		}
		return null;
	}

	/**
	 * Async request invoker.
	 */
	public static class ActionRequestInvoker implements Runnable {

		private final ActionRequest actionRequest;
		private final AsyncContext asyncContext;

		public ActionRequestInvoker(AsyncContext asyncContext, ActionRequest actionRequest) {
			this.actionRequest = actionRequest;
			this.asyncContext = asyncContext;
		}

		public void run() {
			try {
				if (log.isDebugEnabled()) {
					log.debug("Async call to: " + actionRequest);
				}
				actionRequest.invoke();
			} catch (Exception ex) {
				log.error("Invoking action path failed: " , ex);
			} finally {
				asyncContext.complete();
			}
		}
	}


	// ---------------------------------------------------------------- render

	/**
	 * Invokes a result after the action invocation.
	 * <p>
	 * Results may be objects that specify which action result will be used
	 * to render the result.
	 * <p>
	 * Result value may consist of two parts: type and value. Result type is optional and, if exists, it is separated
	 * by semi-colon from the value. If type is not specified
	 * then the default result type if still not defined. Result type defines which
	 * {@link ActionResult} should be used for rendering the value.
	 * <p>
	 * Result value is first checked against aliased values. Then, it is resolved and then passed
	 * to the founded {@link ActionResult}.
	 *
	 * @see ActionResult#render(jodd.madvoc.ActionRequest, Object)
	 */
	@SuppressWarnings("unchecked")
	public void render(ActionRequest actionRequest, Object resultObject) throws Exception {
		ActionResult actionResult = resultsManager.lookup(actionRequest, resultObject);

		if (actionResult == null) {
			throw new MadvocException("Action result not found");
		}

		if (madvocConfig.isPreventCaching()) {
			ServletUtil.preventCaching(actionRequest.getHttpServletResponse());
		}

		actionResult.render(actionRequest, actionRequest.getActionResult());
	}

	// ---------------------------------------------------------------- create

	/**
	 * Creates new action object from {@link ActionConfig} using default constructor.
	 */
	protected Object createAction(Class actionClass) {
		try {
			return actionClass.newInstance();
		} catch (Exception ex) {
			throw new MadvocException("Invalid Madvoc action", ex);
		}
	}

	/**
	 * Creates new action request.
	 * @param actionPath		action path
	 * @param actionConfig		action configuration
	 * @param action			action object
	 * @param servletRequest	http request
	 * @param servletResponse	http response
	 * @return action request
	 */
	protected ActionRequest createActionRequest(
			String actionPath,
			ActionConfig actionConfig,
			Object action,
			HttpServletRequest servletRequest,
			HttpServletResponse servletResponse) {

		return new ActionRequest(this, actionPath, actionConfig, action, servletRequest, servletResponse);
	}

}