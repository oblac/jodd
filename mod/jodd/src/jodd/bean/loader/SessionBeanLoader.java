// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean.loader;

import java.util.Enumeration;

import javax.servlet.http.HttpSession;

import jodd.util.StringUtil;

/**
 * Populates java bean from HttpSession objects. It allows to be instanced with a
 * 'prefix' that will be added in front of all attributes.
 */
public class SessionBeanLoader extends BaseBeanLoader {

	protected final String prefix;

	public SessionBeanLoader() {
		this.prefix = null;
	}

	public SessionBeanLoader(String prefix) {
		this.prefix = prefix;
	}

	public void load(Object bean, Object source) {
		if (source instanceof HttpSession) {
			HttpSession session = (HttpSession) source;

			Enumeration attributeNames = session.getAttributeNames();

			while (attributeNames.hasMoreElements()) {
				String attributeName = (String) attributeNames.nextElement();

				Object value = session.getAttribute(attributeName);

				if (prefix != null) {
					attributeName = prefix + StringUtil.capitalize(attributeName);
				}

				beanUtilBean.setPropertyForcedSilent(bean, attributeName, value);
			}
		}
	}

}