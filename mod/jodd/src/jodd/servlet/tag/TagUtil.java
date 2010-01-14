// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.tag;

import jodd.io.FastCharArrayWriter;

import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Writer;

/**
 * Various tag utilities.
 */
public class TagUtil {

	private static final String SCOPE_APPLICATION = "application";
	private static final String SCOPE_SESSION = "session";
	private static final String SCOPE_REQUEST = "request";
	private static final String SCOPE_PAGE = "page";

	/**
	 * Invokes tag body.
	 */
	public static void invokeBody(JspFragment body) throws JspException {
		if (body == null) {
			return;
		}
		try {
			body.invoke(null);
		} catch (IOException ioex) {
			throw new JspException("Unable to invoke tag body.", ioex);
		}
	}

	/**
	 * Invokes tag body to provided writer.
	 */
	public static void invokeBody(JspFragment body, Writer writer) throws JspException {
		if (body == null) {
			return;
		}
		try {
			body.invoke(writer);
		} catch (IOException ioex) {
			throw new JspException("Unable to invoke tag body.", ioex);
		}
	}

	/**
	 * Renders tag body to char array.
	 */
	public static char[] renderBody(JspFragment body) throws JspException {
		FastCharArrayWriter writer = new FastCharArrayWriter();
		invokeBody(body, writer);
		return writer.toCharArray();
	}


	/**
	 * Renders tag body to string.
	 * @see #renderBody(javax.servlet.jsp.tagext.JspFragment)
	 */
	public static String renderBodyToString(JspFragment body) throws JspException {
		char[] result = renderBody(body);
		return new String(result);
	}

	/**
	 * Sets scope attribute.
	 */
	public static void setScopeAttribute(String name, Object value, String scope, PageContext pageContext) throws JspException {
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		String scopeValue = scope != null ? scope.toLowerCase() : SCOPE_PAGE;
		if (scopeValue.equals(SCOPE_PAGE)) {
			pageContext.setAttribute(name, value);
		} else if (scopeValue.equals(SCOPE_REQUEST)) {
			request.setAttribute(name, value);
		} else if (scopeValue.equals(SCOPE_SESSION)) {
			request.getSession().setAttribute(name, value);
		} else if (scopeValue.equals(SCOPE_APPLICATION)) {
            request.getSession().getServletContext().setAttribute(name, value);
        } else {
			throw new JspException("Invalid scope: '" + scope + "'.");
        }
	}

	/**
	 * Removes scope attribute.
	 */
	public static void removeScopeAttribute(String name, String scope, PageContext pageContext) throws JspException {
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		String scopeValue = scope != null ? scope.toLowerCase() : SCOPE_PAGE;
		if (scopeValue.equals(SCOPE_PAGE)) {
			pageContext.removeAttribute(name);
		} else if (scopeValue.equals(SCOPE_REQUEST)) {
			request.removeAttribute(name);
		} else if (scopeValue.equals(SCOPE_SESSION)) {
			request.getSession().removeAttribute(name);
		} else if (scopeValue.equals(SCOPE_APPLICATION)) {
            request.getSession().getServletContext().removeAttribute(name);
        } else {
			throw new JspException("Invalid scope: '" + scope + "'.");
        }
	}

}
