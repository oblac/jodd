// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

/**
 * Some general utilities.
 * All methods are safe as possible and operates with as many types as possible.
 */
public class Util {

	/**
	 * Safely compares two objects just like <code>equals()</code> would, except
	 * it allows any of the 2 objects to be <code>null</code>.
	 *
	 * @return <code>true</code> if arguments are equal, otherwise <code>false</code>
	 */
	public static boolean equals(Object obj1, Object obj2) {
		return (obj1 != null) ? (obj1.equals(obj2)) : (obj2 == null);
	}


	/**
	 * Returns string representation of an object, while checking for <code>null</code>.
	 */
	public static String toString(Object value) {
		if (value == null) {
			return null;
		}
		return value.toString();
	}

		// ---------------------------------------------------------------- misc

	/**
	 * Returns length of the object. Returns <code>0</code> for <code>null</code>.
	 * Returns <code>-1</code> for objects without a length.
	 */
	public static int length(Object obj) {
		if (obj == null) {
			return 0;
		}
		if (obj instanceof CharSequence) {
			return ((CharSequence) obj).length();
		}
		if (obj instanceof Collection) {
			return ((Collection) obj).size();
		}
		if (obj instanceof Map) {
			return ((Map) obj).size();
		}

		int count;
		if (obj instanceof Iterator) {
			Iterator iter = (Iterator) obj;
			count = 0;
			while (iter.hasNext()) {
				count++;
				iter.next();
			}
			return count;
		}
		if (obj instanceof Enumeration) {
			Enumeration enumeration = (Enumeration) obj;
			count = 0;
			while (enumeration.hasMoreElements()) {
				count++;
				enumeration.nextElement();
			}
			return count;
		}
		if (obj.getClass().isArray() == true) {
			return Array.getLength(obj);
		}
		return -1;
	}

	/**
	 * Returns <code>true</code> if first argument contains provided element.
	 * It works for strings, collections, maps and arrays.
s	 */
	public static boolean containsElement(Object obj, Object element) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof String) {
			if (element == null) {
				return false;
			}
			return ((String) obj).contains(element.toString());
		}
		if (obj instanceof Collection) {
			return ((Collection) obj).contains(element);
		}
		if (obj instanceof Map) {
			return ((Map) obj).values().contains(element);
		}

		if (obj instanceof Iterator) {
			Iterator iter = (Iterator) obj;
			while (iter.hasNext()) {
				Object o = iter.next();
				if (equals(o, element)) {
					return true;
				}
			}
			return false;
		}
		if (obj instanceof Enumeration) {
			Enumeration enumeration = (Enumeration) obj;
			while (enumeration.hasMoreElements()) {
				Object o = enumeration.nextElement();
				if (equals(o, element)) {
					return true;
				}
			}
			return false;
		}
		if (obj.getClass().isArray() == true) {
			int len = Array.getLength(obj);
			for (int i = 0; i < len; i++) {
				Object o = Array.get(obj, i);
				if (equals(o, element)) {
					return true;
				}
			}
		}
		return false;
	}

}