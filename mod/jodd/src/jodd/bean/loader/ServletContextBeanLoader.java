// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean.loader;

import java.util.Enumeration;

import javax.servlet.ServletContext;

import jodd.util.StringUtil;

/**
 * Populates java bean from ServletContext objects. It allows to be instanced with a
 * 'prefix' that will be added in front of all attributes.
 */
public class ServletContextBeanLoader extends BaseBeanLoader {

	protected final String prefix;

	public ServletContextBeanLoader() {
		this.prefix = null;
	}

	public ServletContextBeanLoader(String prefix) {
		this.prefix = prefix;
	}

	public void load(Object bean, Object source) {
		if (source instanceof ServletContext) {

			ServletContext servletContext = (ServletContext) source;

			Enumeration attributeNames = servletContext.getAttributeNames();

			while (attributeNames.hasMoreElements()) {

				String attributeName = (String) attributeNames.nextElement();

				Object value = servletContext.getAttribute(attributeName);

				if (prefix != null) {
					attributeName = prefix + StringUtil.capitalize(attributeName);
				}

				beanUtilBean.setPropertyForcedSilent(bean, attributeName, value);
			}
		}
	}

}