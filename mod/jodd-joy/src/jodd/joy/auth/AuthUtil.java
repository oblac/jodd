// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.auth;

import jodd.joy.crypt.Threefish;
import jodd.servlet.ServletUtil;
import jodd.util.Base64;
import jodd.util.StringUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Authentication utilities.
 */
public class AuthUtil {

	public static final String AUTH_SESSION_NAME = "AUTH";
	public static final String AUTH_COOKIE_NAME = "JODD_JOY_REMEMBERME";

	/**
	 * Returns new session object from the request attributes,
	 * usually created during user registration.
	 */
	public static Object getNewUserSession(HttpServletRequest servletRequest) {
		Object newUserSession = servletRequest.getAttribute(AUTH_SESSION_NAME);
		servletRequest.removeAttribute(AUTH_SESSION_NAME);
		return newUserSession;
	}

	/**
	 * Returns user session or <code>null</code> if there is no authenticated user.
	 */
	public static Object getUserSession(HttpSession httpSession) {
		if (httpSession == null) {
			return null;
		}
		return httpSession.getAttribute(AUTH_SESSION_NAME);
	}

	/**
	 * @see #getUserSession(javax.servlet.http.HttpSession)
	 */
	public static Object getUserSession(HttpServletRequest servletRequest) {
		return getUserSession(servletRequest.getSession(false));
	}

	/**
	 * Closes user session.
	 */
	public static void closeUserSession(HttpSession httpSession) {
		if (httpSession != null) {
			httpSession.removeAttribute(AUTH_SESSION_NAME);
		}
	}

	/**
	 * @see #closeUserSession(javax.servlet.http.HttpSession)
	 */
	public static void closeUserSession(HttpServletRequest servletRequest) {
		closeUserSession(servletRequest.getSession(false));
	}

	/**
	 * Starts user session by storing user session object into http session.
	 */
	public static void startUserSession(HttpSession httpSession, Object userSession) {
		httpSession.setAttribute(AUTH_SESSION_NAME, userSession);
	}

	/**
	 * @see #startUserSession(javax.servlet.http.HttpServletRequest, Object)
	 */
	public static void startUserSession(HttpServletRequest servletRequest, Object userSession) {
		HttpSession session = servletRequest.getSession(true);

		session.setAttribute(AUTH_SESSION_NAME, userSession);
	}

	// ---------------------------------------------------------------- cookies

	private static final Threefish ENCRYPTOR = new Threefish(Threefish.BLOCK_SIZE_BITS_256);
	private static final char COOKIE_DELIMETER = '*';
	
	static {
		ENCRYPTOR.init("jodd#auth!enc*ss@ap", 0x134298db8abf9485L, 0x603bce00abL);
	}

	/**
	 * Reads auth cookie and returns stored string array from cookie data.
	 */
	public static String[] readAuthCookie(HttpServletRequest request) {
		Cookie cookie = ServletUtil.getCookie(request, AUTH_COOKIE_NAME);
		if (cookie == null) {
			return null;
		}
		String[] values = StringUtil.splitc(cookie.getValue(), COOKIE_DELIMETER);
		for (int i = 0; i < values.length; i++) {
			byte[] decoded = Base64.decode(values[i]);
			values[i] = ENCRYPTOR.decryptString(decoded);
		}
		return values;
	}

	/**
	 * Stores string array into the cookie.
	 */
	public static void storeAuthCookie(HttpServletResponse response, int cookieMaxAge, String... values) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < values.length; i++) {
			if (i > 0) {
				sb.append(COOKIE_DELIMETER);
			}
			byte[] encrypted = ENCRYPTOR.encryptString(values[i]); 
			sb.append(Base64.encodeToString(encrypted));
		}

		Cookie cookie = new Cookie(AUTH_COOKIE_NAME, sb.toString());
		//cookie.setDomain(SSORealm.SSO_DOMAIN);
		cookie.setMaxAge(cookieMaxAge);
		cookie.setPath("/");
		response.addCookie(cookie);
	}


	/**
	 * Removes auth cookie.
	 */
	public static void removeAuthCookie(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
		Cookie cookie = ServletUtil.getCookie(servletRequest, AUTH_COOKIE_NAME);
		if (cookie == null) {
			return;
		}
		cookie.setMaxAge(0);
		cookie.setPath("/");
		servletResponse.addCookie(cookie);
	}
}
