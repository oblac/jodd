// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.interceptor;

import jodd.madvoc.ActionRequest;
import jodd.madvoc.ScopeType;
import jodd.madvoc.injector.SessionScopeInjector;
import jodd.madvoc.injector.RequestScopeInjector;
import jodd.madvoc.injector.ContextInjector;
import jodd.madvoc.meta.In;
import jodd.madvoc.component.MadvocConfig;
import jodd.madvoc.component.InjectorsManager;
import jodd.servlet.ServletUtil;
import jodd.servlet.upload.MultipartRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Configures actions and applies some servlet configuration prior action execution.
 * This interceptor does the following:
 * <ul>
 * <li>uses multi-part request if needed
 * <li>performs the injection (using either default or specified injector)
 * <li>invokes the action
 * <li>performs the outjection.
 * </ul>
 */
public class ServletConfigInterceptor extends ActionInterceptor {

	@In(scope = ScopeType.CONTEXT)
	protected MadvocConfig madvocConfig;

	@In(scope = ScopeType.CONTEXT)
	protected InjectorsManager injectorsManager;

	protected RequestScopeInjector requestScopeInjector;
	protected SessionScopeInjector sessionScopeInjector;
	protected ContextInjector contextInjector;

	@Override
	public void init() {
		requestScopeInjector = injectorsManager.getRequestScopeInjector();
		sessionScopeInjector = injectorsManager.getSessionScopeInjector();
		contextInjector = injectorsManager.getContextInjector();
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object intercept(ActionRequest actionRequest) throws Exception {
		HttpServletRequest servletRequest = actionRequest.getHttpServletRequest();
		HttpServletResponse serlvetResponse = actionRequest.getHttpServletResponse();

		// detect multipart request
		if (ServletUtil.isMultipartRequest(servletRequest)) {
			servletRequest = new MultipartRequestWrapper(servletRequest, madvocConfig.getFileUploadFactory(), madvocConfig.getEncoding());
			actionRequest.setHttpServletRequest(servletRequest);
		}

		// get injector
		Object target = actionRequest.getAction();
		inject(target, servletRequest, serlvetResponse);
		Object result = actionRequest.invoke();
		outject(target, servletRequest);
		return result;
	}

	/**
	 * Performs injection.
	 */
	protected void inject(Object target, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
		contextInjector.inject(target, servletRequest, servletResponse);
		sessionScopeInjector.inject(target, servletRequest);
		requestScopeInjector.inject(target, servletRequest);
	}

	/**
	 * Performs outjection.
	 */
	protected void outject(Object target, HttpServletRequest servletRequest) {
		contextInjector.outject(target, servletRequest);
		sessionScopeInjector.outject(target, servletRequest);
		requestScopeInjector.outject(target, servletRequest);
	}

}
