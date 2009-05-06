// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet;

import jodd.util.idgen.Uuid24Generator;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import java.util.Set;
import java.util.HashSet;

/**
 * Shields agains CSRF attacks.
 */
public class CsrfShield {

	public static final String CSRF_TOKEN_NAME = "_csrf_token";
	public static final String CSRF_TOKEN_SET_NAME = "_csrf_token_set";

	/**
	 * Generates new CSRF token and puts it in the session. Returns generated token.
	 */
	public static String prepareCsrfToken(HttpSession session) {
		@SuppressWarnings({"unchecked"})
		Set<String> tokenSet = (Set<String>) session.getAttribute(CSRF_TOKEN_SET_NAME);
		if (tokenSet == null) {
			tokenSet = new HashSet<String>();
			session.setAttribute(CSRF_TOKEN_SET_NAME, tokenSet);
		}

		String value;
		boolean unique;
		do {
			value = Uuid24Generator.generateUUID();
			unique = tokenSet.add(value);
		} while (!unique);
		return value;
	}

	/**
	 * Checks if existing {@link jodd.servlet.tag.CsrfTokenTag CSRF token} is valid.
	 * Returns <code>false</code> if token was requested, but not found.
	 * Otherwise, it returns <code>true</code>: when requested token
	 * was found; or when there was no token request at all.
	 * @see #checkExistingCsrfToken(javax.servlet.http.HttpServletRequest)
	 */
	public static boolean checkCsrfToken(HttpServletRequest request) {
		@SuppressWarnings({"unchecked"})
		Set<String> tokenSet = (Set<String>) request.getSession().getAttribute(CSRF_TOKEN_SET_NAME);
		if (tokenSet != null) {
			String tokenValue = request.getParameter(CSRF_TOKEN_NAME);
			if (tokenValue != null) {
				return tokenSet.remove(tokenValue);
			}
		}
		return true;
	}

	/**
	 * Similar as {@link #checkCsrfToken(javax.servlet.http.HttpServletRequest)} except it returns <code>true</code>
	 * only when token exists. Otherwise, it returns <code>false</code>: when token doesn't exist
	 * or if it is invalid.
	 * @see #checkCsrfToken(javax.servlet.http.HttpServletRequest)
	 */
	public static boolean checkExistingCsrfToken(HttpServletRequest request) {
		@SuppressWarnings({"unchecked"})
		Set<String> tokenSet = (Set<String>) request.getSession().getAttribute(CSRF_TOKEN_SET_NAME);
		if (tokenSet != null) {
			String tokenValue = request.getParameter(CSRF_TOKEN_NAME);
			if (tokenValue != null) {
				return tokenSet.remove(tokenValue);
			}
		}
		return false;
	}


}
