// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.auth;

import jodd.joy.madvoc.action.AppAction;
import jodd.log.Log;
import jodd.madvoc.ActionRequest;
import jodd.madvoc.interceptor.ActionInterceptor;
import jodd.servlet.DispatcherUtil;
import jodd.servlet.URLCoder;
import jodd.util.StringPool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static jodd.joy.madvoc.action.AppAction.REDIRECT;

/**
 * Authorization checking interceptor. Usually invoked after {@link AuthenticationInterceptor}.
 * <p>
 * <b>Authorization</b> pertains to the question "What may you do?". In JEE applications,
 * this is achieved by making secured resources accessible ("requestable" in web applications)
 * to particular "roles". Principals (i.e. users) who are associated with one or more of
 * these roles will have access to those resources.
 */
public abstract class AuthorizationInterceptor extends ActionInterceptor {

	private static final Log log = Log.getLogger(AuthorizationInterceptor.class);

	@Override
	public Object intercept(ActionRequest actionRequest) throws Exception {
		HttpServletRequest servletRequest = actionRequest.getHttpServletRequest();
		HttpServletResponse servletResponse = actionRequest.getHttpServletResponse();
		HttpSession session = servletRequest.getSession();

		Object userSession = AuthUtil.getUserSession(session);

		if (log.isDebugEnabled()) {
			log.debug("authorize user: " + userSession);
		}

		if (authorize(actionRequest, userSession) == false) {
			log.info("access denied for: " + userSession);

			servletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);

			if (userSession != null) {
				return resultAccessDenied();
			} else {
				return resultLogin(DispatcherUtil.getUrl(servletRequest));
			}
		}

		log.info("access granted for: " + userSession);

		return actionRequest.invoke();
	}


	/**
	 * Prepares result for access denied page.
	 */
	protected Object resultAccessDenied() {
		return REDIRECT + AppAction.ALIAS_ACCESS_DENIED;
	}


	/**
	 * Prepares result for login page, when access to target URL is forbidden.
	 * To prevent using request parameter, consider CHAIN result type.
	 * Target URL may be <code>null</code>.
	 */
	protected Object resultLogin(String targetUrl) {
		if (targetUrl == null) {
			targetUrl = StringPool.EMPTY;
		} else {
			targetUrl = URLCoder.build().param(AuthAction.LOGIN_SUCCESS_PATH, targetUrl).toString();
		}
		return REDIRECT + AppAction.ALIAS_LOGIN + targetUrl;
	}


	/**
	 * Performs authorization of a request. User may or may not be
	 * authenticated.
	 * <p>
	 * For user that is not authenticated, <code>userSession</code> is <code>null</code>.
	 * Authenticated users will have their user session set.
	 */
	protected abstract boolean authorize(ActionRequest request, Object userSession);

}
