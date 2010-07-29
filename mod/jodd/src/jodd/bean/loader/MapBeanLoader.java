// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean.loader;

import java.util.Map;

import jodd.bean.BeanUtil;

/**
 * Populate java bean using objects that are implementation of Map interface.
 * <p>
 * Properties in Map object are defined as follows:
 * each key of Map object is a <code>String</code> the represents a bean property name and
 * keys value is an object that represents bean property value.
 */
public class MapBeanLoader implements BeanLoader {

	@SuppressWarnings({"unchecked"})
	public static void loadBean(Object bean, Object mapObj) {
		if (mapObj instanceof Map) {
			Map<String, Object> map = (Map<String, Object>) mapObj;
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				Object propertyValue = entry.getValue();
				if (propertyValue == null) {
					continue;
				}
				BeanUtil.setPropertyForcedSilent(bean, entry.getKey(), propertyValue);
			}
		}
	}

	public void load(Object bean, Object map) {
		loadBean(bean, map);
	}

}
