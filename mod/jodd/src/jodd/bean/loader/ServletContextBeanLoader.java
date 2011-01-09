// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean.loader;

import java.util.Enumeration;

import javax.servlet.ServletContext;

import jodd.bean.BeanUtil;

/**
 * Populates java bean from ServletContext objects. It allows to be instanced with a
 * 'prefix' that will be added in front of all attributes.
 */
public class ServletContextBeanLoader implements BeanLoader {

	public ServletContextBeanLoader() {
	}

	String prefix;

	public ServletContextBeanLoader(String prefix) {
		this.prefix = prefix;
	}

	public static void loadBean(Object bean, Object context, String prefix) {
		if (context instanceof ServletContext) {

			Enumeration attribNames = ((ServletContext)context).getAttributeNames();
			while (attribNames.hasMoreElements()) {
				String attribName = (String) attribNames.nextElement();
				Object value = ((ServletContext)context).getAttribute(attribName);
				if (value == null) {
					continue;
				}
				if (prefix != null) {
					attribName = prefix + Character.toUpperCase(attribName.charAt(0)) + attribName.substring(1);
				}
				try {
					BeanUtil.setPropertyForcedSilent(bean, attribName, value);
				} catch (Exception ex) {
					// ignore exception
				}
			}
		}
	}

	public void load(Object bean, Object context) {
		loadBean(bean, context, prefix);
	}

}
