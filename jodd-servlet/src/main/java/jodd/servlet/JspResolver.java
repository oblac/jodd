// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet;

import jodd.bean.BeanUtil;
import jodd.bean.BeanUtilBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

/**
 * Resolves values and attributes on JSP page.
 * May be used by it's static methods, but you need to provide
 * either <code>PageContext</code> or <code>ServletRequest</code>
 * every time. The other scenario is to create the instance
 * of <code>JspResolver</code> on top of the page, and then
 * use it later on.
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

	/**
	 * Resolves value from scope using page context of this thread.
	 */
	public static Object resolveValue(String name) {
		return value(name, ServletUtil.getPageContextFromThread());
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

	public static Object resolveAttribute(String attributeName) {
		return attribute(attributeName, ServletUtil.getPageContextFromThread());
	}

	// ---------------------------------------------------------------- tools

	private static Object result(String name, String thisRef, Object value) {
		if (thisRef.equals(name)) {
			return value;
		}
		name = BeanUtilBean.THIS_REF + name.substring(thisRef.length());
		return BeanUtil.getDeclaredPropertySilently(value, name);
	}

}
