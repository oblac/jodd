// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom;

import java.util.HashMap;

/**
 * <code>HashMap</code> with <code>String</code> keys that are case-insensitive.
 */
public class NamedValuesHashMap<V> extends HashMap<String, V> {

	@Override
	public V put(String key, V value) {
		key = key.toUpperCase();
		return super.put(key, value);
	}

	@Override
	public V get(Object key) {
		key = ((String)key).toUpperCase();
		return super.get(key);
	}

}