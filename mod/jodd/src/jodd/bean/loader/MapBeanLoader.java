// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean.loader;

import java.util.Map;

/**
 * Populate java bean using implementation of <code>Map</code>.
 * <p>
 * For each key of <code>Map</code>, it's <code>toString</code> method
 * is called to get property name.
 */
public class MapBeanLoader extends BaseBeanLoader {

	@SuppressWarnings({"unchecked"})
	public void load(Object bean, Object source) {

		if (source instanceof Map) {
			Map<Object, Object> map = (Map<Object, Object>) source;

			for (Map.Entry<Object, Object> entry : map.entrySet()) {

				String name = entry.getKey().toString();

				Object value = entry.getValue();

				setProperty(bean, name, value);
			}
		}
	}

}