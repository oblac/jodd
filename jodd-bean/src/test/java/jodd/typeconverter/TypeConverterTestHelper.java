// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

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

	public static byte[] arrb(int... v) {
		byte[] bytes = new byte[v.length];

		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) v[i];
		}

		return bytes;
	}

	public static short[] arrs(int... v) {
		short[] shorts = new short[v.length];

		for (int i = 0; i < shorts.length; i++) {
			shorts[i] = (short) v[i];
		}

		return shorts;
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
		ArrayList<T> list = new ArrayList<>(v.length);

		for (int i = 0; i < v.length; i++) {
			list.add(v[i]);
		}

		return list;
	}

	public static <T> HashSet<T> seto(T... v) {
		HashSet<T> set = new LinkedHashSet<>(v.length);

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
