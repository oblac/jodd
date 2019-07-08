// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.servlet.tag;

import jodd.exception.UncheckedException;
import jodd.io.FastCharArrayWriter;
import jodd.servlet.ServletUtil;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import java.io.IOException;
import java.io.Writer;

/**
 * Various tag utilities.
 */
public class TagUtil {

	/**
	 * Invokes tag body.
	 */
	public static void invokeBody(final JspFragment body) throws JspException {
		if (body == null) {
			return;
		}
		try {
			body.invoke(null);
		} catch (IOException ioex) {
			throw new JspException("Tag body failed", ioex);
		}
	}

	/**
	 * Invokes tag body to provided writer.
	 */
	public static void invokeBody(final JspFragment body, final Writer writer) throws JspException {
		if (body == null) {
			return;
		}
		try {
			body.invoke(writer);
		} catch (IOException ioex) {
			throw new JspException("Tag body failed", ioex);
		}
	}

	/**
	 * Renders tag body to char array.
	 */
	public static char[] renderBody(final JspFragment body) throws JspException {
		FastCharArrayWriter writer = new FastCharArrayWriter();
		invokeBody(body, writer);
		return writer.toCharArray();
	}


	/**
	 * Renders tag body to string.
	 * @see #renderBody(javax.servlet.jsp.tagext.JspFragment)
	 */
	public static String renderBodyToString(final JspFragment body) throws JspException {
		char[] result = renderBody(body);
		return new String(result);
	}

	/**
	 * Sets scope attribute.
	 */
	public static void setScopeAttribute(final String name, final Object value, final String scope, final PageContext pageContext) throws JspException {
		try {
			ServletUtil.setScopeAttribute(name, value, scope, pageContext);
		} catch (UncheckedException uex) {
			throw new JspException(uex);
        }
	}

	/**
	 * Removes scope attribute.
	 */
	public static void removeScopeAttribute(final String name, final String scope, final PageContext pageContext) throws JspException {
		try {
			ServletUtil.removeScopeAttribute(name, scope, pageContext);
		} catch (UncheckedException uex) {
			throw new JspException(uex);
        }
	}

}
