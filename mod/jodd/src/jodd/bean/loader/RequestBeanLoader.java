// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean.loader;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import jodd.bean.BeanUtil;

/**
 * Populates java bean from servlet request parameters and attributes. Parameters
 * are populated by using {@link RequestParamBeanLoader}.
 */
public class RequestBeanLoader implements BeanLoader {
	private boolean trim;

	public RequestBeanLoader() {
	}

	public RequestBeanLoader(boolean trim) {
		this.trim = trim;
	}

	public static void loadBean(Object bean, Object request, boolean trim) {
		RequestParamBeanLoader.loadBean(bean, request, trim);
		if (request instanceof HttpServletRequest) {
			Enumeration attribNames = ((HttpServletRequest)request).getAttributeNames();
			while (attribNames.hasMoreElements()) {
				String attribName = (String) attribNames.nextElement();
				Object value = ((HttpServletRequest)request).getAttribute(attribName);
				if (value == null) {
					continue;
				}
				BeanUtil.setPropertyForcedSilent(bean, attribName, value);
			}
		}
	}

	public void load(Object bean, Object request) {
		loadBean(bean, request, trim);
	}

}
