// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean.loader;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

/**
 * Populates java bean from servlet request parameters and attributes. Parameters
 * are populated by using {@link RequestParamBeanLoader}.
 */
public class RequestBeanLoader extends BaseBeanLoader {

	protected final boolean trim;

	public RequestBeanLoader() {
		this.trim = false;
	}

	public RequestBeanLoader(boolean trim) {
		this.trim = trim;
	}

	public void load(Object bean, Object source) {
		HttpServletRequest request = (HttpServletRequest) source;

		if (source instanceof HttpServletRequest) {
			Enumeration paramNames = request.getParameterNames();

			while (paramNames.hasMoreElements()) {
				String paramName = (String) paramNames.nextElement();
				String[] paramValues = request.getParameterValues(paramName);
				if (paramValues == null) {
					continue;
				}
				if (paramValues.length == 0) {
					continue;
				}
				if (trim == true) {
					for (int i = 0; i < paramValues.length; i++) {
						paramValues[i] = paramValues[i].trim();
					}
				}
				if (paramValues.length == 1) {	// use just String
					beanUtilBean.setPropertyForcedSilent(bean, paramName, paramValues[0]);
				} else {						// use String array
					beanUtilBean.setPropertyForcedSilent(bean, paramName, paramValues);
				}
			}

			Enumeration attributeNamesttribNames = request.getAttributeNames();

			while (attributeNamesttribNames.hasMoreElements()) {
				String attributeName = (String) attributeNamesttribNames.nextElement();

				Object value = request.getAttribute(attributeName);

				beanUtilBean.setPropertyForcedSilent(bean, attributeName, value);
			}
		}
	}

}