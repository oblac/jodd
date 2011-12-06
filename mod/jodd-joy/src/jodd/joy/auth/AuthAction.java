// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.auth;

import jodd.joy.madvoc.action.AppAction;
import jodd.madvoc.meta.Action;

/**
 * Authentication action, usually extended by login action.
 * Defines three actions: login, logout and register.
 * These actions are just 'hooks', so Madvoc can catch them, but
 * methods will not be executed since {@link AuthInterceptor} will
 * consume them. 
 */
public abstract class AuthAction extends AppAction {

	public static final String LOGIN_ACTION_PATH = "/j_login";
	public static final String LOGOUT_ACTION_PATH = "/j_logout";
	public static final String REGISTER_ACTION_PATH = "/j_register";

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
