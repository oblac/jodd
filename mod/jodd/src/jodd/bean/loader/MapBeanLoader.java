// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean.loader;

import java.util.Map;

import jodd.bean.BeanUtil;

/**
 * Populate java bean using objects that are implementation of Map interface.
 * <p>
 * Properties in Map object are defined as follows:
 * each key of Map object is a string the represents a bean property name and
 * keys value is an object that represents bean property value.
 */
public class MapBeanLoader implements BeanLoader {

	public static void loadBean(Object bean, Object map) {
		if (map instanceof Map) {
			for (Object o : ((Map) map).keySet()) {
				String propertyName = (String) o;
				Object propertyValue = ((Map) map).get(propertyName);
				if (propertyValue == null) {
					return;
				}
				BeanUtil.setPropertyForcedSilent(bean, propertyName, propertyValue);
			}
		}
	}

	public void load(Object bean, Object map) {
		loadBean(bean, map);
	}

}
