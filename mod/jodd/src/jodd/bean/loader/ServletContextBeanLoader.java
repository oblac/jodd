// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean.loader;

import java.util.Enumeration;

import javax.servlet.ServletContext;

/**
 * Populates java bean from ServletContext objects.
 */
public class ServletContextBeanLoader extends BaseBeanLoader {

	public void load(Object bean, Object source) {
		if (source instanceof ServletContext) {

			ServletContext servletContext = (ServletContext) source;

			Enumeration attributeNames = servletContext.getAttributeNames();

			while (attributeNames.hasMoreElements()) {

				String attributeName = (String) attributeNames.nextElement();

				Object value = servletContext.getAttribute(attributeName);

				setProperty(bean, attributeName, value);
			}
		}
	}

}