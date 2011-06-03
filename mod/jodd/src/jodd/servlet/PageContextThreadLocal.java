// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet;

import javax.servlet.jsp.PageContext;

/**
 * Thread local storage for <code>PageContext</code>.
 */
public class PageContextThreadLocal {

	private static final ThreadLocal<PageContext> THREAD_LOCAL = new ThreadLocal<PageContext>();

	/**
	 * Sets page context to thread local variable.
	 */
	public static void set(PageContext pageContext) {
		THREAD_LOCAL.set(pageContext);
	}

	/**
	 * Returns page context from thread local variable.
	 */
	public static PageContext get() {
		return THREAD_LOCAL.get();
	}
}
