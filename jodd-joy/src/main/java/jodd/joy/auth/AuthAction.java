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

import jodd.json.JsonObject;
import jodd.madvoc.meta.In;
import jodd.madvoc.meta.scope.Request;
import jodd.madvoc.result.HttpStatus;
import jodd.madvoc.result.JsonResult;
import jodd.petite.meta.PetiteInject;
import jodd.servlet.ServletUtil;
import jodd.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Authentication action, usually extended by login action.
 * Defines three actions: login, logout and register.
 * These actions are just dummy 'hooks' so Madvoc can catch them and
 * invoke interceptor. Methods itself will not be invoked.
 * <p>
 * Usually <code>LoginAction</code> extends this class.
 */
public abstract class AuthAction<T> {

	private static final Logger log = LoggerFactory.getLogger(AuthAction.class);

	public static final String J_LOGIN_PATH = "/j_login";
	public static final String J_LOGOUT_PATH = "/j_logout";

	public static final String PARAM_USERNAME = "j_username";
	public static final String PARAM_PASSWORD = "j_password";

	public static final String ALIAS_INDEX = "<index>";
	public static final String ALIAS_LOGIN = "<login>";

	@In @Request protected HttpServletRequest servletRequest;
	@In @Request protected HttpServletResponse servletResponse;

	@PetiteInject
	protected UserAuth<T> userAuth;

	// ---------------------------------------------------------------- login

	/**
	 * Authenticate user and start user session.
	 */
	protected JsonResult login() {
		T authToken;

		authToken = loginViaBasicAuth(servletRequest);

		if (authToken == null) {
			authToken = loginViaRequestParams(servletRequest);
		}

		if (authToken == null) {
			log.warn("Login failed.");

			return JsonResult.of(HttpStatus.error401().unauthorized("Login failed."));
		}

		log.info("login OK!");

		final UserSession<T> userSession = new UserSession<>(authToken, userAuth.tokenValue(authToken));

		userSession.start(servletRequest, servletResponse);

		// return token

		return tokenAsJson(authToken);
	}

	/**
	 * Prepares the JSON payload that carries on the token value.
	 */
	protected JsonResult tokenAsJson(final T authToken) {
		final JsonObject jsonObject = new JsonObject();

		jsonObject.put("token", userAuth.tokenValue(authToken));

		return JsonResult.of(jsonObject);
	}

	/**
	 * Tries to login user with form data. Returns session object, otherwise returns <code>null</code>.
	 */
	protected T loginViaRequestParams(final HttpServletRequest servletRequest) {
		final String username = servletRequest.getParameter(PARAM_USERNAME).trim();
		if (StringUtil.isEmpty(username)) {
			return null;
		}
		final String password = servletRequest.getParameter(PARAM_PASSWORD).trim();

		return userAuth.login(username, password);
	}

	/**
	 * Tries to login user with basic authentication.
	 */
	protected T loginViaBasicAuth(final HttpServletRequest servletRequest) {
		final String username = ServletUtil.resolveAuthUsername(servletRequest);
		if (username == null) {
			return null;
		}
		final String password = ServletUtil.resolveAuthPassword(servletRequest);

		return userAuth.login(username, password);
	}

	// ---------------------------------------------------------------- logout

	/**
	 * Logout hook.
	 */
	protected JsonResult logout() {
		log.debug("logout user");

		UserSession.stop(servletRequest, servletResponse);

		return JsonResult.of(HttpStatus.ok());
	}


}
