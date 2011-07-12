// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom;

import jodd.util.StringUtil;
import jodd.bean.BeanUtil;

import java.util.Map;
import java.util.HashMap;

/**
 * Joins an array of objects using provided hints.
 * If hint is not available, methods returns the very same object array instance. 
 */
public class JoinHintResolver {

	public Object[] join(Object[] data, String hints) {
		if (hints == null) {
			return data;
		}
		return join(data, StringUtil.splitc(hints, ','));
	}

	/**
	 * Joins entity array using provided string hints.
	 */
	public Object[] join(Object[] data, String[] hints) {
		if (hints == null) {
			return data;
		}
		// build context
		Map<String, Object> context = new HashMap<String, Object>(hints.length);
		for (int i = 0; i < hints.length; i++) {
			hints[i] = hints[i].trim();
			String hint = hints[i];
			if (hint.indexOf('.') == -1) {
				context.put(hint, data[i]);
			}
		}

		// no joining hints found
		if (context.size() == data.length) {
			return data;
		}

		// joining
		Object[] result = new Object[context.size()];
		int count = 0;
		for (int i = 0; i < hints.length; i++) {
			String hint = hints[i];
			int ndx = hint.indexOf('.');
			if (ndx != -1) {
				String key = hint.substring(0, ndx);
				Object value = context.get(key);
				if (value == null) {
					throw new DbOrmException("Context key '" + key + "' doesn't exist.");
				}
				BeanUtil.setDeclaredPropertySilent(value, hint.substring(ndx + 1), data[i]);
			} else {
				result[count] = data[i];
				count++;
			}
		}
		return result;
	}
}
