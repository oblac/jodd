// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import javax.servlet.http.HttpServletRequest;

/**
 * Rewrites action path.
 */
public class ActionPathRewriter {

	/**
	 * Rewrites action path.
	 */
	@SuppressWarnings({"UnusedDeclaration"})
	public String rewrite(HttpServletRequest servletRequest, String actionPath, String httpMethod) {
		return actionPath;
	}
}
