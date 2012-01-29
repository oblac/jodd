// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet;

import jodd.bean.BeanUtil;
import jodd.bean.BeanUtilBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

/**
 * Resolves values on JSP page.
 */
public class JspValueResolver {

	protected final HttpServletRequest servletRequest;
	protected final PageContext pageContext;

	public JspValueResolver(HttpServletRequest servletRequest) {
		this.pageContext = null;
		this.servletRequest = servletRequest;
	}

	public JspValueResolver(PageContext pageContext) {
		this.pageContext = pageContext;
		this.servletRequest = (HttpServletRequest) pageContext.getRequest();
	}

	// ---------------------------------------------------------------- resolves values

	/**
	 * Resolves values: attributes and parameters.
	 */
	public Object value(String name) {
		if (pageContext != null) {
			return resolveValue(name, pageContext);
		}
		return resolveValue(name, servletRequest);
	}

	/**
	 * Resolves value from scopes.
	 * @see jodd.servlet.ServletUtil#value(HttpServletRequest, String)
	 */
	public static Object resolveValue(String valueName, HttpServletRequest request) {
		return ServletUtil.value(request, valueName);
	}

	public static Object resolveValue(String valueName, PageContext page) {
		return ServletUtil.value(page, valueName);
	}

	public static Object resolveValue(String valueName) {
		return resolveValue(valueName, PageContextThreadLocal.get());
	}


	// ---------------------------------------------------------------- resolves attributes


	/**
	 * Resolves attribute value from scopes.
	 */
	public Object attr(String name) {
		if (pageContext != null) {
			return resolveAttribute(name, pageContext);
		}
		return resolveAttribute(name, servletRequest);
	}

	/**
	 * Resolves attribute value from scopes.
	 * @see jodd.servlet.ServletUtil#attrValue(HttpServletRequest, String)
	 */
	public static Object resolveAttribute(String attributeName, HttpServletRequest request) {
		return ServletUtil.attrValue(request, attributeName);
	}

	public static Object resolveAttribute(String attributeName, PageContext page) {
		return ServletUtil.attrValue(page, attributeName);
	}

	public static Object resolveAttribute(String attributeName) {
		return resolveAttribute(attributeName, PageContextThreadLocal.get());
	}

	// ---------------------------------------------------------------- reflection

	/**
	 * Resolves property values from all scopes.
	 * @see #resolveProperty(String, HttpServletRequest)
	 */
	public Object property(String name) {
		if (pageContext != null) {
			return resolveProperty(name, pageContext);
		}
		return resolveProperty(name, servletRequest);
	}


	/**
	 * Resolves property from scopes. Property names contains special characters
	 * such as dot, square bracket etc.
	 */
	public static Object resolveProperty(String name, HttpServletRequest request) {
		String thisRef = BeanUtil.extractThisReference(name);
		Object value = resolveValue(thisRef, request);
		if (value == null) {
			return null;
		}
		//noinspection StringEquality
		if (thisRef == name) {
			return value;
		}
		name = BeanUtilBean.THIS_REF + name.substring(thisRef.length());
		return BeanUtil.getDeclaredPropertySilently(value, name);
	}


	/**
	 * Resolves property from scopes. Property names contains special characters
	 * such as dot, square bracket etc.
	 */
	public static Object resolveProperty(String name, PageContext page) {
		String thisRef = BeanUtil.extractThisReference(name);
		Object value = resolveValue(thisRef, page);
		if (value == null) {
			return null;
		}
		//noinspection StringEquality
		if (thisRef == name) {
			return value;
		}
		name = BeanUtilBean.THIS_REF + name.substring(thisRef.length());
		return BeanUtil.getDeclaredPropertySilently(value, name);
	}

	public static Object resolveProperty(String name) {
		return resolveProperty(name, PageContextThreadLocal.get());
	}

}
