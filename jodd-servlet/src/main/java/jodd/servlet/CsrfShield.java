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

package jodd.servlet;

import jodd.util.RandomString;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Shields against CSRF attacks.
 */
public class CsrfShield {

	public static final String CSRF_TOKEN_NAME = "_csrf_token";
	public static final String CSRF_TOKEN_SET = "_csrf_token_set";

	protected static int timeToLive = 600;
	protected static int maxTokensPerSession = 20;

	/**
	 * Sets time to live for tokens in seconds.
	 * By setting negative value or 0 token leaves forever.
	 */
	public static void setTimeToLive(final int periodInSeconds) {
		timeToLive = periodInSeconds;
	}

	/**
	 * Sets max number of tokens that will be stored for single session.
	 * It is actually the number of CSRF validation that may occur in the
	 * same time. Limit prevents from malicious growing of the set.
	 */
	public static void setMaxTokensPerSession(final int maxTokensPerSession) {
		CsrfShield.maxTokensPerSession = maxTokensPerSession;
	}

	// ---------------------------------------------------------------- prepare

	/**
	 * @see #prepareCsrfToken(javax.servlet.http.HttpSession, int)
	 */
	public static String prepareCsrfToken(final PageContext pageContext) {
		return prepareCsrfToken(pageContext.getSession());
	}

	/**
	 * @see #prepareCsrfToken(javax.servlet.http.HttpSession, int)
	 */
	public static String prepareCsrfToken(final HttpSession session) {
		return prepareCsrfToken(session, timeToLive);
	}

	/**
	 * Generates new CSRF token and puts it in the session. Returns generated token value.
	 */
	@SuppressWarnings({"unchecked"})
	public static String prepareCsrfToken(final HttpSession session, final int timeToLive) {
		Set<Token> tokenSet = (Set<Token>) session.getAttribute(CSRF_TOKEN_SET);
		if (tokenSet == null) {
			tokenSet = new HashSet<>();
			session.setAttribute(CSRF_TOKEN_SET, tokenSet);
		}
		String value;
		boolean unique;
		do {
			value = RandomString.get().randomAlphaNumeric(32);
			assureSize(tokenSet);
			unique = tokenSet.add(new Token(value, timeToLive));
		} while (!unique);
		return value;
	}


	/**
	 * Removes expired tokens if token set is full.
	 * @see #setMaxTokensPerSession(int)  
	 */
	protected static void assureSize(final Set<Token> tokenSet) {
		if (tokenSet.size() < maxTokensPerSession) {
			return;
		}
		long validUntilMin = Long.MAX_VALUE;
		Token tokenToRemove = null;
		Iterator<Token> iterator = tokenSet.iterator();
		while (iterator.hasNext()) {
			Token token = iterator.next();
			if (token.isExpired()) {
				iterator.remove();
				continue;
			}
			if (token.validUntil < validUntilMin) {
				validUntilMin = token.validUntil;
				tokenToRemove = token;
			}
		}
		if ((tokenToRemove != null) && (tokenSet.size() >= maxTokensPerSession)) {
			tokenSet.remove(tokenToRemove);
		}
	}


	// ---------------------------------------------------------------- check

	/**
	 * @see #checkCsrfToken(javax.servlet.http.HttpServletRequest, String) 
	 */
	public static boolean checkCsrfToken(final HttpServletRequest request) {
		return checkCsrfToken(request, CSRF_TOKEN_NAME);
	}

	/**
	 * Checks if {@link jodd.servlet.tag.CsrfTokenTag CSRF token} is valid.
	 * Returns <code>false</code> if token was requested, but not found.
	 * Otherwise, it returns <code>true</code>.
	 */
	@SuppressWarnings({"unchecked"})
	public static boolean checkCsrfToken(final HttpServletRequest request, final String tokenName) {
		String tokenValue = request.getParameter(tokenName);
		return checkCsrfToken(request.getSession(), tokenValue);
	}

	/**
	 * Checks token value.
C	 */
	@SuppressWarnings({"unchecked"})
	public static boolean checkCsrfToken(final HttpSession session, final String tokenValue) {
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
	 * CSRF Token.
	 */
	public static class Token implements Serializable {

		protected final String value;
		protected final long validUntil;

		public Token(final String value) {
			this(value, 0);
		}

		public Token(final String value, final long timeToLive) {
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

		@Override
		public boolean equals(final Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			Token token = (Token) o;
			return value.equals(token.value);

		}

		@Override
		public int hashCode() {
			return value.hashCode();
		}
	}

}
