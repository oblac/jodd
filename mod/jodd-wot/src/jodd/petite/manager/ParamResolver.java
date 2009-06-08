// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.manager;

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

	protected static final String[] EMPTY_PARAMS = new String[0];	// saves memory

	public ParamResolver() {
		params = new HashMap<String, Object>();
	}

	public void put(String name, Object value) {
		params.put(name, value);
	}

	public Object get(String name) {
		return params.get(name);
	}

	public String[] resolve(String beanName, boolean resolveReferenceParams) {
		beanName = beanName + '.';
		List<String> list = new ArrayList<String>();
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			if (entry.getKey().startsWith(beanName)) {
				list.add(entry.getKey());
				Object value = entry.getValue();
				if (resolveReferenceParams == false) {
					continue;
				}
				// resolve all references
				String name;
				while (true) {
					if ((value != null) && (value instanceof String)) {		// inspect only strings
						String strValue = ((String) value).trim();
						if (strValue.startsWith(StringPool.DOLLAR)) {
							name = strValue.substring(1);
							if (name.startsWith(StringPool.DOLLAR)) {		// escaped with double $
								value = name;
								entry.setValue(value);
								break;
							}
							value = params.get(name);
							entry.setValue(value);
							continue;
						}
					}
					break;
				}
			}
		}
		if (list.isEmpty()) {
			return EMPTY_PARAMS;
		} else {
			return list.toArray(new String[list.size()]);
		}
	}
}
