// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

/**
 * Action definition is represented by action's path, http method and result base path.
 */
public class ActionDef {

	protected final String actionPath;
	protected final String actionMethod;
	protected final String resultBasePath;

	public ActionDef(String actionPath, String actionMethod, String resultBasePath) {
		this.actionPath = actionPath;
		this.actionMethod = actionMethod;
		this.resultBasePath = resultBasePath == null ? actionPath : resultBasePath;
	}

	public ActionDef(String actionPath, String actionMethod) {
		this.actionPath = actionPath;
		this.actionMethod = actionMethod;
		this.resultBasePath = actionPath;
	}

	public ActionDef(String actionPath) {
		this(actionPath, null);
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

	/**
	 * Returns result base path.
	 */
	public String getResultBasePath() {
		return resultBasePath;
	}
}