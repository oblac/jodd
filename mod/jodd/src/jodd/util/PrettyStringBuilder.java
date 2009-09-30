// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * Constructs pretty string representation of object value.
 */
public class PrettyStringBuilder {

	protected int maxItemsToShow = 10;
	protected int maxDeep = 3;
	protected int deep;
	protected String nullValue = "<null>";

	public int getMaxItemsToShow() {
		return maxItemsToShow;
	}

	/**
	 * Sets the max number of items of arrays, collections and maps to show.
	 */
	public void setMaxItemsToShow(int maxItemsToShow) {
		this.maxItemsToShow = maxItemsToShow;
	}

	public int getMaxDeep() {
		return maxDeep;
	}

	/**
	 * Sets how deep to examine inner objects.
	 */
	public void setMaxDeep(int maxDeep) {
		this.maxDeep = maxDeep;
	}

	public String getNullValue() {
		return nullValue;
	}

	/**
	 * Sets <code>null</code> value representation.
	 */
	public void setNullValue(String nullValue) {
		this.nullValue = nullValue;
	}

	/**
	 * Returns pretty value from object value.
	 */
	protected String toPrettyString(Object obj) {
		deep++;
		if (obj == null) {
			deep--;
			return nullValue;
		}
		if (deep == maxDeep) {
			deep--;
			return obj.toString();
		}
		StringBuilder s = new StringBuilder();
		Class c = obj.getClass();
		if (c.isArray()) {
			int arrayLen = Array.getLength(obj);
			int len = Math.min(arrayLen, maxItemsToShow);
			s.append('[');
			for (int i = 0; i < len; i++) {
				s.append(toPrettyString(Array.get(obj, i)));
				if (i != len - 1) {
					s.append(',');
				}
			}
			if (len < arrayLen) {
				s.append("...");
			}
			s.append(']');
		} else if (obj instanceof Collection) {
			Collection coll = (Collection) obj;
			int len = Math.min(coll.size(), maxItemsToShow);
			Iterator it = coll.iterator();
			int i = 0;
			s.append('(');
			while ((it.hasNext() && (i < maxItemsToShow))) {
				s.append(toPrettyString(it.next()));
				if (i != len - 1) {
					s.append(',');
				}
				i++;
			}
			if (i < coll.size()) {
				s.append("...");
			}
			s.append(')');
		} else if (obj instanceof Map) {
			Map map = (Map) obj;
			int len = Math.min(map.size(), maxItemsToShow);
			Iterator it = map.keySet().iterator();
			int i = 0;
			s.append('{');
			while ((it.hasNext() && (i < maxItemsToShow))) {
				Object key = it.next();
				s.append(key).append(':');
				s.append(toPrettyString(map.get(key)));
				if (i != len - 1) {
					s.append(',');
				}
				i++;
			}
			if (i < map.size()) {
				s.append("...");
			}
			s.append('}');
		} else {
			s.append(obj.toString());
		}
		deep--;
		return s.toString();
	}

	/**
	 * Returns pretty string representation of the object.
	 */
	public String toString(Object value) {
		return toPrettyString(value);
	}
}
