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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * User session object stored in the HTTP session.
 */
public class UserSession<T> {

	private static final String AUTH_SESSION_NAME = UserSession.class.getName();
	private static final String AUTH_COOKIE_NAME = "JODD_JOY_SESSION";

	/**
	 * Cookie max age, when cookies are used.
	 * By default set to 14 days.
	 */
	protected int cookieMaxAge = 14 * 24 * 60 * 60;

	private T authToken;
	private String authTokenValue;

	// ---------------------------------------------------------------- start/stop/get

	/**
	 * Retrieves the user session from the HTTP session. Returns {@code null}
	 * if no session found.
	 */
	public static UserSession get(final HttpServletRequest httpServletRequest) {
		final HttpSession httpSession = httpServletRequest.getSession(false);
		if (httpSession == null) {
			return null;
		}
		return (UserSession) httpSession.getAttribute(AUTH_SESSION_NAME);
	}

	/**
	 * Stops the user session by removing it from the http session and invalidating the cookie.
	 */
	public static void stop(final HttpServletRequest servletRequest, final HttpServletResponse servletResponse) {
		final HttpSession httpSession = servletRequest.getSession(false);
		if (httpSession != null) {
			httpSession.removeAttribute(AUTH_SESSION_NAME);
		}

		final Cookie cookie = ServletUtil.getCookie(servletRequest, AUTH_COOKIE_NAME);
		if (cookie == null) {
			return;
		}
		cookie.setMaxAge(0);
		cookie.setPath("/");
		servletResponse.addCookie(cookie);
	}

	/**
	 * Starts new user session.
	 */
	public void start(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) {
		final HttpSession httpSession = httpServletRequest.getSession(true);
		httpSession.setAttribute(AUTH_SESSION_NAME, this);

		final Cookie cookie = new Cookie(AUTH_COOKIE_NAME, authTokenValue);
		//cookie.setDomain(SSORealm.SSO_DOMAIN);
		cookie.setMaxAge(cookieMaxAge);
		cookie.setPath("/");
		httpServletResponse.addCookie(cookie);
	}

	// ---------------------------------------------------------------- bean

	/**
	 * Creates new user session.
	 */
	public UserSession(final T authToken, final String authTokenValue) {
		this.authToken = authToken;
		this.authTokenValue = authTokenValue;
	}

	/**
	 * Returns associated user token.
	 */
	public T getAuthToken() {
		return authToken;
	}

}
