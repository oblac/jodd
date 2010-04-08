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

	protected String loginActionPath = AuthAction.LOGIN_ACTION_PATH;
	protected String logoutActionPath = AuthAction.LOGOUT_ACTION_PATH;
	protected String loginUsername = "j_username";
	protected String loginPassword = "j_password";
	protected String loginSuccessPath = "j_path";
	protected String loginToken = "j_token";


	@Override
	public Object intercept(ActionRequest actionRequest) throws Exception {
		HttpServletRequest servletRequest = actionRequest.getHttpServletRequest();
		HttpServletResponse servletResponse = actionRequest.getHttpServletResponse();
		HttpSession session = servletRequest.getSession();
		String actionPath = actionRequest.getActionPath();

		// LOGOUT
		if (actionPath.equals(logoutActionPath) == true) {
			log.debug("logout user");
			AuthUtil.removeAuthCookie(servletRequest, servletResponse);
			removeSessionObject(session);
			return AppAction.REDIRECT + AppAction.ALIAS_LOGIN;
		}

		// any other page then logout
		Object sessionObject = AuthUtil.getActiveSession(session);
		if (sessionObject != null) {
			// USER IS LOGGED IN
			if (actionPath.equals(loginActionPath)) {
				// never visit login path while user is logged in
				return AppAction.REDIRECT + AppAction.ALIAS_INDEX;
			}
			if (authorize(actionRequest, sessionObject) == false) {
				return AppAction.REDIRECT + AppAction.ALIAS_ACCESS_DENIED;
			}
			return actionRequest.invoke();
		}

		// COOKIE
		// session is not active, check the cookie
		String[] cookieData = AuthUtil.readAuthCookie(servletRequest);
		if (cookieData != null) {
			sessionObject = loginCookie(cookieData);
			if (sessionObject != null) {
				log.debug("login with cookie");
				saveSessionObject(session, sessionObject);
				if (authorize(actionRequest, sessionObject) == false) {
					return AppAction.REDIRECT + AppAction.ALIAS_ACCESS_DENIED;
				}
				return actionRequest.invoke();
			}
			// no session, invalid cookie
			AuthUtil.removeAuthCookie(servletRequest, servletResponse);
		}

		// ANY PAGE BUT LOGIN
		if (actionPath.equals(loginActionPath) == false) {
			if (authorize(actionRequest, null) == false) {
				// session is not active, redirect to login
				log.debug("authentication required");
				servletRequest.setAttribute(loginSuccessPath, DispatcherUtil.getActionPath(servletRequest));
				actionRequest.getHttpServletResponse().setStatus(HttpServletResponse.SC_FORBIDDEN);
				return AppAction.CHAIN + AppAction.ALIAS_LOGIN;
			}
			// public page
			return actionRequest.invoke();
		}

		// LOGIN PAGE
		// session is not active, but user wants to login
		String path = loginSuccessPath != null ? servletRequest.getParameter(loginSuccessPath) : null;
		if (StringUtil.isEmpty(path)) {
			path = AppAction.ALIAS_INDEX;
		}

		if (loginToken != null) {
			String token = servletRequest.getParameter(loginToken);
			// check token
			if (CsrfShield.checkCsrfToken(session, token) == false) {
				log.warn("csrf token validation failed.");
				return AppAction.REDIRECT + AppAction.ALIAS_LOGIN + "?err=2";
			}
		}

		String username = servletRequest.getParameter(loginUsername);
		String password = servletRequest.getParameter(loginPassword);
		sessionObject = loginUser(username, password);
		if (sessionObject == null) {
			log.warn("login failed for {}.", username);
			return AppAction.REDIRECT + AppAction.ALIAS_LOGIN + "?err=1";
		}
		saveSessionObject(session, sessionObject);
		cookieData = createCookieData(sessionObject);
		AuthUtil.storeAuthCookie(servletResponse, cookieData[0], cookieData[1]);
		log.info("login {} ok", username);
		if (authorize(actionRequest, sessionObject) == false) {
			return AppAction.REDIRECT + AppAction.ALIAS_ACCESS_DENIED;
		}
		return AppAction.REDIRECT + path;
	}

	/**
	 * Saves session object into the user session.
	 */
	protected void saveSessionObject(HttpSession session, Object sessionObject) {
		session.setAttribute(AuthUtil.AUTH_SESSION_NAME, sessionObject);
	}

	/**
	 * Removes session object.
	 */
	protected void removeSessionObject(HttpSession session) {
		session.removeAttribute(AuthUtil.AUTH_SESSION_NAME);
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
	protected abstract Object loginCookie(String[] cookieData);

	/**
	 * Tries to login a user. Returns <code>true</code> if login is successful, otherwise it returns <code>false</code>.
	 *
	 * @param username entered user name from login form
	 * @param password entered raw password
	 */
	protected abstract Object loginUser(String username, String password);

	/**
	 * Prepares cookie data from session object.
	 */
	protected abstract String[] createCookieData(Object sessionObject);
}