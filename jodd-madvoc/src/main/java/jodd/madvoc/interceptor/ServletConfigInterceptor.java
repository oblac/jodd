// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.interceptor;

import jodd.madvoc.ActionRequest;
import jodd.madvoc.ScopeType;
import jodd.madvoc.component.InjectorsManager;
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
	protected InjectorsManager injectorsManager;

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

		injectorsManager.getMadvocContextScopeInjector().inject(actionRequest);

		// no need to inject madvoc params, as this can be slow
		// and its better to use some single data object instead
		//madvocContextInjector.injectMadvocParams(target);

		injectorsManager.getServletContextScopeInjector().inject(actionRequest);
		injectorsManager.getApplicationScopeInjector().inject(actionRequest);

		injectorsManager.getSessionScopeInjector().inject(actionRequest);

		injectorsManager.getRequestScopeInjector().prepare(actionRequest);		// todo check if needed
		injectorsManager.getRequestScopeInjector().inject(actionRequest);

		injectorsManager.getActionPathMacroInjector().inject(actionRequest);
	}

	/**
	 * Performs outjection.
	 */
	protected void outject(ActionRequest actionRequest) {

		injectorsManager.getServletContextScopeInjector().outject(actionRequest);
		injectorsManager.getApplicationScopeInjector().outject(actionRequest);

		injectorsManager.getSessionScopeInjector().outject(actionRequest);

		injectorsManager.getRequestScopeInjector().outject(actionRequest);
	}

}