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

package jodd.servlet;

import jodd.bean.JoddBean;
import jodd.bean.BeanUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

/**
 * Resolves values and attributes on JSP page.
 * May be used by it's static methods, but you need to provide
 * either <code>PageContext</code> or <code>ServletRequest</code>
 * every time. The other approach is to create the instance
 * of <code>JspResolver</code> on top of the page and then
 * reuse it later on.
 */
public class JspResolver {

	protected final HttpServletRequest servletRequest;
	protected final PageContext pageContext;

	public JspResolver(HttpServletRequest servletRequest) {
		this.pageContext = null;
		this.servletRequest = servletRequest;
	}

	public JspResolver(PageContext pageContext) {
		this.pageContext = pageContext;
		this.servletRequest = (HttpServletRequest) pageContext.getRequest();
	}

	// ---------------------------------------------------------------- resolves values

	/**
	 * Resolves values: attributes and parameters.
	 */
	public Object value(String name) {
		if (pageContext != null) {
			return value(name, pageContext);
		}
		return value(name, servletRequest);
	}

	/**
	 * Resolves value from scopes.
	 * @see jodd.servlet.ServletUtil#value(HttpServletRequest, String)
	 */
	public static Object value(String name, HttpServletRequest request) {
		String thisRef = BeanUtil.extractThisReference(name);
		Object value = ServletUtil.value(request, thisRef);
		if (value == null) {
			return ServletUtil.value(request, name);
		}
		return result(name, thisRef, value);
	}

	public static Object value(String name, PageContext pageContext) {
		String thisRef = BeanUtil.extractThisReference(name);
		Object value = ServletUtil.value(pageContext, thisRef);
		if (value == null) {
			return ServletUtil.value(pageContext, name);
		}
		return result(name, thisRef, value);
	}

	// ---------------------------------------------------------------- resolves attributes


	/**
	 * Resolves attribute value from scopes.
	 */
	public Object attribute(String name) {
		if (pageContext != null) {
			return attribute(name, pageContext);
		}
		return attribute(name, servletRequest);
	}

	/**
	 * Resolves attribute value from scopes.
	 * @see jodd.servlet.ServletUtil#attribute(HttpServletRequest, String)
	 */
	public static Object attribute(String name, HttpServletRequest request) {
		String thisRef = BeanUtil.extractThisReference(name);
		Object value = ServletUtil.attribute(request, thisRef);
		if (value == null) {
			return ServletUtil.attribute(request, name);
		}
		return result(name, thisRef, value);

	}

	public static Object attribute(String name, PageContext pageContext) {
		String thisRef = BeanUtil.extractThisReference(name);
		Object value = ServletUtil.attribute(pageContext, thisRef);
		if (value == null) {
			return ServletUtil.attribute(pageContext, name);
		}
		return result(name, thisRef, value);

	}

	// ---------------------------------------------------------------- tools

	private static Object result(String name, String thisRef, Object value) {
		if (thisRef.equals(name)) {
			return value;
		}

		name = name.substring(thisRef.length() + 1);
		return BeanUtil.getDeclaredPropertySilently(value, name);
	}

}