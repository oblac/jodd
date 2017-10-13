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

package jodd.joy.auth;

import jodd.madvoc.ActionRequest;
import jodd.madvoc.interceptor.BaseActionInterceptor;
import jodd.servlet.DispatcherUtil;
import jodd.util.URLCoder;
import jodd.util.StringPool;
import jodd.log.Logger;
import jodd.log.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Authorization checking interceptor. Usually invoked after {@link AuthenticationInterceptor}.
 * <p>
 * <b>Authorization</b> pertains to the question "What may you do?". In JEE applications,
 * this is achieved by making secured resources accessible ("requestable" in web applications)
 * to particular "roles". Principals (i.e. users) who are associated with one or more of
 * these roles will have access to those resources.
 */
public abstract class AuthorizationInterceptor extends BaseActionInterceptor {

	private static final Logger log = LoggerFactory.getLogger(AuthorizationInterceptor.class);

	public Object intercept(ActionRequest actionRequest) throws Exception {
		HttpServletRequest servletRequest = actionRequest.getHttpServletRequest();
		HttpServletResponse servletResponse = actionRequest.getHttpServletResponse();
		HttpSession session = servletRequest.getSession();

		Object userSession = AuthUtil.getUserSession(session);

		if (log.isDebugEnabled()) {
			log.debug("authorize user: " + userSession);
		}

		if (!authorize(actionRequest, userSession)) {
			if (log.isInfoEnabled()) {
				log.info("access denied for: " + userSession);
			}

			servletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);

			if (userSession != null) {
				return resultAccessDenied();
			} else {
				return resultLogin(DispatcherUtil.getUrl(servletRequest));
			}
		}

		if (log.isInfoEnabled()) {
			log.info("access granted for: " + userSession);
		}

		return actionRequest.invoke();
	}


	/**
	 * Prepares result for access denied page.
	 */
	protected Object resultAccessDenied() {
		return "redirect:" + AuthAction.ALIAS_ACCESS_DENIED;
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
			targetUrl =
					'?' + URLCoder.encodeQueryParam(AuthAction.LOGIN_SUCCESS_PATH) +
					'=' + URLCoder.encodeQueryParam(targetUrl);
		}
		return "redirect:" + AuthAction.ALIAS_LOGIN + targetUrl;
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
