// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean.loader;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

/**
 * Populates java bean from servlet request parameters and attributes. Parameters
 * are populated by using {@link RequestParamBeanLoader}.
 */
public class RequestBeanLoader extends BaseBeanLoader {

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
				if (paramValues.length == 1) {	// use just String
					setProperty(bean, paramName, paramValues[0]);
				} else {						// use String array
					setProperty(bean, paramName, paramValues);
				}
			}

			Enumeration attributeNamesttribNames = request.getAttributeNames();

			while (attributeNamesttribNames.hasMoreElements()) {
				String attributeName = (String) attributeNamesttribNames.nextElement();

				Object value = request.getAttribute(attributeName);

				setProperty(bean, attributeName, value);
			}
		}
	}

}