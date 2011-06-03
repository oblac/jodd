// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.jsp;

import jodd.servlet.PageContextThreadLocal;
import jodd.servlet.jspfn.JoddJspFunctions;

import javax.servlet.jsp.PageContext;

/**
 * Some jsp functions.
 */
public class JoddJoyJspFunctions {

	/**
	 * Performs page initialization. The following is done:
	 * <ul>PageContextThreadLocal is set</ul>
	 * <ul>CTX page variable is set with context path value</ul>
	 */
	public static void initPage(PageContext pageContext) {
		PageContextThreadLocal.set(pageContext);
		JoddJspFunctions.setContextPathVariable(pageContext, "page", "CTX");
	}
}
