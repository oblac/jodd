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
import jodd.madvoc.config.ActionRuntime;
import jodd.madvoc.interceptor.ActionInterceptor;
import jodd.madvoc.result.JsonResult;
import jodd.servlet.ServletUtil;
import jodd.net.HttpStatus;

import javax.servlet.http.HttpServletRequest;

/**
 * Simple Auth interceptor.
 * <p>
 * <b>Authentication</b> checking interceptor.
 * Provides auto-login using cookies.
 * <p>
 * <b>Authentication</b> pertains to the question "Who are you?". Usually a user
 * authenticates himself by successfully associating his "principal"
 * (often a username) with his "credentials" (often a password).
 * <p>
 * <b>Authorization</b> checking interceptor.
 * <p>
 * <b>Authorization</b> pertains to the question "What may you do?". In JEE applications,
 * this is achieved by making secured resources accessible ("requestable" in web applications)
 * to particular "roles". Principals (i.e. users) who are associated with one or more of
 * these roles will have access to those resources.
 */
public class AuthInterceptor<T> implements ActionInterceptor {

	/**
	 * Simple static HOOK for the implementation.
	 */
	public static UserAuth userAuth;

	private UserAuth<T> userAuth() {
		return userAuth;
	}

	protected boolean return404instead401 = true;
	protected boolean authenticateViaBasicAuth = true;

	public void setReturn404instead401(final boolean return404instead401) {
		this.return404instead401 = return404instead401;
	}

	public void setAuthenticateViaBasicAuth(final boolean authenticateViaBasicAuth) {
		this.authenticateViaBasicAuth = authenticateViaBasicAuth;
	}

	@Override
	public Object intercept(final ActionRequest actionRequest) throws Exception {
		final ActionRuntime actionRuntime = actionRequest.getActionRuntime();

		if (actionRuntime.isAuthenticated()) {
			// action requires user to be authenticated

			T grantedAuthToken = authenticateUserViaHttpSession(actionRequest);

			if (grantedAuthToken == null) {
				grantedAuthToken = authenticateUserViaToken(actionRequest);
			}

			if (authenticateViaBasicAuth && grantedAuthToken == null) {
				grantedAuthToken = authenticateUserViaBasicAuth(actionRequest);
			}

			if (grantedAuthToken == null) {
				return JsonResult.of(return404instead401 ? HttpStatus.error404().notFound() : HttpStatus.error401().unauthorized("Not authorized"));
			}

			if (!authorized(actionRequest)) {
				return JsonResult.of(HttpStatus.error403().forbidden());
			}
		}
		return actionRequest.invoke();
	}

	/**
	 * Tries to authenticate user via HTTP session. Returns the token if user is authenticated.
	 * Returned token may be rotated.
	 */
	protected T authenticateUserViaHttpSession(final ActionRequest actionRequest) {
		final HttpServletRequest servletRequest = actionRequest.getHttpServletRequest();

		final UserSession<T> userSession = UserSession.get(servletRequest);
		if (userSession == null) {
			return null;
		}

		final T authToken = userSession.getAuthToken();
		if (authToken == null) {
			return null;
		}

		// granted
		final T newAuthToken = userAuth().rotateToken(authToken);

		if (newAuthToken != authToken) {
			final UserSession<T> newUserSesion = new UserSession<>(newAuthToken, userAuth().tokenValue(newAuthToken));

			newUserSesion.start(servletRequest, actionRequest.getHttpServletResponse());
		}

		return newAuthToken;
	}

	/**
	 * Tries to authenticate user via token. Returns the token if user is authenticated.
	 * Returned token may be rotated.
	 */
	protected T authenticateUserViaToken(final ActionRequest actionRequest) {
		final HttpServletRequest servletRequest = actionRequest.getHttpServletRequest();

		// then try the auth token
		final String token = ServletUtil.resolveAuthBearerToken(servletRequest);
		if (token == null) {
			return null;
		}

		final T authToken = userAuth().validateToken(token);
		if (authToken == null) {
			return null;
		}

		// granted
		final T newAuthToken = userAuth().rotateToken(authToken);

		actionRequest.getHttpServletResponse().setHeader("Authentication", "Bearer: " + userAuth().tokenValue(newAuthToken));

		return newAuthToken;
	}

	/**
	 * Tires to authenticate user via the basic authentication. Returns the token if user is authenticated.
	 */
	protected T authenticateUserViaBasicAuth(final ActionRequest actionRequest) {
		final HttpServletRequest servletRequest = actionRequest.getHttpServletRequest();

		final String username = ServletUtil.resolveAuthUsername(servletRequest);
		if (username == null) {
			return null;
		}

		final String password = ServletUtil.resolveAuthPassword(servletRequest);

		final T authToken = userAuth().login(username, password);

		if (authToken == null) {
			return null;
		}

		return authToken;
	}


	/**
	 * Hook method for authorization of action requests.
	 */
	protected boolean authorized(final ActionRequest actionRequest) {
		return true;
	}

}