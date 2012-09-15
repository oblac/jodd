// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean.loader;

import java.util.Enumeration;

import javax.servlet.http.HttpSession;

/**
 * Populates java bean from HttpSession objects.
 */
public class SessionBeanLoader extends BaseBeanLoader {

	public void load(Object bean, Object source) {
		if (source instanceof HttpSession) {
			HttpSession session = (HttpSession) source;

			Enumeration attributeNames = session.getAttributeNames();

			while (attributeNames.hasMoreElements()) {
				String attributeName = (String) attributeNames.nextElement();

				Object value = session.getAttribute(attributeName);

				setProperty(bean, attributeName, value);
			}
		}
	}

}