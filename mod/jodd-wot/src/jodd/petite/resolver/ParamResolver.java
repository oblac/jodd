// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.resolver;

import jodd.util.PropertiesUtil;
import jodd.util.StringPool;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * Parameter storage and resolver.
 */
public class ParamResolver {

	protected final Map<String, Object> params;

	public ParamResolver() {
		params = new HashMap<String, Object>();
	}

	public void put(String name, Object value) {
		params.put(name, value);
	}

	public Object get(String name) {
		return params.get(name);
	}

	/**
	 * Returns an array of param keys that belongs to provided bean.
	 */
	public String[] resolve(String beanName, boolean resolveReferenceParams) {
		beanName = beanName + '.';
		List<String> list = new ArrayList<String>();
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			String key = entry.getKey();
			if (key.startsWith(beanName) == false) {
				continue;
			}
			list.add(key);
			if (resolveReferenceParams == false) {
				continue;
			}
			// resolve all references
			String value = PropertiesUtil.resolveProperty(params, key);
			entry.setValue(value);
		}
		if (list.isEmpty()) {
			return StringPool.EMPTY_ARRAY;
		} else {
			return list.toArray(new String[list.size()]);
		}
	}
}
