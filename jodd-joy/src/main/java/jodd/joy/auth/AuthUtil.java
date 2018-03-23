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

import jodd.servlet.ServletUtil;
import jodd.util.Base64;
import jodd.util.StringUtil;
import jodd.util.crypt.Threefish;

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
	public static Object getNewUserSession(final HttpServletRequest servletRequest) {
		Object newUserSession = servletRequest.getAttribute(AUTH_SESSION_NAME);
		servletRequest.removeAttribute(AUTH_SESSION_NAME);
		return newUserSession;
	}

	/**
	 * Returns user session or <code>null</code> if there is no authenticated user.
	 */
	public static Object getUserSession(final HttpSession httpSession) {
		if (httpSession == null) {
			return null;
		}
		return httpSession.getAttribute(AUTH_SESSION_NAME);
	}

	/**
	 * @see #getUserSession(javax.servlet.http.HttpSession)
	 */
	public static Object getUserSession(final HttpServletRequest servletRequest) {
		return getUserSession(servletRequest.getSession(false));
	}

	/**
	 * Closes user session.
	 */
	public static void closeUserSession(final HttpSession httpSession) {
		if (httpSession != null) {
			httpSession.removeAttribute(AUTH_SESSION_NAME);
		}
	}

	/**
	 * @see #closeUserSession(javax.servlet.http.HttpSession)
	 */
	public static void closeUserSession(final HttpServletRequest servletRequest) {
		closeUserSession(servletRequest.getSession(false));
	}

	/**
	 * Starts user session by storing user session object into http session.
	 */
	public static void startUserSession(final HttpSession httpSession, final Object userSession) {
		httpSession.setAttribute(AUTH_SESSION_NAME, userSession);
	}

	/**
	 * @see #startUserSession(javax.servlet.http.HttpServletRequest, Object)
	 */
	public static void startUserSession(final HttpServletRequest servletRequest, final Object userSession) {
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
	 * Returns <code>null</code> if cookie does not exist.
	 * Throws an exception if cookie data is invalid or corrupted.
	 */
	public static String[] readAuthCookie(final HttpServletRequest request) {
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
	public static void storeAuthCookie(final HttpServletResponse response, final int cookieMaxAge, final String... values) {
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
	public static void removeAuthCookie(final HttpServletRequest servletRequest, final HttpServletResponse servletResponse) {
		Cookie cookie = ServletUtil.getCookie(servletRequest, AUTH_COOKIE_NAME);
		if (cookie == null) {
			return;
		}
		cookie.setMaxAge(0);
		cookie.setPath("/");
		servletResponse.addCookie(cookie);
	}
}
