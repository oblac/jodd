// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.auth;

import jodd.joy.madvoc.action.AppAction;
import jodd.madvoc.meta.Action;

/**
 * Authentication action, usually extended by login action.
 * Defines three actions: login, logout and register.
 * These actions are just dummy 'hooks' so Madvoc can catch them and
 * invoke interceptor. Methods itself will not be invoked.
 * <p>
 * Usually <code>LoginAction</code> extends this class.
 */
public abstract class AuthAction extends AppAction {

	public static final String LOGIN_ACTION_PATH = "/j_login";
	public static final String LOGOUT_ACTION_PATH = "/j_logout";
	public static final String REGISTER_ACTION_PATH = "/j_register";

	public static final String LOGIN_USERNAME = "j_username";
	public static final String LOGIN_PASSWORD = "j_password";
	public static final String LOGIN_TOKEN = "j_token";
	public static final String LOGIN_SUCCESS_PATH = "j_path";

	/**
	 * Login hook.
	 */
	@Action(value = LOGIN_ACTION_PATH, method = METHOD_POST)
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
