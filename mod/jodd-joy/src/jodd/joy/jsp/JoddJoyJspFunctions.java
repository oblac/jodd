// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.jsp;

import jodd.servlet.PageContextThreadLocal;
import jodd.servlet.ServletUtil;

import javax.servlet.jsp.PageContext;

/**
 * Some jsp functions.
 */
public class JoddJoyJspFunctions {

	private static final String CTX_VAR_NAME = "CTX";

	/**
	 * Performs page initialization. The following is done:
	 * <ul>PageContextThreadLocal is set</ul>
	 * <ul>CTX page attribute is set with context path value</ul>
	 * <ul>CTX request attribute is set with context path value</ul>
	 */
	public static void initPage(PageContext pageContext) {
		PageContextThreadLocal.set(pageContext);
		String ctxPath = ServletUtil.getContextPath(pageContext);
		pageContext.setAttribute(CTX_VAR_NAME, ctxPath);
		pageContext.getRequest().setAttribute(CTX_VAR_NAME, ctxPath);
	}
}
