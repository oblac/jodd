// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet;

import jodd.util.RandomStringUtil;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Shields agains CSRF attacks.
 */
public class CsrfShield {

	public static final String CSRF_TOKEN_NAME = "_csrf_token";
	public static final String CSRF_TOKEN_SET = "_csrf_token_set";

	protected static int timeToLive = 600;

	/**
	 * Sets time to live for tokens in seconds.
	 * By setting negative value or 0 token leaves forever.
	 */
	public static void setTimeToLive(int periodInSeconds) {
		timeToLive = periodInSeconds;
	}

	// ---------------------------------------------------------------- prepare

	/**
	 * @see #prepareCsrfToken(javax.servlet.http.HttpSession, int)
	 */
	public static String prepareCsrfToken(HttpSession session) {
		return prepareCsrfToken(session, timeToLive);
	}

	/**
	 * Generates new CSRF token and puts it in the session. Returns generated token value.
	 */
	@SuppressWarnings({"unchecked"})
	public static String prepareCsrfToken(HttpSession session, int timeToLive) {
		Set<Token> tokenSet = (Set<Token>) session.getAttribute(CSRF_TOKEN_SET);
		if (tokenSet == null) {
			tokenSet = new HashSet<Token>();
			session.setAttribute(CSRF_TOKEN_SET, tokenSet);
		}
		String value;
		boolean unique;
		do {
			value = RandomStringUtil.randomAlphaNumeric(32);
			unique = tokenSet.add(new Token(value, timeToLive));
		} while (!unique);
		return value;
	}


	// ---------------------------------------------------------------- check

	/**
	 * @see #checkCsrfToken(javax.servlet.http.HttpServletRequest, String) 
	 */
	public static boolean checkCsrfToken(HttpServletRequest request) {
		return checkCsrfToken(request, CSRF_TOKEN_NAME);
	}

	/**
	 * Checks if {@link jodd.servlet.tag.CsrfTokenTag CSRF token} is valid.
	 * Returns <code>false</code> if token was requested, but not found.
	 * Otherwise, it returns <code>true</code>.
	 */
	@SuppressWarnings({"unchecked"})
	public static boolean checkCsrfToken(HttpServletRequest request, String tokenName) {
		String tokenValue = request.getParameter(tokenName);
		return checkCsrfToken(request.getSession(), tokenValue);
	}

	/**
	 * Checks token value.
C	 */
	@SuppressWarnings({"unchecked"})
	public static boolean checkCsrfToken(HttpSession session, String tokenValue) {
		Set<Token> tokenSet = (Set<Token>) session.getAttribute(CSRF_TOKEN_SET);
		if ((tokenSet == null) && (tokenValue == null)) {
			return true;
		}
		if ((tokenSet == null) || (tokenValue == null)) {
			return false;
		}
		boolean found = false;
		Iterator<Token> it = tokenSet.iterator();
		while (it.hasNext()) {
			Token t = it.next();
			if (t.isExpired()) {
				it.remove();
				continue;
			}
			if (t.getValue().equals(tokenValue)) {
				it.remove();
				found = true;
			}
		}
		return found;
	}

	/**
	 * Csrf Token.
	 */
	public static class Token {
		protected final String value;
		protected final long validUntil;

		public Token(String value) {
			this(value, 0);
		}

		public Token(String value, long timeToLive) {
			this.value = value;
			this.validUntil = timeToLive <= 0 ? Long.MAX_VALUE : (System.currentTimeMillis() + timeToLive * 1000);
		}

		/**
		 * Returns <code>true</code> if token is expired.
		 */
		public boolean isExpired() {
			return System.currentTimeMillis() > validUntil;
		}

		/**
		 * Returns token value.
		 */
		public String getValue() {
			return value;
		}
	}

}
