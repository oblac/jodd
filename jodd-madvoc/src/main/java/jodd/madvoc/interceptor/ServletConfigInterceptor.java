// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.interceptor;

import jodd.madvoc.ActionRequest;
import jodd.madvoc.ScopeType;
import jodd.madvoc.component.MadvocContextInjector;
import jodd.madvoc.component.ScopeDataResolver;
import jodd.madvoc.component.ServletContextInjector;
import jodd.madvoc.injector.ActionPathMacroInjector;
import jodd.madvoc.injector.RequestScopeInjector;
import jodd.madvoc.injector.SessionScopeInjector;
import jodd.madvoc.component.MadvocConfig;
import jodd.madvoc.meta.In;
import jodd.servlet.ServletUtil;
import jodd.servlet.upload.MultipartRequestWrapper;

import javax.servlet.http.HttpServletRequest;

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

	@In(scope = ScopeType.CONTEXT)
	protected ScopeDataResolver scopeDataResolver;

	protected RequestScopeInjector requestScopeInjector;
	protected SessionScopeInjector sessionScopeInjector;
	protected ActionPathMacroInjector actionPathMacroInjector;

	@Override
	public void init() {
		requestScopeInjector = new RequestScopeInjector(madvocConfig, scopeDataResolver);
		sessionScopeInjector = new SessionScopeInjector(scopeDataResolver);
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

		madvocContextInjector.injectMadvocContext(target);

		// no need to inject madvoc params, as this can be slow
		// and its better to use some single data object instead
		//madvocContextInjector.injectMadvocParams(target);

		servletContextInjector.inject(actionRequest);

		sessionScopeInjector.inject(actionRequest);

		requestScopeInjector.prepare(actionRequest);
		requestScopeInjector.inject(actionRequest);

		actionPathMacroInjector.inject(actionRequest);
	}

	/**
	 * Performs outjection.
	 */
	protected void outject(ActionRequest actionRequest) {

		servletContextInjector.outject(actionRequest);

		sessionScopeInjector.outject(actionRequest);

		requestScopeInjector.outject(actionRequest);
	}

}
