// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

/**
 * Action ID is represented by action's path and http method.
 */
public class ActionId {

	protected final String actionPath;
	protected final String actionMethod;

	public ActionId(String actionPath, String actionMethod) {
		this.actionPath = actionPath;
		this.actionMethod = actionMethod;
	}

	public ActionId(String actionPath) {
		this.actionPath = actionPath;
		this.actionMethod = null;
	}

	/**
	 * Returns action's path.
	 */
	public String getActionPath() {
		return actionPath;
	}

	/**
	 * Returns action's HTTP method.
	 */
	public String getActionMethod() {
		return actionMethod;
	}
}