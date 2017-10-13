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

import jodd.madvoc.meta.Action;

/**
 * Authentication action, usually extended by login action.
 * Defines three actions: login, logout and register.
 * These actions are just dummy 'hooks' so Madvoc can catch them and
 * invoke interceptor. Methods itself will not be invoked.
 * <p>
 * Usually <code>LoginAction</code> extends this class.
 */
public abstract class AuthAction {

	public static final String LOGIN_ACTION_PATH = "/j_login";
	public static final String LOGOUT_ACTION_PATH = "/j_logout";
	public static final String REGISTER_ACTION_PATH = "/j_register";

	public static final String LOGIN_USERNAME = "j_username";
	public static final String LOGIN_PASSWORD = "j_password";
	public static final String LOGIN_TOKEN = "j_token";
	public static final String LOGIN_SUCCESS_PATH = "j_path";

	public static final String ALIAS_INDEX = "<index>";
	public static final String ALIAS_LOGIN = "<login>";
	public static final String ALIAS_LOGIN_NAME = "login";
	public static final String ALIAS_ACCESS_DENIED = "<accessDenied>";
	public static final String ALIAS_ACCESS_DENIED_NAME = "accessDenied";

	/**
	 * Login hook.
	 */
	@Action(value = LOGIN_ACTION_PATH, method = "POST")
	public final void login() {
	}

	/**
	 * Logout hook.
	 */
	@Action(LOGOUT_ACTION_PATH)
	public final void logout() {
	}

	/**
	 * Register hook.
	 */
	@Action(REGISTER_ACTION_PATH)
	public final void register() {
	}

}
