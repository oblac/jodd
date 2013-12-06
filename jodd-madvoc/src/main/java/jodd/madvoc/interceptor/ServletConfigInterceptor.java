// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.interceptor;

import jodd.madvoc.ActionRequest;
import jodd.madvoc.ScopeType;
import jodd.madvoc.component.MadvocContextInjector;
import jodd.madvoc.component.ServletContextInjector;
import jodd.madvoc.injector.ActionPathMacroInjector;
import jodd.madvoc.injector.RequestScopeInjector;
import jodd.madvoc.injector.SessionScopeInjector;
import jodd.madvoc.component.MadvocConfig;
import jodd.madvoc.meta.In;
import jodd.servlet.ServletUtil;
import jodd.servlet.upload.MultipartRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Configures actions and applies some servlet configuration prior action execution.
 * This interceptor does the following:
 * <ul>
 * <li>uses multi-part request if needed</li>
 * <li>performs the injection (using either default or specified injector)</li>
 * <li>invokes the action</li>
 * <li>performs the outjection.</li>
 * </ul>
 */
public class ServletConfigInterceptor extends BaseActionInterceptor {

	@In(scope = ScopeType.CONTEXT)
	protected MadvocConfig madvocConfig;

	@In(scope = ScopeType.CONTEXT)
	protected ServletContextInjector servletContextInjector;

	@In(scope = ScopeType.CONTEXT)
	protected MadvocContextInjector madvocContextInjector;

	protected RequestScopeInjector requestScopeInjector;
	protected SessionScopeInjector sessionScopeInjector;
	protected ActionPathMacroInjector actionPathMacroInjector;

	@Override
	public void init() {
		requestScopeInjector = new RequestScopeInjector(madvocConfig);
		sessionScopeInjector = new SessionScopeInjector();
		actionPathMacroInjector = new ActionPathMacroInjector();
	}

	/**
	 * {@inheritDoc}
	 */
	public Object intercept(ActionRequest actionRequest) throws Exception {
		HttpServletRequest servletRequest = actionRequest.getHttpServletRequest();

		// detect multipart request
		if (ServletUtil.isMultipartRequest(servletRequest)) {
			servletRequest = new MultipartRequestWrapper(servletRequest, madvocConfig.getFileUploadFactory(), madvocConfig.getEncoding());
			actionRequest.setHttpServletRequest(servletRequest);
		}

		// do it
		inject(actionRequest);
		Object result = actionRequest.invoke();
		outject(actionRequest);
		return result;
	}

	/**
	 * Performs injection.
	 */
	protected void inject(ActionRequest actionRequest) {
		Object target = actionRequest.getAction();
		HttpServletRequest servletRequest = actionRequest.getHttpServletRequest();
		HttpServletResponse servletResponse = actionRequest.getHttpServletResponse();

		madvocContextInjector.injectMadvocContext(target);

		// no need to inject madvoc params, as this can be slow
		// and its better to use some single data object instead
		//madvocContextInjector.injectMadvocParams(target);

		servletContextInjector.injectContext(target, servletRequest, servletResponse);

		sessionScopeInjector.inject(target, servletRequest);

		requestScopeInjector.prepare(servletRequest);
		requestScopeInjector.inject(target, servletRequest);

		actionPathMacroInjector.inject(target, actionRequest);
	}

	/**
	 * Performs outjection.
	 */
	protected void outject(ActionRequest actionRequest) {
		Object target = actionRequest.getAction();
		HttpServletRequest servletRequest = actionRequest.getHttpServletRequest();
		HttpServletResponse servletResponse = actionRequest.getHttpServletResponse();

		madvocContextInjector.outjectMadvocContext(target);
		servletContextInjector.outjectContext(target, servletRequest, servletResponse);

		sessionScopeInjector.outject(target, servletRequest);

		requestScopeInjector.outject(target, servletRequest);
	}

}
