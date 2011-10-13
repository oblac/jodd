// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.decora;

import javax.servlet.http.HttpServletRequest;

/**
 * Decora manager defines if some request should be decorated and resolves decorators paths.
 */
public class DecoraManager {

	public static final String DEFAULT_DECORATOR = "/decora/main.jsp";

	// ---------------------------------------------------------------- properties

	protected boolean decorateErrorPages;

	public boolean isDecorateErrorPages() {
		return decorateErrorPages;
	}

	public void setDecorateErrorPages(boolean decorateErrorPages) {
		this.decorateErrorPages = decorateErrorPages;
	}

	// ---------------------------------------------------------------- check

	/**
	 * Determines if a request should be decorated.
	 * By default returns <code>true</code>.
	 */
	public boolean decorateRequest(HttpServletRequest request) {
		return true;
	}

	/**
	 * Determines if some content type should be decorated.
	 * By default returns <code>true</code>.
	 */
	public boolean decorateContentType(String contentType, String mimeType, String encoding) {
		return true;
	}

	/**
	 * Determines if buffering should be used for some HTTP status code.
	 * By default returns <code>true</code> for status code 200 and, optionally,
	 * for error pages (status code >= 400).
	 */
	public boolean decorateStatusCode(int statusCode) {
		return (statusCode == 200) || (decorateErrorPages && statusCode >= 400);
	}

	// ---------------------------------------------------------------- find

	/**
	 * Resolves decorator path based on request and action path.
	 * If decorator is not found, returns <code>null</code>.
	 * By default applies decorator on all *.html pages.
	 */
	public String resolveDecorator(HttpServletRequest request, String actionPath) {
		if (actionPath.endsWith(".html") == true) {
			return DEFAULT_DECORATOR;
		}
		return null;
	}

}
