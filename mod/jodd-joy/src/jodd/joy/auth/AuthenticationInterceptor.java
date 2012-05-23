// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.auth;

import jodd.joy.madvoc.action.AppAction;
import jodd.log.Log;
import jodd.madvoc.ActionRequest;
import jodd.madvoc.interceptor.ActionInterceptor;
import jodd.servlet.CsrfShield;
import jodd.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static jodd.joy.madvoc.action.AppAction.REDIRECT;

/**
 * Authentication checking interceptor. Usually invoked before {@link AuthorizationInterceptor}.
 * Provides auto-login using cookies.
 * <p>
 * <b>Authentication</b> pertains to the question "Who are you?". Usually a user
 * authenticates himself by successfully associating his "principal"
 * (often a username) with his "credentials" (often a password).
 */
public abstract class AuthenticationInterceptor extends ActionInterceptor {

	private static final Log log = Log.getLogger(AuthenticationInterceptor.class);

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


	@Override
	public Object intercept(ActionRequest actionRequest) throws Exception {
		HttpServletRequest servletRequest = actionRequest.getHttpServletRequest();
		HttpServletResponse servletResponse = actionRequest.getHttpServletResponse();
		HttpSession session = servletRequest.getSession();
		String actionPath = actionRequest.getActionPath();

		// LOGOUT
		if (isLogoutAction(actionPath) == true) {
			if (log.isDebugEnabled()) {
				log.debug("logout user");
			}
			closeAuthSession(servletRequest, servletResponse);
			return resultLogoutSuccess();
		}

		// any other page then logout
		Object userSession = AuthUtil.getUserSession(session);
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
		String[] cookieData = useCookie ? AuthUtil.readAuthCookie(servletRequest) : null;
		if (cookieData != null) {
			userSession = loginViaCookie(cookieData);

			if (userSession != null) {
				if (log.isDebugEnabled()) {
					log.debug("login with cookie");
				}
				startAuthSession(servletRequest, servletResponse, userSession, false);
				// continue
				return actionRequest.invoke();
			}
			// no session, invalidate cookie
			closeAuthSession(servletRequest, servletResponse);
		}

		// REGISTER USER
		if (isRegisterAction(actionPath)) {
			Object newUserSession = AuthUtil.getNewUserSession(servletRequest);
			if (newUserSession != null) {
				if (log.isDebugEnabled()) {
					log.debug("new user session created");
				}
				startAuthSession(servletRequest, servletResponse, newUserSession, true);
				return resultRegistrationSuccess();
			}
		}

		if (isLoginAction(actionPath) == false) {
			// ANY PAGE BUT LOGIN, continue
			return actionRequest.invoke();
		}

		// LOGIN
		// session is not active, but user wants to login
		String token = servletRequest.getParameter(AuthAction.LOGIN_TOKEN);
		// check token
		if (CsrfShield.checkCsrfToken(session, token) == false) {
			log.warn("csrf token validation failed.");
			return resultLoginFailed(2);
		}

		userSession = loginViaRequest(servletRequest);
		if (userSession == null) {
			log.warn("login failed.");
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
	protected void startAuthSession(HttpServletRequest servletRequest, HttpServletResponse servletResponse, Object userSession, boolean isNew) {
		AuthUtil.startUserSession(servletRequest, userSession);
		if (useCookie == false) {
			return;
		}
		if (isNew == true || recreateCookieOnLogin == true) {
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
		return REDIRECT + path;
	}

	/**
	 * Prepares result for logout success page.
	 */
	protected Object resultLogoutSuccess() {
		return REDIRECT + AuthAction.ALIAS_INDEX;
	}

	/**
	 * Prepares result for registration success page.
	 */
	protected Object resultRegistrationSuccess() {
		return REDIRECT + AuthAction.ALIAS_INDEX;
	}


	/**
	 * Prepares result for login failed page.
	 */
	protected Object resultLoginFailed(int reason) {
		return REDIRECT + AppAction.ALIAS_LOGIN + "?err=" + reason;
	}

	// ---------------------------------------------------------------- abstracts

	/**
	 * Tries to login user with cookie data. Returns session object, otherwise returns <code>null</code>.
	 */
	protected abstract Object loginViaCookie(String[] cookieData);


	/**
	 * Tires to login user with form data. Returns session object, otherwise returns <code>null</code>.
	 * By default, calls {@link #loginUsernamePassword(String, String)}.
	 */
	protected Object loginViaRequest(HttpServletRequest servletRequest) {
		String username = servletRequest.getParameter(AuthAction.LOGIN_USERNAME);
		String password = servletRequest.getParameter(AuthAction.LOGIN_PASSWORD);

		log.info("login " + username);

		return loginUsernamePassword(username, password);
	}

	/**
	 * Tries to login a user using username and password.
	 * Returns <code>true</code> if login is successful, otherwise returns <code>false</code>.
	 *
	 * @param username entered user name from login form
	 * @param password entered raw password
	 */
	protected abstract Object loginUsernamePassword(String username, String password);

	/**
	 * Prepares cookie data from session object.
	 */
	protected abstract String[] createCookieData(Object userSession);
}