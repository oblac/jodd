// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.tag;

import jodd.exception.UncheckedException;
import jodd.io.FastCharArrayWriter;
import jodd.servlet.ServletUtil;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.JspException;
import java.io.IOException;
import java.io.Writer;

/**
 * Various tag utilities.
 */
public class TagUtil {

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
		try {
			ServletUtil.setScopeAttribute(name, value, scope, pageContext);
		} catch (UncheckedException uex) {
			throw new JspException(uex);
        }
	}

	/**
	 * Removes scope attribute.
	 */
	public static void removeScopeAttribute(String name, String scope, PageContext pageContext) throws JspException {
		try {
			ServletUtil.removeScopeAttribute(name, scope, pageContext);
		} catch (UncheckedException uex) {
			throw new JspException(uex);
        }
	}

}
