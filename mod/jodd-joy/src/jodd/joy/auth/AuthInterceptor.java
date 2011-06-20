// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.auth;

import jodd.joy.madvoc.action.AppAction;
import jodd.madvoc.ActionRequest;
import jodd.madvoc.interceptor.ActionInterceptor;
import jodd.servlet.CsrfShield;
import jodd.servlet.DispatcherUtil;
import jodd.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static jodd.joy.auth.AuthUtil.AUTH_SESSION_NAME;
import static jodd.joy.madvoc.action.AppAction.CHAIN;
import static jodd.joy.madvoc.action.AppAction.REDIRECT;

/**
 * Authentication checking interceptor, the core of Jodd auth system.
 * Performs authentication and authorization.
 * <p>
 * <b>Authentication</b> pertains to the question “Who are you?”. Usually a user
 * authenticates himself by successfully associating his “principal”
 * (often a username) with his “credentials” (often a password).
 * <p>
 * <b>Authorization</b> pertains to the question “What may you do?”. In JEE applications,
 * this is achieved by making secured resources accessible (“requestable” in web applications)
 * to particular “roles”. Principals (i.e. users) who are associated with one or more of
 * these roles will have access to those resources.
 */
public abstract class AuthInterceptor extends ActionInterceptor {

	private static final Logger log = LoggerFactory.getLogger(AuthInterceptor.class);

	/**
	 * Action path that performs user login.
	 */
	protected String loginActionPath = AuthAction.LOGIN_ACTION_PATH;

	/**
	 * Action path that performs user logout.
	 */
	protected String logoutActionPath = AuthAction.LOGOUT_ACTION_PATH;

	/**
	 * Action path that performs registration of new users.
	 */
	protected String registerActionPath = AuthAction.REGISTER_ACTION_PATH;
	
	/**
	 * Page to redirect to after successful login, if no success path is
	 * defined as request parameter.
	 */
	protected String loginSuccessPage = AuthAction.ALIAS_INDEX;

	/**
	 * Page for failed logins.
	 */
	protected String loginFailedPage = AppAction.ALIAS_LOGIN;

	/**
	 * Access denied page.
	 */
	protected String accessDeniedPage = AppAction.ALIAS_ACCESS_DENIED;

	/**
	 * Action path to redirect to after successful logout.
	 */
	protected String logoutSuccessPage = AuthAction.ALIAS_INDEX;


	/**
	 * Welcome page when new user is registered.
	 */
	protected String registrationSuccessPage = AuthAction.ALIAS_INDEX;


	protected String loginUsername = "j_username";
	protected String loginPassword = "j_password";
	protected String loginSuccessPath = "j_path";
	protected String loginToken = "j_token";


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
		if (actionPath.equals(logoutActionPath) == true) {
			if (log.isDebugEnabled()) {
				log.debug("logout user");
			}
			closeAuthSession(servletRequest, servletResponse);
			return resultLogoutSuccess();
		}

		// any other page then logout
		Object sessionObject = AuthUtil.getActiveSession(session);
		if (sessionObject != null) {
			// USER IS LOGGED IN
			if (actionPath.equals(loginActionPath)) {
				// never access login path while user is logged in
				return resultLoginSuccess();
			}
			if (authorize(actionRequest, sessionObject) == false) {
				return resultAccessDenied();
			}
			// PRIVATE PAGE, LOGGED IN
			return actionRequest.invoke();
		}

		// COOKIE
		// session is not active, check the cookie
		String[] cookieData = useCookie ? AuthUtil.readAuthCookie(servletRequest) : null;
		if (cookieData != null) {
			sessionObject = loginViaCookie(cookieData);
			if (sessionObject != null) {
				if (log.isDebugEnabled()) {
					log.debug("login with cookie");
				}
				startAuthSession(servletRequest, servletResponse, sessionObject, false);
				if (authorize(actionRequest, sessionObject) == false) {
					return resultAccessDenied();
				}
				return actionRequest.invoke();
			}
			// no session, invalidate cookie
			closeAuthSession(servletRequest, servletResponse);
		}

		if (actionPath.equals(registerActionPath)) {
			Object newSessionObject = servletRequest.getAttribute(AUTH_SESSION_NAME);
			if (newSessionObject != null) {
				if (log.isDebugEnabled()) {
					log.debug("new user session created");
				}
				servletRequest.removeAttribute(AUTH_SESSION_NAME);
				startAuthSession(servletRequest, servletResponse, newSessionObject, true);
				return resultRegistrationSuccess();
			}
		}

		// ANY PAGE BUT LOGIN
		if (actionPath.equals(loginActionPath) == false) {
			if (authorize(actionRequest, null) == false) {
				// session is not active, chain to login
				if (log.isDebugEnabled()) {
					log.debug("authentication required");
				}
				servletRequest.setAttribute(loginSuccessPath, DispatcherUtil.getUrl(servletRequest));
				actionRequest.getHttpServletResponse().setStatus(HttpServletResponse.SC_FORBIDDEN);
				return resultLogin();
			}
			// PUBLIC PAGE, NOT LOGGED IN
			return actionRequest.invoke();
		}

		// LOGIN PAGE
		// session is not active, but user wants to login
		if (loginToken != null) {
			String token = servletRequest.getParameter(loginToken);
			// check token
			if (CsrfShield.checkCsrfToken(session, token) == false) {
				log.warn("csrf token validation failed.");
				return resultLoginFailed(2);
			}
		}

		sessionObject = loginViaRequest(servletRequest);
		if (sessionObject == null) {
			log.warn("login failed.");
			return resultLoginFailed(1);
		}
		startAuthSession(servletRequest, servletResponse, sessionObject, true);
		log.info("login ok");
		if (authorize(actionRequest, sessionObject) == false) {
			return resultAccessDenied();
		}

		// LOGGED IN
		String path = loginSuccessPath != null ? servletRequest.getParameter(loginSuccessPath) : null;
		if (StringUtil.isEmpty(path)) {
			path = loginSuccessPage;
		}
		return resultContinue(path);
	}

	// ---------------------------------------------------------------- main auth hooks

	/**
	 * Starts auth session by saving session auth object and optionally creating an auth cookie.
	 * Auth cookie is created for new auth sessions and for old ones if recreation is enabled.
	 * @param servletRequest http request
	 * @param servletResponse http response
	 * @param sessionObject created session object
	 * @param isNew if <code>true</code> indicated the session is new (i.e. user is either registered or signed in), if <code>false</code> means that session is continued (i.e. user is signed in via cookie).
	 */
	protected void startAuthSession(HttpServletRequest servletRequest, HttpServletResponse servletResponse, Object sessionObject, boolean isNew) {
		servletRequest.getSession().setAttribute(AUTH_SESSION_NAME, sessionObject);
		if (useCookie == false) {
			return;
		}
		if (isNew == true || recreateCookieOnLogin == true) {
			String[] cookieData = createCookieData(sessionObject);
			AuthUtil.storeAuthCookie(servletResponse, cookieMaxAge, cookieData[0], cookieData[1]);
		}
	}

	/**
	 * Closes auth session by removing auth session object from the http session
	 * and clearing the auth cookie.
	 */
	protected void closeAuthSession(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
		servletRequest.getSession().removeAttribute(AUTH_SESSION_NAME);
		AuthUtil.removeAuthCookie(servletRequest, servletResponse);
	}


	// ---------------------------------------------------------------- redirects


	/**
	 * Prepares result for logout success page.
	 */
	protected Object resultLogoutSuccess() {
		return REDIRECT + logoutSuccessPage;
	}

	/**
	 * Prepares result for login success page.
	 */
	protected Object resultLoginSuccess() {
		return REDIRECT + loginSuccessPage;
	}

	/**
	 * Prepares result for registration success page.
	 */
	protected Object resultRegistrationSuccess() {
		return REDIRECT + registrationSuccessPage;
	}

	/**
	 * Prepares result for access denied page.
	 */
	protected Object resultAccessDenied() {
		return REDIRECT + accessDeniedPage;
	}

	/**
	 * Prepares result for login page.
	 */
	protected Object resultLogin() { 
		return CHAIN + AppAction.ALIAS_LOGIN;
	}

	/**
	 * Prepares result for login failed page.
	 */
	protected Object resultLoginFailed(int type) {
		return REDIRECT + loginFailedPage + "?err=" + type;
	}

	/**
	 * Prepares result to continue.
	 */
	protected Object resultContinue(String path) {
		return REDIRECT + path;
	}



	// ---------------------------------------------------------------- authorization

	/**
	 * Performs authorization of a request. User may or may not be
	 * authenticated. Default implementation returns <code>true</code>.
	 * <p>
	 * For user that is not authenticated, <code>sessionObject</code> is <code>null</code>.
	 * Authenticated users will have their session.
	 */
	protected abstract boolean authorize(ActionRequest request, Object sessionObject);

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
		String username = servletRequest.getParameter(loginUsername);
		String password = servletRequest.getParameter(loginPassword);
		log.info("login {}.", username);
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
	protected abstract String[] createCookieData(Object sessionObject);
}