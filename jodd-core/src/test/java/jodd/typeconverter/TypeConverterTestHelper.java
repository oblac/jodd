// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.mutable.MutableInteger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;

public final class TypeConverterTestHelper {

	public static boolean[] arrl(boolean... v) {
		return v;
	}

	public static Object[] arro(Object... v) {
		return v;
	}

	public static String[] arrs(String... v) {
		return v;
	}

	public static Class[] arrc(Class... v) {
		return v;
	}

	public static int[] arri(int... v) {
		return v;
	}

	public static long[] arrl(long... v) {
		return v;
	}

	public static byte[] arrb(byte... v) {
		return v;
	}

	public static short[] arrs(short... v) {
		return v;
	}

	public static char[] arrc(char... v) {
		return v;
	}

	public static double[] arrd(double... v) {
		return v;
	}

	public static float[] arrf(float... v) {
		return v;
	}

	public static <T> ArrayList<T> listo(T... v) {
		ArrayList<T> list = new ArrayList<T>(v.length);

		for (int i = 0; i < v.length; i++) {
			list.add(v[i]);
		}

		return list;
	}

	public static <T> HashSet<T> seto(T... v) {
		HashSet<T> set = new LinkedHashSet<T>(v.length);

		for (int i = 0; i < v.length; i++) {
			set.add(v[i]);
		}

		return set;
	}

	public static <T> Iterable<T> iterableo(final T... v) {
		return new Iterable<T>() {
			public Iterator<T> iterator() {
				final MutableInteger index = new MutableInteger(0);
				return new Iterator<T>() {
					public boolean hasNext() {
						return index.value < v.length;
					}

					public T next() {
						T value = v[index.value];
						index.value++;
						return value;
					}

					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}
		};
	}

}
