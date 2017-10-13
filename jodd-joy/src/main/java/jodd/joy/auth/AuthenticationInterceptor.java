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
import jodd.servlet.CsrfShield;
import jodd.util.StringUtil;
import jodd.log.Logger;
import jodd.log.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Authentication checking interceptor. Usually invoked before {@link AuthorizationInterceptor}.
 * Provides auto-login using cookies.
 * <p>
 * <b>Authentication</b> pertains to the question "Who are you?". Usually a user
 * authenticates himself by successfully associating his "principal"
 * (often a username) with his "credentials" (often a password).
 */
public abstract class AuthenticationInterceptor<U> extends BaseActionInterceptor {

	private static final Logger log = LoggerFactory.getLogger(AuthenticationInterceptor.class);

	/**
	 * If <code>true</code>, cookie will be created for keeping user sessions.
	 */
	protected boolean useCookie = true;

	/**
	 * Cookie max age, when cookies are used.
	 * By default set to 14 days.
	 */
	protected int cookieMaxAge = 14 * 24 * 60 * 60;

	/**
	 * When user just logs in with cookie, should we recreate the cookie
	 * (and therefore prolong cookie valid time) or leave it as it is.
	 */
	protected boolean recreateCookieOnLogin;


	public Object intercept(ActionRequest actionRequest) throws Exception {
		HttpServletRequest servletRequest = actionRequest.getHttpServletRequest();
		HttpServletResponse servletResponse = actionRequest.getHttpServletResponse();
		HttpSession session = servletRequest.getSession();
		String actionPath = actionRequest.getActionPath();

		// LOGOUT
		if (isLogoutAction(actionPath)) {
			log.debug("logout user");

			closeAuthSession(servletRequest, servletResponse);
			return resultLogoutSuccess();
		}

		// any other page then logout
		U userSession = (U) AuthUtil.getUserSession(session);
		if (userSession != null) {
			// USER IS LOGGED IN
			if (isLoginAction(actionPath)) {
				// never access login path while user is logged in
				return resultLoginSuccess(null);
			}
			// CONTINUE
			return actionRequest.invoke();
		}

		// no user session

		// COOKIE
		// user session is not available, check the cookie
		String[] cookieData = null;
		if (useCookie) {
			try {
				cookieData = AuthUtil.readAuthCookie(servletRequest);
			} catch (Exception ex) {
				log.warn("invalid cookie", ex);
			}
		}

		if (cookieData != null) {
			userSession = loginViaCookie(cookieData);

			if (userSession != null) {
				log.debug("login with cookie");

				startAuthSession(servletRequest, servletResponse, userSession, false);

				if (isLoginAction(actionPath)) {
					return resultLoginSuccess(null);
				}
				// continue
				return actionRequest.invoke();
			}
			// no session, invalidate cookie
			closeAuthSession(servletRequest, servletResponse);
		}

		// REGISTER USER
		if (isRegisterAction(actionPath)) {
			U newUserSession = (U) AuthUtil.getNewUserSession(servletRequest);
			if (newUserSession != null) {
				log.debug("new user session created");

				startAuthSession(servletRequest, servletResponse, newUserSession, true);
				return resultRegistrationSuccess();
			}
		}

		if (!isLoginAction(actionPath)) {
			// ANY PAGE BUT LOGIN, continue
			return actionRequest.invoke();
		}

		// LOGIN
		// session is not active, but user wants to login
		String token = servletRequest.getParameter(AuthAction.LOGIN_TOKEN);
		// check token
		if (!CsrfShield.checkCsrfToken(session, token)) {
			log.warn("csrf token validation failed");
			return resultLoginFailed(2);
		}

		userSession = loginViaRequest(servletRequest);
		if (userSession == null) {
			log.warn("login failed");
			return resultLoginFailed(1);
		}

		startAuthSession(servletRequest, servletResponse, userSession, true);
		log.info("login ok");

		// LOGGED IN
		String path = servletRequest.getParameter(AuthAction.LOGIN_SUCCESS_PATH);
		return resultLoginSuccess(path);
	}

	// ---------------------------------------------------------------- main auth hooks

	/**
	 * Starts auth session by saving session auth object and optionally creating an auth cookie.
	 * Auth cookie is created for new auth sessions and for old ones if recreation is enabled.
	 * @param servletRequest http request
	 * @param servletResponse http response
	 * @param userSession created session object
	 * @param isNew if <code>true</code> indicated the session is new (i.e. user is either registered or signed in), if <code>false</code> means that session is continued (i.e. user is signed in via cookie).
	 */
	protected void startAuthSession(HttpServletRequest servletRequest, HttpServletResponse servletResponse, U userSession, boolean isNew) {
		AuthUtil.startUserSession(servletRequest, userSession);
		if (!useCookie) {
			return;
		}
		if (isNew || recreateCookieOnLogin) {
			String[] cookieData = createCookieData(userSession);
			if (cookieData != null) {
				AuthUtil.storeAuthCookie(servletResponse, cookieMaxAge, cookieData[0], cookieData[1]);
			}
		}
	}

	/**
	 * Closes auth session by removing auth session object from the http session
	 * and clearing the auth cookie.
	 */
	protected void closeAuthSession(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
		AuthUtil.closeUserSession(servletRequest);
		AuthUtil.removeAuthCookie(servletRequest, servletResponse);
	}


	// ---------------------------------------------------------------- actions detectors

	/**
	 * Detects login path.
	 */
	protected boolean isLoginAction(String actionPath) {
		return actionPath.equals(AuthAction.LOGIN_ACTION_PATH);
	}

	/**
	 * Detects logout path.
	 */
	protected boolean isLogoutAction(String actionPath) {
		return actionPath.equals(AuthAction.LOGOUT_ACTION_PATH);
	}

	/**
	 * Detects registration path.
	 */
	protected boolean isRegisterAction(String actionPath) {
		return actionPath.equals(AuthAction.REGISTER_ACTION_PATH);
	}

	// ---------------------------------------------------------------- results


	/**
	 * Prepares result to continue to, after success login.
	 * If target path is empty, will continue to the index page.
	 */
	protected Object resultLoginSuccess(String path) {
		if (StringUtil.isEmpty(path)) {
			path = AuthAction.ALIAS_INDEX;
		}
		return "redirect:" + path;
	}

	/**
	 * Prepares result for logout success page.
	 */
	protected Object resultLogoutSuccess() {
		return "redirect:" + AuthAction.ALIAS_INDEX;
	}

	/**
	 * Prepares result for registration success page.
	 */
	protected Object resultRegistrationSuccess() {
		return "redirect:" + AuthAction.ALIAS_INDEX;
	}


	/**
	 * Prepares result for login failed page.
	 */
	protected Object resultLoginFailed(int reason) {
		return "redirect:" + AuthAction.ALIAS_LOGIN + "?err=" + reason;
	}

	// ---------------------------------------------------------------- abstracts

	/**
	 * Tries to login user with cookie data. Returns session object, otherwise returns <code>null</code>.
	 */
	protected abstract U loginViaCookie(String[] cookieData);

	/**
	 * Tires to login user with form data. Returns session object, otherwise returns <code>null</code>.
	 * By default, calls {@link #loginUsernamePassword(String, String)}.
	 */
	protected U loginViaRequest(HttpServletRequest servletRequest) {
		String username = servletRequest.getParameter(AuthAction.LOGIN_USERNAME);
		String password = servletRequest.getParameter(AuthAction.LOGIN_PASSWORD);

		log.info("login " + username);

		return loginUsernamePassword(username, password);
	}

	/**
	 * Tries to login a user using username and password.
	 * Returns session object if login was successful.
	 *
	 * @param username entered user name from login form
	 * @param password entered raw password
	 */
	protected abstract U loginUsernamePassword(String username, String password);

	/**
	 * Prepares cookie data from session object.
	 */
	protected abstract String[] createCookieData(U userSession);

}