// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

/**
 * Action definition is represented by action's path and http method.
 */
public class ActionDef {

	protected final String actionPath;
	protected final String actionMethod;

	public ActionDef(String actionPath, String actionMethod) {
		this.actionPath = actionPath;
		this.actionMethod = actionMethod;
	}

	public ActionDef(String actionPath) {
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