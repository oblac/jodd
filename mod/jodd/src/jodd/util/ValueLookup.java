// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import java.util.List;
import java.util.Map;

/**
 * Performs safe value lookup from containers and arrays.
 * This is useful for rendering values.
 */
public class ValueLookup {

	// ---------------------------------------------------------------- array

	public static <T> T array(T[] array, int index) {
		if ((array == null) || (index >= array.length) || (index < 0)) {
			return null;
		}
		return array[index];
	}

	public static boolean array(boolean[] array, int index) {
		if ((array == null) || (index >= array.length) || (index < 0)) {
			return false;
		}
		return array[index];
	}

	public static byte array(byte[] array, int index) {
		if ((array == null) || (index >= array.length) || (index < 0)) {
			return 0;
		}
		return array[index];
	}

	public static short array(short[] array, int index) {
		if ((array == null) || (index >= array.length) || (index < 0)) {
			return 0;
		}
		return array[index];
	}

	public static int array(int[] array, int index) {
		if ((array == null) || (index >= array.length) || (index < 0)) {
			return 0;
		}
		return array[index];
	}

	public static long array(long[] array, int index) {
		if ((array == null) || (index >= array.length) || (index < 0)) {
			return 0;
		}
		return array[index];
	}

	public static float array(float[] array, int index) {
		if ((array == null) || (index >= array.length) || (index < 0)) {
			return 0;
		}
		return array[index];
	}

	public static double array(double[] array, int index) {
		if ((array == null) || (index >= array.length) || (index < 0)) {
			return 0;
		}
		return array[index];
	}


	// ---------------------------------------------------------------- collections

	public static <T> T list(List<T> list, int index) {
		if ((list == null) || (index >= list.size()) || (index < 0)) {
			return null;
		}
		return list.get(index);
	}

	public static <K,V> V map(Map<K, V> map, Object key) {
		if (map == null) {
			return null;
		}
		//noinspection SuspiciousMethodCalls
		return map.get(key);
	}

}
