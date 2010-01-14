// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean.modifier;

import jodd.bean.BeanUtil;
import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;

/**
 * Simple bean modifier that trims all String parameters of given java bean object.
 */
public class TrimStringsBeanModifier implements BeanModifier {

	/**
	 * {@inheritDoc}
	 */
	public void modify(Object bean) {
		if (bean == null) {
			return;
		}
		ClassDescriptor bcd = ClassIntrospector.lookup(bean.getClass());
		String[] getters = bcd.getAllBeanGetterNames();
		for (String getter : getters) {
			if (bcd.getBeanSetter(getter) == null) {
				continue;
			}
			onProperty(bean, getter);
		}
	}

	void onProperty(Object obj, String name) {
		try {
			Object value = BeanUtil.getProperty(obj, name);
			if (value == null) {
				return;
			}
			if (value instanceof String) {						// trim String parameter
				value = ((String)value).trim();
				BeanUtil.setProperty(obj, name, value);
			} else if (value.getClass().isArray() == true) {
				if (value instanceof String[]) {				// trim String[] parameter
					String[] valueArray = (String[]) value;
					for (int i = 0; i < valueArray.length; i++) {
						valueArray[i] = valueArray[i].trim();
					}
				} else {
					Object[] valueArray = (Object[]) value;		// trim Strings in Object[] parameter
					for (int i = 0; i < valueArray.length; i++) {
						if (valueArray[i] instanceof String) {
							valueArray[i] = ((String)valueArray[i]).trim();
						}
					}
				}
			}
		} catch (Exception ex) {
			// can't trim property, just go out.
		}
	}
}
