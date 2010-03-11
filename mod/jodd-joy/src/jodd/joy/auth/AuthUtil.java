package jodd.joy.auth;

import jodd.joy.crypt.SymmetricEncryptor;
import jodd.servlet.ServletUtil;
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
	 * Returns <code>true</code> if user session is active.
	 */
	public static boolean isSessionActive(HttpSession session) {
		return session.getAttribute(AUTH_SESSION_NAME) != null;
	}

	/**
	 * Returns active session or <code>null</code> if there is no user session.
	 */
	public static Object getActiveSession(HttpSession session) {
		return session.getAttribute(AUTH_SESSION_NAME);
	}

	private static final SymmetricEncryptor SYMEC = new SymmetricEncryptor("jodd#auth!enc*ss@ap");
	private static final char COOKIE_DELIMETER = '*';

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
			values[i] = SYMEC.decrypt(values[i]);
		}
		return values;
	}

	/**
	 * Stores string array into the cookie.
	 */
	public static void storeAuthCookie(HttpServletResponse response, String... values) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < values.length; i++) {
			if (i > 0) {
				sb.append(COOKIE_DELIMETER);
			}
			sb.append(SYMEC.encrypt(values[i]));
		}

		Cookie cookie = new Cookie(AUTH_COOKIE_NAME, sb.toString());
		//cookie.setDomain(SSORealm.SSO_DOMAIN);
		cookie.setMaxAge(14*24*60*60);
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
