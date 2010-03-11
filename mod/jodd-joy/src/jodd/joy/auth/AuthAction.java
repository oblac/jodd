package jodd.joy.auth;

import jodd.joy.madvoc.action.AppAction;
import jodd.madvoc.meta.Action;

/**
 * Authentication action, usually extended by login action.
 * Defines two actions: login and logout. 
 */
public abstract class AuthAction extends AppAction {

	public static final String LOGIN_ACTION_PATH = "/j_login";
	public static final String LOGOUT_ACTION_PATH = "/j_logout";

	@Action(LOGIN_ACTION_PATH)
	public void login() {
	}

	@Action(LOGOUT_ACTION_PATH)
	public void logout() {
	}

}
