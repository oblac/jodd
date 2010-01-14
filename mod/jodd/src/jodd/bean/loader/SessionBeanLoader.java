// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean.loader;

import java.util.Enumeration;

import javax.servlet.http.HttpSession;

import jodd.bean.BeanUtil;

/**
 * Populates java bean from HttpSession objects. It allows to be instanced with a
 * 'prefix' that will be added in front of all attributes.
 */
public class SessionBeanLoader implements BeanLoader {

	public SessionBeanLoader() {
	}

	String prefix;

	public SessionBeanLoader(String prefix) {
		this.prefix = prefix;
	}

	public static void loadBean(Object bean, Object session, String prefix) {
		if (session instanceof HttpSession) {

			Enumeration attribNames = ((HttpSession)session).getAttributeNames();
			while (attribNames.hasMoreElements()) {
				String attribName = (String) attribNames.nextElement();
				Object value = ((HttpSession)session).getAttribute(attribName);
				if (value == null) {
					continue;
				}
				if (prefix != null) {
					attribName = prefix + Character.toUpperCase(attribName.charAt(0)) + attribName.substring(1);
				}
				BeanUtil.setPropertyForcedSilent(bean, attribName, value);
			}
		}
	}

	public void load(Object bean, Object session) {
		loadBean(bean, session, prefix);
	}

}
