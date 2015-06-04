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

package jodd.util;

import java.lang.reflect.Array;
import static jodd.util.StringPool.NULL;

/**
 * Array utilities.
 * <b>DO NOT MODIFY: this source is generated.</b>
 */
public class ArraysUtil {


	// ---------------------------------------------------------------- wrap

	/**
	 * Wraps elements into an array.
	 */
	public static <T> T[] array(T... elements) {
		return elements;
	}

	/**
	 * Wraps elements into an array.
	 */
	public static byte[] bytes(byte... elements) {
		return elements;
	}

	/**
	 * Wraps elements into an array.
	 */
	public static char[] chars(char... elements) {
		return elements;
	}

	/**
	 * Wraps elements into an array.
	 */
	public static short[] shorts(short... elements) {
		return elements;
	}

	/**
	 * Wraps elements into an array.
	 */
	public static int[] ints(int... elements) {
		return elements;
	}

	/**
	 * Wraps elements into an array.
	 */
	public static long[] longs(long... elements) {
		return elements;
	}

	/**
	 * Wraps elements into an array.
	 */
	public static float[] floats(float... elements) {
		return elements;
	}

	/**
	 * Wraps elements into an array.
	 */
	public static double[] doubles(double... elements) {
		return elements;
	}

	/**
	 * Wraps elements into an array.
	 */
	public static boolean[] booleans(boolean... elements) {
		return elements;
	}


	// ---------------------------------------------------------------- join

	/**
	 * Joins arrays. Component type is resolved from the array argument.
	 */
	@SuppressWarnings({"unchecked"})
	public static <T> T[] join(T[]... arrays) {
		Class<T> componentType = (Class<T>) arrays.getClass().getComponentType().getComponentType();
		return join(componentType, arrays);
	}

	/**
	 * Joins arrays using provided component type.
	 */
	@SuppressWarnings({"unchecked"})
	public static <T> T[] join(Class<T> componentType, T[][] arrays) {
		if (arrays.length == 1) {
			return arrays[0];
		}
		int length = 0;
		for (T[] array : arrays) {
			length += array.length;
		}
		T[] result = (T[]) Array.newInstance(componentType, length);

		length = 0;
		for (T[] array : arrays) {
			System.arraycopy(array, 0, result, length, array.length);
			length += array.length;
		}
		return result;
	}


	/**
	 * Join <code>String</code> arrays.
	 */
	public static String[] join(String[]... arrays) {
		if (arrays.length == 0) {
			return new String[0];
		}
		if (arrays.length == 1) {
			return arrays[0];
		}
		int length = 0;
		for (String[] array : arrays) {
			length += array.length;
		}
		String[] result = new String[length];
		length = 0;
		for (String[] array : arrays) {
			System.arraycopy(array, 0, result, length, array.length);
			length += array.length;
		}
		return result;
	}

	/**
	 * Join <code>byte</code> arrays.
	 */
	public static byte[] join(byte[]... arrays) {
		if (arrays.length == 0) {
			return new byte[0];
		}
		if (arrays.length == 1) {
			return arrays[0];
		}
		int length = 0;
		for (byte[] array : arrays) {
			length += array.length;
		}
		byte[] result = new byte[length];
		length = 0;
		for (byte[] array : arrays) {
			System.arraycopy(array, 0, result, length, array.length);
			length += array.length;
		}
		return result;
	}

	/**
	 * Join <code>char</code> arrays.
	 */
	public static char[] join(char[]... arrays) {
		if (arrays.length == 0) {
			return new char[0];
		}
		if (arrays.length == 1) {
			return arrays[0];
		}
		int length = 0;
		for (char[] array : arrays) {
			length += array.length;
		}
		char[] result = new char[length];
		length = 0;
		for (char[] array : arrays) {
			System.arraycopy(array, 0, result, length, array.length);
			length += array.length;
		}
		return result;
	}

	/**
	 * Join <code>short</code> arrays.
	 */
	public static short[] join(short[]... arrays) {
		if (arrays.length == 0) {
			return new short[0];
		}
		if (arrays.length == 1) {
			return arrays[0];
		}
		int length = 0;
		for (short[] array : arrays) {
			length += array.length;
		}
		short[] result = new short[length];
		length = 0;
		for (short[] array : arrays) {
			System.arraycopy(array, 0, result, length, array.length);
			length += array.length;
		}
		return result;
	}

	/**
	 * Join <code>int</code> arrays.
	 */
	public static int[] join(int[]... arrays) {
		if (arrays.length == 0) {
			return new int[0];
		}
		if (arrays.length == 1) {
			return arrays[0];
		}
		int length = 0;
		for (int[] array : arrays) {
			length += array.length;
		}
		int[] result = new int[length];
		length = 0;
		for (int[] array : arrays) {
			System.arraycopy(array, 0, result, length, array.length);
			length += array.length;
		}
		return result;
	}

	/**
	 * Join <code>long</code> arrays.
	 */
	public static long[] join(long[]... arrays) {
		if (arrays.length == 0) {
			return new long[0];
		}
		if (arrays.length == 1) {
			return arrays[0];
		}
		int length = 0;
		for (long[] array : arrays) {
			length += array.length;
		}
		long[] result = new long[length];
		length = 0;
		for (long[] array : arrays) {
			System.arraycopy(array, 0, result, length, array.length);
			length += array.length;
		}
		return result;
	}

	/**
	 * Join <code>float</code> arrays.
	 */
	public static float[] join(float[]... arrays) {
		if (arrays.length == 0) {
			return new float[0];
		}
		if (arrays.length == 1) {
			return arrays[0];
		}
		int length = 0;
		for (float[] array : arrays) {
			length += array.length;
		}
		float[] result = new float[length];
		length = 0;
		for (float[] array : arrays) {
			System.arraycopy(array, 0, result, length, array.length);
			length += array.length;
		}
		return result;
	}

	/**
	 * Join <code>double</code> arrays.
	 */
	public static double[] join(double[]... arrays) {
		if (arrays.length == 0) {
			return new double[0];
		}
		if (arrays.length == 1) {
			return arrays[0];
		}
		int length = 0;
		for (double[] array : arrays) {
			length += array.length;
		}
		double[] result = new double[length];
		length = 0;
		for (double[] array : arrays) {
			System.arraycopy(array, 0, result, length, array.length);
			length += array.length;
		}
		return result;
	}

	/**
	 * Join <code>boolean</code> arrays.
	 */
	public static boolean[] join(boolean[]... arrays) {
		if (arrays.length == 0) {
			return new boolean[0];
		}
		if (arrays.length == 1) {
			return arrays[0];
		}
		int length = 0;
		for (boolean[] array : arrays) {
			length += array.length;
		}
		boolean[] result = new boolean[length];
		length = 0;
		for (boolean[] array : arrays) {
			System.arraycopy(array, 0, result, length, array.length);
			length += array.length;
		}
		return result;
	}


	// ---------------------------------------------------------------- resize

	/**
	 * Resizes an array.
	 */
	public static <T> T[] resize(T[] buffer, int newSize) {
		Class<T> componentType = (Class<T>) buffer.getClass().getComponentType();
		T[] temp = (T[]) Array.newInstance(componentType, newSize);
		System.arraycopy(buffer, 0, temp, 0, buffer.length >= newSize ? newSize : buffer.length);
		return temp;
	}


	/**
	 * Resizes a <code>String</code> array.
	 */
	public static String[] resize(String buffer[], int newSize) {
		String temp[] = new String[newSize];
		System.arraycopy(buffer, 0, temp, 0, buffer.length >= newSize ? newSize : buffer.length);
		return temp;
	}

	/**
	 * Resizes a <code>byte</code> array.
	 */
	public static byte[] resize(byte buffer[], int newSize) {
		byte temp[] = new byte[newSize];
		System.arraycopy(buffer, 0, temp, 0, buffer.length >= newSize ? newSize : buffer.length);
		return temp;
	}

	/**
	 * Resizes a <code>char</code> array.
	 */
	public static char[] resize(char buffer[], int newSize) {
		char temp[] = new char[newSize];
		System.arraycopy(buffer, 0, temp, 0, buffer.length >= newSize ? newSize : buffer.length);
		return temp;
	}

	/**
	 * Resizes a <code>short</code> array.
	 */
	public static short[] resize(short buffer[], int newSize) {
		short temp[] = new short[newSize];
		System.arraycopy(buffer, 0, temp, 0, buffer.length >= newSize ? newSize : buffer.length);
		return temp;
	}

	/**
	 * Resizes a <code>int</code> array.
	 */
	public static int[] resize(int buffer[], int newSize) {
		int temp[] = new int[newSize];
		System.arraycopy(buffer, 0, temp, 0, buffer.length >= newSize ? newSize : buffer.length);
		return temp;
	}

	/**
	 * Resizes a <code>long</code> array.
	 */
	public static long[] resize(long buffer[], int newSize) {
		long temp[] = new long[newSize];
		System.arraycopy(buffer, 0, temp, 0, buffer.length >= newSize ? newSize : buffer.length);
		return temp;
	}

	/**
	 * Resizes a <code>float</code> array.
	 */
	public static float[] resize(float buffer[], int newSize) {
		float temp[] = new float[newSize];
		System.arraycopy(buffer, 0, temp, 0, buffer.length >= newSize ? newSize : buffer.length);
		return temp;
	}

	/**
	 * Resizes a <code>double</code> array.
	 */
	public static double[] resize(double buffer[], int newSize) {
		double temp[] = new double[newSize];
		System.arraycopy(buffer, 0, temp, 0, buffer.length >= newSize ? newSize : buffer.length);
		return temp;
	}

	/**
	 * Resizes a <code>boolean</code> array.
	 */
	public static boolean[] resize(boolean buffer[], int newSize) {
		boolean temp[] = new boolean[newSize];
		System.arraycopy(buffer, 0, temp, 0, buffer.length >= newSize ? newSize : buffer.length);
		return temp;
	}


	// ---------------------------------------------------------------- append

	/**
	 * Appends an element to array.
	 */
	public static <T> T[] append(T[] buffer, T newElement) {
		T[] t = resize(buffer, buffer.length + 1);
		t[buffer.length] = newElement;
		return t;
	}

	/**
	 * Appends an element to <code>String</code> array.
	 */
	public static String[] append(String buffer[], String newElement) {
		String[] t = resize(buffer, buffer.length + 1);
		t[buffer.length] = newElement;
		return t;
	}

	/**
	 * Appends an element to <code>byte</code> array.
	 */
	public static byte[] append(byte buffer[], byte newElement) {
		byte[] t = resize(buffer, buffer.length + 1);
		t[buffer.length] = newElement;
		return t;
	}

	/**
	 * Appends an element to <code>char</code> array.
	 */
	public static char[] append(char buffer[], char newElement) {
		char[] t = resize(buffer, buffer.length + 1);
		t[buffer.length] = newElement;
		return t;
	}

	/**
	 * Appends an element to <code>short</code> array.
	 */
	public static short[] append(short buffer[], short newElement) {
		short[] t = resize(buffer, buffer.length + 1);
		t[buffer.length] = newElement;
		return t;
	}

	/**
	 * Appends an element to <code>int</code> array.
	 */
	public static int[] append(int buffer[], int newElement) {
		int[] t = resize(buffer, buffer.length + 1);
		t[buffer.length] = newElement;
		return t;
	}

	/**
	 * Appends an element to <code>long</code> array.
	 */
	public static long[] append(long buffer[], long newElement) {
		long[] t = resize(buffer, buffer.length + 1);
		t[buffer.length] = newElement;
		return t;
	}

	/**
	 * Appends an element to <code>float</code> array.
	 */
	public static float[] append(float buffer[], float newElement) {
		float[] t = resize(buffer, buffer.length + 1);
		t[buffer.length] = newElement;
		return t;
	}

	/**
	 * Appends an element to <code>double</code> array.
	 */
	public static double[] append(double buffer[], double newElement) {
		double[] t = resize(buffer, buffer.length + 1);
		t[buffer.length] = newElement;
		return t;
	}

	/**
	 * Appends an element to <code>boolean</code> array.
	 */
	public static boolean[] append(boolean buffer[], boolean newElement) {
		boolean[] t = resize(buffer, buffer.length + 1);
		t[buffer.length] = newElement;
		return t;
	}


	// ---------------------------------------------------------------- remove

	/**
	 * Removes sub-array.
	 */
	public static <T> T[] remove(T[] buffer, int offset, int length) {
		Class<T> componentType = (Class<T>) buffer.getClass().getComponentType();
		return remove(buffer, offset, length, componentType);
	}

	/**
	 * Removes sub-array.
	 */
	@SuppressWarnings({"unchecked"})
	public static <T> T[] remove(T[] buffer, int offset, int length, Class<T> componentType) {
		int len2 = buffer.length - length;
		T[] temp = (T[]) Array.newInstance(componentType, len2);
		System.arraycopy(buffer, 0, temp, 0, offset);
		System.arraycopy(buffer, offset + length, temp, offset, len2 - offset);
		return temp;
	}

	/**
	 * Removes sub-array from <code>String</code> array.
	 */
	public static String[] remove(String[] buffer, int offset, int length) {
		int len2 = buffer.length - length;
		String temp[] = new String[len2];
		System.arraycopy(buffer, 0, temp, 0, offset);
		System.arraycopy(buffer, offset + length, temp, offset, len2 - offset);
		return temp;
	}

	/**
	 * Removes sub-array from <code>byte</code> array.
	 */
	public static byte[] remove(byte[] buffer, int offset, int length) {
		int len2 = buffer.length - length;
		byte temp[] = new byte[len2];
		System.arraycopy(buffer, 0, temp, 0, offset);
		System.arraycopy(buffer, offset + length, temp, offset, len2 - offset);
		return temp;
	}

	/**
	 * Removes sub-array from <code>char</code> array.
	 */
	public static char[] remove(char[] buffer, int offset, int length) {
		int len2 = buffer.length - length;
		char temp[] = new char[len2];
		System.arraycopy(buffer, 0, temp, 0, offset);
		System.arraycopy(buffer, offset + length, temp, offset, len2 - offset);
		return temp;
	}

	/**
	 * Removes sub-array from <code>short</code> array.
	 */
	public static short[] remove(short[] buffer, int offset, int length) {
		int len2 = buffer.length - length;
		short temp[] = new short[len2];
		System.arraycopy(buffer, 0, temp, 0, offset);
		System.arraycopy(buffer, offset + length, temp, offset, len2 - offset);
		return temp;
	}

	/**
	 * Removes sub-array from <code>int</code> array.
	 */
	public static int[] remove(int[] buffer, int offset, int length) {
		int len2 = buffer.length - length;
		int temp[] = new int[len2];
		System.arraycopy(buffer, 0, temp, 0, offset);
		System.arraycopy(buffer, offset + length, temp, offset, len2 - offset);
		return temp;
	}

	/**
	 * Removes sub-array from <code>long</code> array.
	 */
	public static long[] remove(long[] buffer, int offset, int length) {
		int len2 = buffer.length - length;
		long temp[] = new long[len2];
		System.arraycopy(buffer, 0, temp, 0, offset);
		System.arraycopy(buffer, offset + length, temp, offset, len2 - offset);
		return temp;
	}

	/**
	 * Removes sub-array from <code>float</code> array.
	 */
	public static float[] remove(float[] buffer, int offset, int length) {
		int len2 = buffer.length - length;
		float temp[] = new float[len2];
		System.arraycopy(buffer, 0, temp, 0, offset);
		System.arraycopy(buffer, offset + length, temp, offset, len2 - offset);
		return temp;
	}

	/**
	 * Removes sub-array from <code>double</code> array.
	 */
	public static double[] remove(double[] buffer, int offset, int length) {
		int len2 = buffer.length - length;
		double temp[] = new double[len2];
		System.arraycopy(buffer, 0, temp, 0, offset);
		System.arraycopy(buffer, offset + length, temp, offset, len2 - offset);
		return temp;
	}

	/**
	 * Removes sub-array from <code>boolean</code> array.
	 */
	public static boolean[] remove(boolean[] buffer, int offset, int length) {
		int len2 = buffer.length - length;
		boolean temp[] = new boolean[len2];
		System.arraycopy(buffer, 0, temp, 0, offset);
		System.arraycopy(buffer, offset + length, temp, offset, len2 - offset);
		return temp;
	}


	// ---------------------------------------------------------------- subarray

	/**
	 * Returns subarray.
	 */
	public static <T> T[] subarray(T[] buffer, int offset, int length) {
		Class<T> componentType = (Class<T>) buffer.getClass().getComponentType();
		return subarray(buffer, offset, length, componentType);
	}

	/**
	 * Returns subarray.
	 */
	@SuppressWarnings({"unchecked"})
	public static <T> T[] subarray(T[] buffer, int offset, int length, Class<T> componentType) {
		T[] temp = (T[]) Array.newInstance(componentType, length);
		System.arraycopy(buffer, offset, temp, 0, length);
		return temp;
	}

	/**
	 * Returns subarray.
	 */
	public static String[] subarray(String[] buffer, int offset, int length) {
		String temp[] = new String[length];
		System.arraycopy(buffer, offset, temp, 0, length);
		return temp;
	}

	/**
	 * Returns subarray.
	 */
	public static byte[] subarray(byte[] buffer, int offset, int length) {
		byte temp[] = new byte[length];
		System.arraycopy(buffer, offset, temp, 0, length);
		return temp;
	}

	/**
	 * Returns subarray.
	 */
	public static char[] subarray(char[] buffer, int offset, int length) {
		char temp[] = new char[length];
		System.arraycopy(buffer, offset, temp, 0, length);
		return temp;
	}

	/**
	 * Returns subarray.
	 */
	public static short[] subarray(short[] buffer, int offset, int length) {
		short temp[] = new short[length];
		System.arraycopy(buffer, offset, temp, 0, length);
		return temp;
	}

	/**
	 * Returns subarray.
	 */
	public static int[] subarray(int[] buffer, int offset, int length) {
		int temp[] = new int[length];
		System.arraycopy(buffer, offset, temp, 0, length);
		return temp;
	}

	/**
	 * Returns subarray.
	 */
	public static long[] subarray(long[] buffer, int offset, int length) {
		long temp[] = new long[length];
		System.arraycopy(buffer, offset, temp, 0, length);
		return temp;
	}

	/**
	 * Returns subarray.
	 */
	public static float[] subarray(float[] buffer, int offset, int length) {
		float temp[] = new float[length];
		System.arraycopy(buffer, offset, temp, 0, length);
		return temp;
	}

	/**
	 * Returns subarray.
	 */
	public static double[] subarray(double[] buffer, int offset, int length) {
		double temp[] = new double[length];
		System.arraycopy(buffer, offset, temp, 0, length);
		return temp;
	}

	/**
	 * Returns subarray.
	 */
	public static boolean[] subarray(boolean[] buffer, int offset, int length) {
		boolean temp[] = new boolean[length];
		System.arraycopy(buffer, offset, temp, 0, length);
		return temp;
	}


	// ---------------------------------------------------------------- insert

	/**
	 * Inserts one array into another array.
	 */
	public static <T> T[] insert(T[] dest, T[] src, int offset) {
		Class<T> componentType = (Class<T>) dest.getClass().getComponentType();
		return insert(dest, src, offset, componentType);
	}
	/**
	 * Inserts one element into an array.
	 */
	public static <T> T[] insert(T[] dest, T src, int offset) {
		Class<T> componentType = (Class<T>) dest.getClass().getComponentType();
		return insert(dest, src, offset, componentType);
	}

	/**
	 * Inserts one array into another array.
	 */
	@SuppressWarnings({"unchecked"})
	public static <T> T[] insert(T[] dest, T[] src, int offset, Class componentType) {
		T[] temp = (T[]) Array.newInstance(componentType, dest.length + src.length);
		System.arraycopy(dest, 0, temp, 0, offset);
		System.arraycopy(src, 0, temp, offset, src.length);
		System.arraycopy(dest, offset, temp, src.length + offset, dest.length - offset);
		return temp;
	}
	/**
	 * Inserts one element into another array.
	 */
	@SuppressWarnings({"unchecked"})
	public static <T> T[] insert(T[] dest, T src, int offset, Class componentType) {
		T[] temp = (T[]) Array.newInstance(componentType, dest.length + 1);
		System.arraycopy(dest, 0, temp, 0, offset);
		temp[offset] = src;
		System.arraycopy(dest, offset, temp, offset + 1, dest.length - offset);
		return temp;
	}

	/**
	 * Inserts one array into another <code>String</code> array.
	 */
	public static String[] insert(String[] dest, String[] src, int offset) {
		String[] temp = new String[dest.length + src.length];
		System.arraycopy(dest, 0, temp, 0, offset);
		System.arraycopy(src, 0, temp, offset, src.length);
		System.arraycopy(dest, offset, temp, src.length + offset, dest.length - offset);
		return temp;
	}

	/**
	 * Inserts one element into another <code>String</code> array.
	 */
	public static String[] insert(String[] dest, String src, int offset) {
		String[] temp = new String[dest.length + 1];
		System.arraycopy(dest, 0, temp, 0, offset);
		temp[offset] = src;
		System.arraycopy(dest, offset, temp, offset + 1, dest.length - offset);
		return temp;
	}

	/**
	 * Inserts one array into another <code>byte</code> array.
	 */
	public static byte[] insert(byte[] dest, byte[] src, int offset) {
		byte[] temp = new byte[dest.length + src.length];
		System.arraycopy(dest, 0, temp, 0, offset);
		System.arraycopy(src, 0, temp, offset, src.length);
		System.arraycopy(dest, offset, temp, src.length + offset, dest.length - offset);
		return temp;
	}

	/**
	 * Inserts one element into another <code>byte</code> array.
	 */
	public static byte[] insert(byte[] dest, byte src, int offset) {
		byte[] temp = new byte[dest.length + 1];
		System.arraycopy(dest, 0, temp, 0, offset);
		temp[offset] = src;
		System.arraycopy(dest, offset, temp, offset + 1, dest.length - offset);
		return temp;
	}

	/**
	 * Inserts one array into another <code>char</code> array.
	 */
	public static char[] insert(char[] dest, char[] src, int offset) {
		char[] temp = new char[dest.length + src.length];
		System.arraycopy(dest, 0, temp, 0, offset);
		System.arraycopy(src, 0, temp, offset, src.length);
		System.arraycopy(dest, offset, temp, src.length + offset, dest.length - offset);
		return temp;
	}

	/**
	 * Inserts one element into another <code>char</code> array.
	 */
	public static char[] insert(char[] dest, char src, int offset) {
		char[] temp = new char[dest.length + 1];
		System.arraycopy(dest, 0, temp, 0, offset);
		temp[offset] = src;
		System.arraycopy(dest, offset, temp, offset + 1, dest.length - offset);
		return temp;
	}

	/**
	 * Inserts one array into another <code>short</code> array.
	 */
	public static short[] insert(short[] dest, short[] src, int offset) {
		short[] temp = new short[dest.length + src.length];
		System.arraycopy(dest, 0, temp, 0, offset);
		System.arraycopy(src, 0, temp, offset, src.length);
		System.arraycopy(dest, offset, temp, src.length + offset, dest.length - offset);
		return temp;
	}

	/**
	 * Inserts one element into another <code>short</code> array.
	 */
	public static short[] insert(short[] dest, short src, int offset) {
		short[] temp = new short[dest.length + 1];
		System.arraycopy(dest, 0, temp, 0, offset);
		temp[offset] = src;
		System.arraycopy(dest, offset, temp, offset + 1, dest.length - offset);
		return temp;
	}

	/**
	 * Inserts one array into another <code>int</code> array.
	 */
	public static int[] insert(int[] dest, int[] src, int offset) {
		int[] temp = new int[dest.length + src.length];
		System.arraycopy(dest, 0, temp, 0, offset);
		System.arraycopy(src, 0, temp, offset, src.length);
		System.arraycopy(dest, offset, temp, src.length + offset, dest.length - offset);
		return temp;
	}

	/**
	 * Inserts one element into another <code>int</code> array.
	 */
	public static int[] insert(int[] dest, int src, int offset) {
		int[] temp = new int[dest.length + 1];
		System.arraycopy(dest, 0, temp, 0, offset);
		temp[offset] = src;
		System.arraycopy(dest, offset, temp, offset + 1, dest.length - offset);
		return temp;
	}

	/**
	 * Inserts one array into another <code>long</code> array.
	 */
	public static long[] insert(long[] dest, long[] src, int offset) {
		long[] temp = new long[dest.length + src.length];
		System.arraycopy(dest, 0, temp, 0, offset);
		System.arraycopy(src, 0, temp, offset, src.length);
		System.arraycopy(dest, offset, temp, src.length + offset, dest.length - offset);
		return temp;
	}

	/**
	 * Inserts one element into another <code>long</code> array.
	 */
	public static long[] insert(long[] dest, long src, int offset) {
		long[] temp = new long[dest.length + 1];
		System.arraycopy(dest, 0, temp, 0, offset);
		temp[offset] = src;
		System.arraycopy(dest, offset, temp, offset + 1, dest.length - offset);
		return temp;
	}

	/**
	 * Inserts one array into another <code>float</code> array.
	 */
	public static float[] insert(float[] dest, float[] src, int offset) {
		float[] temp = new float[dest.length + src.length];
		System.arraycopy(dest, 0, temp, 0, offset);
		System.arraycopy(src, 0, temp, offset, src.length);
		System.arraycopy(dest, offset, temp, src.length + offset, dest.length - offset);
		return temp;
	}

	/**
	 * Inserts one element into another <code>float</code> array.
	 */
	public static float[] insert(float[] dest, float src, int offset) {
		float[] temp = new float[dest.length + 1];
		System.arraycopy(dest, 0, temp, 0, offset);
		temp[offset] = src;
		System.arraycopy(dest, offset, temp, offset + 1, dest.length - offset);
		return temp;
	}

	/**
	 * Inserts one array into another <code>double</code> array.
	 */
	public static double[] insert(double[] dest, double[] src, int offset) {
		double[] temp = new double[dest.length + src.length];
		System.arraycopy(dest, 0, temp, 0, offset);
		System.arraycopy(src, 0, temp, offset, src.length);
		System.arraycopy(dest, offset, temp, src.length + offset, dest.length - offset);
		return temp;
	}

	/**
	 * Inserts one element into another <code>double</code> array.
	 */
	public static double[] insert(double[] dest, double src, int offset) {
		double[] temp = new double[dest.length + 1];
		System.arraycopy(dest, 0, temp, 0, offset);
		temp[offset] = src;
		System.arraycopy(dest, offset, temp, offset + 1, dest.length - offset);
		return temp;
	}

	/**
	 * Inserts one array into another <code>boolean</code> array.
	 */
	public static boolean[] insert(boolean[] dest, boolean[] src, int offset) {
		boolean[] temp = new boolean[dest.length + src.length];
		System.arraycopy(dest, 0, temp, 0, offset);
		System.arraycopy(src, 0, temp, offset, src.length);
		System.arraycopy(dest, offset, temp, src.length + offset, dest.length - offset);
		return temp;
	}

	/**
	 * Inserts one element into another <code>boolean</code> array.
	 */
	public static boolean[] insert(boolean[] dest, boolean src, int offset) {
		boolean[] temp = new boolean[dest.length + 1];
		System.arraycopy(dest, 0, temp, 0, offset);
		temp[offset] = src;
		System.arraycopy(dest, offset, temp, offset + 1, dest.length - offset);
		return temp;
	}


	// ---------------------------------------------------------------- insertAt

	/**
	 * Inserts one array into another at given offset.
	 */
	public static <T> T[] insertAt(T[] dest, T[] src, int offset) {
		Class<T> componentType = (Class<T>) dest.getClass().getComponentType();
		return insertAt(dest, src, offset, componentType);
	}

	/**
	 * Inserts one array into another at given offset.
	 */
	@SuppressWarnings({"unchecked"})
	public static <T> T[] insertAt(T[] dest, T[] src, int offset, Class componentType) {
		T[] temp = (T[]) Array.newInstance(componentType, dest.length + src.length - 1);
		System.arraycopy(dest, 0, temp, 0, offset);
		System.arraycopy(src, 0, temp, offset, src.length);
		System.arraycopy(dest, offset + 1, temp, src.length + offset, dest.length - offset - 1);
		return temp;
	}

	/**
	 * Inserts one array into another by replacing specified offset.
	 */
	public static String[] insertAt(String[] dest, String[] src, int offset) {
		String[] temp = new String[dest.length + src.length - 1];
		System.arraycopy(dest, 0, temp, 0, offset);
		System.arraycopy(src, 0, temp, offset, src.length);
		System.arraycopy(dest, offset + 1, temp, src.length + offset, dest.length - offset - 1);
		return temp;
	}

	/**
	 * Inserts one array into another by replacing specified offset.
	 */
	public static byte[] insertAt(byte[] dest, byte[] src, int offset) {
		byte[] temp = new byte[dest.length + src.length - 1];
		System.arraycopy(dest, 0, temp, 0, offset);
		System.arraycopy(src, 0, temp, offset, src.length);
		System.arraycopy(dest, offset + 1, temp, src.length + offset, dest.length - offset - 1);
		return temp;
	}

	/**
	 * Inserts one array into another by replacing specified offset.
	 */
	public static char[] insertAt(char[] dest, char[] src, int offset) {
		char[] temp = new char[dest.length + src.length - 1];
		System.arraycopy(dest, 0, temp, 0, offset);
		System.arraycopy(src, 0, temp, offset, src.length);
		System.arraycopy(dest, offset + 1, temp, src.length + offset, dest.length - offset - 1);
		return temp;
	}

	/**
	 * Inserts one array into another by replacing specified offset.
	 */
	public static short[] insertAt(short[] dest, short[] src, int offset) {
		short[] temp = new short[dest.length + src.length - 1];
		System.arraycopy(dest, 0, temp, 0, offset);
		System.arraycopy(src, 0, temp, offset, src.length);
		System.arraycopy(dest, offset + 1, temp, src.length + offset, dest.length - offset - 1);
		return temp;
	}

	/**
	 * Inserts one array into another by replacing specified offset.
	 */
	public static int[] insertAt(int[] dest, int[] src, int offset) {
		int[] temp = new int[dest.length + src.length - 1];
		System.arraycopy(dest, 0, temp, 0, offset);
		System.arraycopy(src, 0, temp, offset, src.length);
		System.arraycopy(dest, offset + 1, temp, src.length + offset, dest.length - offset - 1);
		return temp;
	}

	/**
	 * Inserts one array into another by replacing specified offset.
	 */
	public static long[] insertAt(long[] dest, long[] src, int offset) {
		long[] temp = new long[dest.length + src.length - 1];
		System.arraycopy(dest, 0, temp, 0, offset);
		System.arraycopy(src, 0, temp, offset, src.length);
		System.arraycopy(dest, offset + 1, temp, src.length + offset, dest.length - offset - 1);
		return temp;
	}

	/**
	 * Inserts one array into another by replacing specified offset.
	 */
	public static float[] insertAt(float[] dest, float[] src, int offset) {
		float[] temp = new float[dest.length + src.length - 1];
		System.arraycopy(dest, 0, temp, 0, offset);
		System.arraycopy(src, 0, temp, offset, src.length);
		System.arraycopy(dest, offset + 1, temp, src.length + offset, dest.length - offset - 1);
		return temp;
	}

	/**
	 * Inserts one array into another by replacing specified offset.
	 */
	public static double[] insertAt(double[] dest, double[] src, int offset) {
		double[] temp = new double[dest.length + src.length - 1];
		System.arraycopy(dest, 0, temp, 0, offset);
		System.arraycopy(src, 0, temp, offset, src.length);
		System.arraycopy(dest, offset + 1, temp, src.length + offset, dest.length - offset - 1);
		return temp;
	}

	/**
	 * Inserts one array into another by replacing specified offset.
	 */
	public static boolean[] insertAt(boolean[] dest, boolean[] src, int offset) {
		boolean[] temp = new boolean[dest.length + src.length - 1];
		System.arraycopy(dest, 0, temp, 0, offset);
		System.arraycopy(src, 0, temp, offset, src.length);
		System.arraycopy(dest, offset + 1, temp, src.length + offset, dest.length - offset - 1);
		return temp;
	}


	// ---------------------------------------------------------------- convert


	/**
	 * Converts to primitive array.
	 */
	public static byte[] values(Byte[] array) {
		byte[] dest = new byte[array.length];
		for (int i = 0; i < array.length; i++) {
			Byte v = array[i];
			if (v != null) {
				dest[i] = v.byteValue();
			}
		}
		return dest;
	}
	/**
	 * Converts to object array.
	 */
	public static Byte[] valuesOf(byte[] array) {
		Byte[] dest = new Byte[array.length];
		for (int i = 0; i < array.length; i++) {
			dest[i] = Byte.valueOf(array[i]);
		}
		return dest;
	}


	/**
	 * Converts to primitive array.
	 */
	public static char[] values(Character[] array) {
		char[] dest = new char[array.length];
		for (int i = 0; i < array.length; i++) {
			Character v = array[i];
			if (v != null) {
				dest[i] = v.charValue();
			}
		}
		return dest;
	}
	/**
	 * Converts to object array.
	 */
	public static Character[] valuesOf(char[] array) {
		Character[] dest = new Character[array.length];
		for (int i = 0; i < array.length; i++) {
			dest[i] = Character.valueOf(array[i]);
		}
		return dest;
	}


	/**
	 * Converts to primitive array.
	 */
	public static short[] values(Short[] array) {
		short[] dest = new short[array.length];
		for (int i = 0; i < array.length; i++) {
			Short v = array[i];
			if (v != null) {
				dest[i] = v.shortValue();
			}
		}
		return dest;
	}
	/**
	 * Converts to object array.
	 */
	public static Short[] valuesOf(short[] array) {
		Short[] dest = new Short[array.length];
		for (int i = 0; i < array.length; i++) {
			dest[i] = Short.valueOf(array[i]);
		}
		return dest;
	}


	/**
	 * Converts to primitive array.
	 */
	public static int[] values(Integer[] array) {
		int[] dest = new int[array.length];
		for (int i = 0; i < array.length; i++) {
			Integer v = array[i];
			if (v != null) {
				dest[i] = v.intValue();
			}
		}
		return dest;
	}
	/**
	 * Converts to object array.
	 */
	public static Integer[] valuesOf(int[] array) {
		Integer[] dest = new Integer[array.length];
		for (int i = 0; i < array.length; i++) {
			dest[i] = Integer.valueOf(array[i]);
		}
		return dest;
	}


	/**
	 * Converts to primitive array.
	 */
	public static long[] values(Long[] array) {
		long[] dest = new long[array.length];
		for (int i = 0; i < array.length; i++) {
			Long v = array[i];
			if (v != null) {
				dest[i] = v.longValue();
			}
		}
		return dest;
	}
	/**
	 * Converts to object array.
	 */
	public static Long[] valuesOf(long[] array) {
		Long[] dest = new Long[array.length];
		for (int i = 0; i < array.length; i++) {
			dest[i] = Long.valueOf(array[i]);
		}
		return dest;
	}


	/**
	 * Converts to primitive array.
	 */
	public static float[] values(Float[] array) {
		float[] dest = new float[array.length];
		for (int i = 0; i < array.length; i++) {
			Float v = array[i];
			if (v != null) {
				dest[i] = v.floatValue();
			}
		}
		return dest;
	}
	/**
	 * Converts to object array.
	 */
	public static Float[] valuesOf(float[] array) {
		Float[] dest = new Float[array.length];
		for (int i = 0; i < array.length; i++) {
			dest[i] = Float.valueOf(array[i]);
		}
		return dest;
	}


	/**
	 * Converts to primitive array.
	 */
	public static double[] values(Double[] array) {
		double[] dest = new double[array.length];
		for (int i = 0; i < array.length; i++) {
			Double v = array[i];
			if (v != null) {
				dest[i] = v.doubleValue();
			}
		}
		return dest;
	}
	/**
	 * Converts to object array.
	 */
	public static Double[] valuesOf(double[] array) {
		Double[] dest = new Double[array.length];
		for (int i = 0; i < array.length; i++) {
			dest[i] = Double.valueOf(array[i]);
		}
		return dest;
	}


	/**
	 * Converts to primitive array.
	 */
	public static boolean[] values(Boolean[] array) {
		boolean[] dest = new boolean[array.length];
		for (int i = 0; i < array.length; i++) {
			Boolean v = array[i];
			if (v != null) {
				dest[i] = v.booleanValue();
			}
		}
		return dest;
	}
	/**
	 * Converts to object array.
	 */
	public static Boolean[] valuesOf(boolean[] array) {
		Boolean[] dest = new Boolean[array.length];
		for (int i = 0; i < array.length; i++) {
			dest[i] = Boolean.valueOf(array[i]);
		}
		return dest;
	}



	// ---------------------------------------------------------------- indexof


	/**
	 * Finds the first occurrence of an element in an array.
	 */
	public static int indexOf(byte[] array, byte value) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == value) {
				return i;
			}
		}
		return -1;
	}
	/**
	 * Returns <code>true</code> if an array contains given value.
	 */
	public static boolean contains(byte[] array, byte value) {
		return indexOf(array, value) != -1;
	}
	/**
	 * Finds the first occurrence of given value in an array from specified given position.
	 */
	public static int indexOf(byte[] array, byte value, int startIndex) {
		for (int i = startIndex; i < array.length; i++) {
			if (array[i] == value) {
				return i;
			}
		}
		return -1;
	}
	/**
	 * Finds the first occurrence in an array from specified given position and upto given length.
	 */
	public static int indexOf(byte[] array, byte value, int startIndex, int endIndex) {
		for (int i = startIndex; i < endIndex; i++) {
			if (array[i] == value) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Finds the first occurrence of an element in an array.
	 */
	public static int indexOf(char[] array, char value) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == value) {
				return i;
			}
		}
		return -1;
	}
	/**
	 * Returns <code>true</code> if an array contains given value.
	 */
	public static boolean contains(char[] array, char value) {
		return indexOf(array, value) != -1;
	}
	/**
	 * Finds the first occurrence of given value in an array from specified given position.
	 */
	public static int indexOf(char[] array, char value, int startIndex) {
		for (int i = startIndex; i < array.length; i++) {
			if (array[i] == value) {
				return i;
			}
		}
		return -1;
	}
	/**
	 * Finds the first occurrence in an array from specified given position and upto given length.
	 */
	public static int indexOf(char[] array, char value, int startIndex, int endIndex) {
		for (int i = startIndex; i < endIndex; i++) {
			if (array[i] == value) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Finds the first occurrence of an element in an array.
	 */
	public static int indexOf(short[] array, short value) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == value) {
				return i;
			}
		}
		return -1;
	}
	/**
	 * Returns <code>true</code> if an array contains given value.
	 */
	public static boolean contains(short[] array, short value) {
		return indexOf(array, value) != -1;
	}
	/**
	 * Finds the first occurrence of given value in an array from specified given position.
	 */
	public static int indexOf(short[] array, short value, int startIndex) {
		for (int i = startIndex; i < array.length; i++) {
			if (array[i] == value) {
				return i;
			}
		}
		return -1;
	}
	/**
	 * Finds the first occurrence in an array from specified given position and upto given length.
	 */
	public static int indexOf(short[] array, short value, int startIndex, int endIndex) {
		for (int i = startIndex; i < endIndex; i++) {
			if (array[i] == value) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Finds the first occurrence of an element in an array.
	 */
	public static int indexOf(int[] array, int value) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == value) {
				return i;
			}
		}
		return -1;
	}
	/**
	 * Returns <code>true</code> if an array contains given value.
	 */
	public static boolean contains(int[] array, int value) {
		return indexOf(array, value) != -1;
	}
	/**
	 * Finds the first occurrence of given value in an array from specified given position.
	 */
	public static int indexOf(int[] array, int value, int startIndex) {
		for (int i = startIndex; i < array.length; i++) {
			if (array[i] == value) {
				return i;
			}
		}
		return -1;
	}
	/**
	 * Finds the first occurrence in an array from specified given position and upto given length.
	 */
	public static int indexOf(int[] array, int value, int startIndex, int endIndex) {
		for (int i = startIndex; i < endIndex; i++) {
			if (array[i] == value) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Finds the first occurrence of an element in an array.
	 */
	public static int indexOf(long[] array, long value) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == value) {
				return i;
			}
		}
		return -1;
	}
	/**
	 * Returns <code>true</code> if an array contains given value.
	 */
	public static boolean contains(long[] array, long value) {
		return indexOf(array, value) != -1;
	}
	/**
	 * Finds the first occurrence of given value in an array from specified given position.
	 */
	public static int indexOf(long[] array, long value, int startIndex) {
		for (int i = startIndex; i < array.length; i++) {
			if (array[i] == value) {
				return i;
			}
		}
		return -1;
	}
	/**
	 * Finds the first occurrence in an array from specified given position and upto given length.
	 */
	public static int indexOf(long[] array, long value, int startIndex, int endIndex) {
		for (int i = startIndex; i < endIndex; i++) {
			if (array[i] == value) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Finds the first occurrence of an element in an array.
	 */
	public static int indexOf(boolean[] array, boolean value) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == value) {
				return i;
			}
		}
		return -1;
	}
	/**
	 * Returns <code>true</code> if an array contains given value.
	 */
	public static boolean contains(boolean[] array, boolean value) {
		return indexOf(array, value) != -1;
	}
	/**
	 * Finds the first occurrence of given value in an array from specified given position.
	 */
	public static int indexOf(boolean[] array, boolean value, int startIndex) {
		for (int i = startIndex; i < array.length; i++) {
			if (array[i] == value) {
				return i;
			}
		}
		return -1;
	}
	/**
	 * Finds the first occurrence in an array from specified given position and upto given length.
	 */
	public static int indexOf(boolean[] array, boolean value, int startIndex, int endIndex) {
		for (int i = startIndex; i < endIndex; i++) {
			if (array[i] == value) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Finds the first occurrence of value in <code>float</code> array.
	 */
	public static int indexOf(float[] array, float value) {
		for (int i = 0; i < array.length; i++) {
			if (Float.compare(array[i], value) == 0) {
				return i;
			}
		}
		return -1;
	}
	/**
	 * Returns <code>true</code> if <code>float</code> array contains given value.
	 */
	public static boolean contains(float[] array, float value) {
		return indexOf(array, value) != -1;
	}
	/**
	 * Finds the first occurrence of given value in <code>float</code>
	 * array from specified given position.
	 */
	public static int indexOf(float[] array, float value, int startIndex) {
		for (int i = startIndex; i < array.length; i++) {
			if (Float.compare(array[i], value) == 0) {
				return i;
			}
		}
		return -1;
	}
	/**
	 * Finds the first occurrence in <code>float</code> array from specified given position and upto given length.
	 */
	public static int indexOf(float[] array, float value, int startIndex, int endIndex) {
		for (int i = startIndex; i < endIndex; i++) {
			if (Float.compare(array[i], value) == 0) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Finds the first occurrence of value in <code>double</code> array.
	 */
	public static int indexOf(double[] array, double value) {
		for (int i = 0; i < array.length; i++) {
			if (Double.compare(array[i], value) == 0) {
				return i;
			}
		}
		return -1;
	}
	/**
	 * Returns <code>true</code> if <code>double</code> array contains given value.
	 */
	public static boolean contains(double[] array, double value) {
		return indexOf(array, value) != -1;
	}
	/**
	 * Finds the first occurrence of given value in <code>double</code>
	 * array from specified given position.
	 */
	public static int indexOf(double[] array, double value, int startIndex) {
		for (int i = startIndex; i < array.length; i++) {
			if (Double.compare(array[i], value) == 0) {
				return i;
			}
		}
		return -1;
	}
	/**
	 * Finds the first occurrence in <code>double</code> array from specified given position and upto given length.
	 */
	public static int indexOf(double[] array, double value, int startIndex, int endIndex) {
		for (int i = startIndex; i < endIndex; i++) {
			if (Double.compare(array[i], value) == 0) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Finds the first occurrence in an array.
	 */
	public static int indexOf(Object[] array, Object value) {
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(value)) {
				return i;
			}
		}
		return -1;
	}
	public static boolean contains(Object[] array, Object value) {
		return indexOf(array, value) != -1;
	}

	/**
	 * Finds the first occurrence in an array from specified given position.
	 */
	public static int indexOf(Object[] array, Object value, int startIndex) {
		for (int i = startIndex; i < array.length; i++) {
			if (array[i].equals(value)) {
				return i;
			}
		}
		return -1;
	}
	public static boolean contains(Object[] array, Object value, int startIndex) {
		return indexOf(array, value, startIndex) != -1;
	}




	// ---------------------------------------------------------------- indexof 2


	/**
	 * Finds the first occurrence in an array.
	 */
	public static int indexOf(byte[] array, byte[] sub) {
		return indexOf(array, sub, 0, array.length);
	}
	public static boolean contains(byte[] array, byte[] sub) {
		return indexOf(array, sub) != -1;
	}


	/**
	 * Finds the first occurrence in an array from specified given position.
	 */
	public static int indexOf(byte[] array, byte[] sub, int startIndex) {
		return indexOf(array, sub, startIndex, array.length);
	}

	/**
	 * Finds the first occurrence in an array from specified given position and upto given length.
	 */
	public static int indexOf(byte[] array, byte[] sub, int startIndex, int endIndex) {
		int sublen = sub.length;
		if (sublen == 0) {
			return startIndex;
		}
		int total = endIndex - sublen + 1;
		byte c = sub[0];
	mainloop:
		for (int i = startIndex; i < total; i++) {
			if (array[i] != c) {
				continue;
			}
			int j = 1;
			int k = i + 1;
			while (j < sublen) {
				if (sub[j] != array[k]) {
					continue mainloop;
				}
				j++; k++;
			}
			return i;
		}
		return -1;
	}

	/**
	 * Finds the first occurrence in an array.
	 */
	public static int indexOf(char[] array, char[] sub) {
		return indexOf(array, sub, 0, array.length);
	}
	public static boolean contains(char[] array, char[] sub) {
		return indexOf(array, sub) != -1;
	}


	/**
	 * Finds the first occurrence in an array from specified given position.
	 */
	public static int indexOf(char[] array, char[] sub, int startIndex) {
		return indexOf(array, sub, startIndex, array.length);
	}

	/**
	 * Finds the first occurrence in an array from specified given position and upto given length.
	 */
	public static int indexOf(char[] array, char[] sub, int startIndex, int endIndex) {
		int sublen = sub.length;
		if (sublen == 0) {
			return startIndex;
		}
		int total = endIndex - sublen + 1;
		char c = sub[0];
	mainloop:
		for (int i = startIndex; i < total; i++) {
			if (array[i] != c) {
				continue;
			}
			int j = 1;
			int k = i + 1;
			while (j < sublen) {
				if (sub[j] != array[k]) {
					continue mainloop;
				}
				j++; k++;
			}
			return i;
		}
		return -1;
	}

	/**
	 * Finds the first occurrence in an array.
	 */
	public static int indexOf(short[] array, short[] sub) {
		return indexOf(array, sub, 0, array.length);
	}
	public static boolean contains(short[] array, short[] sub) {
		return indexOf(array, sub) != -1;
	}


	/**
	 * Finds the first occurrence in an array from specified given position.
	 */
	public static int indexOf(short[] array, short[] sub, int startIndex) {
		return indexOf(array, sub, startIndex, array.length);
	}

	/**
	 * Finds the first occurrence in an array from specified given position and upto given length.
	 */
	public static int indexOf(short[] array, short[] sub, int startIndex, int endIndex) {
		int sublen = sub.length;
		if (sublen == 0) {
			return startIndex;
		}
		int total = endIndex - sublen + 1;
		short c = sub[0];
	mainloop:
		for (int i = startIndex; i < total; i++) {
			if (array[i] != c) {
				continue;
			}
			int j = 1;
			int k = i + 1;
			while (j < sublen) {
				if (sub[j] != array[k]) {
					continue mainloop;
				}
				j++; k++;
			}
			return i;
		}
		return -1;
	}

	/**
	 * Finds the first occurrence in an array.
	 */
	public static int indexOf(int[] array, int[] sub) {
		return indexOf(array, sub, 0, array.length);
	}
	public static boolean contains(int[] array, int[] sub) {
		return indexOf(array, sub) != -1;
	}


	/**
	 * Finds the first occurrence in an array from specified given position.
	 */
	public static int indexOf(int[] array, int[] sub, int startIndex) {
		return indexOf(array, sub, startIndex, array.length);
	}

	/**
	 * Finds the first occurrence in an array from specified given position and upto given length.
	 */
	public static int indexOf(int[] array, int[] sub, int startIndex, int endIndex) {
		int sublen = sub.length;
		if (sublen == 0) {
			return startIndex;
		}
		int total = endIndex - sublen + 1;
		int c = sub[0];
	mainloop:
		for (int i = startIndex; i < total; i++) {
			if (array[i] != c) {
				continue;
			}
			int j = 1;
			int k = i + 1;
			while (j < sublen) {
				if (sub[j] != array[k]) {
					continue mainloop;
				}
				j++; k++;
			}
			return i;
		}
		return -1;
	}

	/**
	 * Finds the first occurrence in an array.
	 */
	public static int indexOf(long[] array, long[] sub) {
		return indexOf(array, sub, 0, array.length);
	}
	public static boolean contains(long[] array, long[] sub) {
		return indexOf(array, sub) != -1;
	}


	/**
	 * Finds the first occurrence in an array from specified given position.
	 */
	public static int indexOf(long[] array, long[] sub, int startIndex) {
		return indexOf(array, sub, startIndex, array.length);
	}

	/**
	 * Finds the first occurrence in an array from specified given position and upto given length.
	 */
	public static int indexOf(long[] array, long[] sub, int startIndex, int endIndex) {
		int sublen = sub.length;
		if (sublen == 0) {
			return startIndex;
		}
		int total = endIndex - sublen + 1;
		long c = sub[0];
	mainloop:
		for (int i = startIndex; i < total; i++) {
			if (array[i] != c) {
				continue;
			}
			int j = 1;
			int k = i + 1;
			while (j < sublen) {
				if (sub[j] != array[k]) {
					continue mainloop;
				}
				j++; k++;
			}
			return i;
		}
		return -1;
	}

	/**
	 * Finds the first occurrence in an array.
	 */
	public static int indexOf(boolean[] array, boolean[] sub) {
		return indexOf(array, sub, 0, array.length);
	}
	public static boolean contains(boolean[] array, boolean[] sub) {
		return indexOf(array, sub) != -1;
	}


	/**
	 * Finds the first occurrence in an array from specified given position.
	 */
	public static int indexOf(boolean[] array, boolean[] sub, int startIndex) {
		return indexOf(array, sub, startIndex, array.length);
	}

	/**
	 * Finds the first occurrence in an array from specified given position and upto given length.
	 */
	public static int indexOf(boolean[] array, boolean[] sub, int startIndex, int endIndex) {
		int sublen = sub.length;
		if (sublen == 0) {
			return startIndex;
		}
		int total = endIndex - sublen + 1;
		boolean c = sub[0];
	mainloop:
		for (int i = startIndex; i < total; i++) {
			if (array[i] != c) {
				continue;
			}
			int j = 1;
			int k = i + 1;
			while (j < sublen) {
				if (sub[j] != array[k]) {
					continue mainloop;
				}
				j++; k++;
			}
			return i;
		}
		return -1;
	}

	/**
	 * Finds the first occurrence in an array.
	 */
	public static int indexOf(float[] array, float[] sub) {
		return indexOf(array, sub, 0, array.length);
	}
	public static boolean contains(float[] array, float[] sub) {
		return indexOf(array, sub) != -1;
	}


	/**
	 * Finds the first occurrence in an array from specified given position.
	 */
	public static int indexOf(float[] array, float[] sub, int startIndex) {
		return indexOf(array, sub, startIndex, array.length);
	}

	/**
	 * Finds the first occurrence in an array from specified given position and upto given length.
	 */
	public static int indexOf(float[] array, float[] sub, int startIndex, int endIndex) {
		int sublen = sub.length;
		if (sublen == 0) {
			return startIndex;
		}
		int total = endIndex - sublen + 1;
		float c = sub[0];
	mainloop:
		for (int i = startIndex; i < total; i++) {
			if (Float.compare(array[i], c) != 0) {
				continue;
			}
			int j = 1;
			int k = i + 1;
			while (j < sublen) {
				if (Float.compare(sub[j], array[k]) != 0) {
					continue mainloop;
				}
				j++; k++;
			}
			return i;
		}
		return -1;
	}

	/**
	 * Finds the first occurrence in an array.
	 */
	public static int indexOf(double[] array, double[] sub) {
		return indexOf(array, sub, 0, array.length);
	}
	public static boolean contains(double[] array, double[] sub) {
		return indexOf(array, sub) != -1;
	}


	/**
	 * Finds the first occurrence in an array from specified given position.
	 */
	public static int indexOf(double[] array, double[] sub, int startIndex) {
		return indexOf(array, sub, startIndex, array.length);
	}

	/**
	 * Finds the first occurrence in an array from specified given position and upto given length.
	 */
	public static int indexOf(double[] array, double[] sub, int startIndex, int endIndex) {
		int sublen = sub.length;
		if (sublen == 0) {
			return startIndex;
		}
		int total = endIndex - sublen + 1;
		double c = sub[0];
	mainloop:
		for (int i = startIndex; i < total; i++) {
			if (Double.compare(array[i], c) != 0) {
				continue;
			}
			int j = 1;
			int k = i + 1;
			while (j < sublen) {
				if (Double.compare(sub[j], array[k]) != 0) {
					continue mainloop;
				}
				j++; k++;
			}
			return i;
		}
		return -1;
	}


	// ---------------------------------------------------------------- toString

	/**
	 * Converts an array to string. Elements are separated by comma.
	 * Returned string contains no brackets.
	 */
	public static String toString(Object[] array) {
		if (array == null) {
			return NULL;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			if (i != 0) {
				sb.append(',');
			}
			sb.append(array[i]);
		}
		return sb.toString();
	}

	/**
	 * Converts an array to string. Elements are separated by comma.
	 * Returned string contains no brackets.
	 */
	public static String toString(String[] array) {
		if (array == null) {
			return NULL;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			if (i != 0) {
				sb.append(',');
			}
			sb.append(array[i]);
		}
		return sb.toString();
	}

	/**
	 * Converts an array to string. Elements are separated by comma.
	 * Returned string contains no brackets.
	 */
	public static String toString(byte[] array) {
		if (array == null) {
			return NULL;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			if (i != 0) {
				sb.append(',');
			}
			sb.append(array[i]);
		}
		return sb.toString();
	}

	/**
	 * Converts an array to string. Elements are separated by comma.
	 * Returned string contains no brackets.
	 */
	public static String toString(char[] array) {
		if (array == null) {
			return NULL;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			if (i != 0) {
				sb.append(',');
			}
			sb.append(array[i]);
		}
		return sb.toString();
	}

	/**
	 * Converts an array to string. Elements are separated by comma.
	 * Returned string contains no brackets.
	 */
	public static String toString(short[] array) {
		if (array == null) {
			return NULL;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			if (i != 0) {
				sb.append(',');
			}
			sb.append(array[i]);
		}
		return sb.toString();
	}

	/**
	 * Converts an array to string. Elements are separated by comma.
	 * Returned string contains no brackets.
	 */
	public static String toString(int[] array) {
		if (array == null) {
			return NULL;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			if (i != 0) {
				sb.append(',');
			}
			sb.append(array[i]);
		}
		return sb.toString();
	}

	/**
	 * Converts an array to string. Elements are separated by comma.
	 * Returned string contains no brackets.
	 */
	public static String toString(long[] array) {
		if (array == null) {
			return NULL;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			if (i != 0) {
				sb.append(',');
			}
			sb.append(array[i]);
		}
		return sb.toString();
	}

	/**
	 * Converts an array to string. Elements are separated by comma.
	 * Returned string contains no brackets.
	 */
	public static String toString(float[] array) {
		if (array == null) {
			return NULL;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			if (i != 0) {
				sb.append(',');
			}
			sb.append(array[i]);
		}
		return sb.toString();
	}

	/**
	 * Converts an array to string. Elements are separated by comma.
	 * Returned string contains no brackets.
	 */
	public static String toString(double[] array) {
		if (array == null) {
			return NULL;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			if (i != 0) {
				sb.append(',');
			}
			sb.append(array[i]);
		}
		return sb.toString();
	}

	/**
	 * Converts an array to string. Elements are separated by comma.
	 * Returned string contains no brackets.
	 */
	public static String toString(boolean[] array) {
		if (array == null) {
			return NULL;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			if (i != 0) {
				sb.append(',');
			}
			sb.append(array[i]);
		}
		return sb.toString();
	}


	/**
	 * Converts an array to string array.
	 */
	public static String[] toStringArray(Object[] array) {
		if (array == null) {
			return null;
		}
		String[] result = new String[array.length];
		for (int i = 0; i < array.length; i++) {
			result[i] = StringUtil.toString(array[i]);
		}
		return result;
	}

	/**
	 * Converts an array to string array.
	 */
	public static String[] toStringArray(String[] array) {
		if (array == null) {
			return null;
		}
		String[] result = new String[array.length];
		for (int i = 0; i < array.length; i++) {
			result[i] = String.valueOf(array[i]);
		}
		return result;
	}

	/**
	 * Converts an array to string array.
	 */
	public static String[] toStringArray(byte[] array) {
		if (array == null) {
			return null;
		}
		String[] result = new String[array.length];
		for (int i = 0; i < array.length; i++) {
			result[i] = String.valueOf(array[i]);
		}
		return result;
	}

	/**
	 * Converts an array to string array.
	 */
	public static String[] toStringArray(char[] array) {
		if (array == null) {
			return null;
		}
		String[] result = new String[array.length];
		for (int i = 0; i < array.length; i++) {
			result[i] = String.valueOf(array[i]);
		}
		return result;
	}

	/**
	 * Converts an array to string array.
	 */
	public static String[] toStringArray(short[] array) {
		if (array == null) {
			return null;
		}
		String[] result = new String[array.length];
		for (int i = 0; i < array.length; i++) {
			result[i] = String.valueOf(array[i]);
		}
		return result;
	}

	/**
	 * Converts an array to string array.
	 */
	public static String[] toStringArray(int[] array) {
		if (array == null) {
			return null;
		}
		String[] result = new String[array.length];
		for (int i = 0; i < array.length; i++) {
			result[i] = String.valueOf(array[i]);
		}
		return result;
	}

	/**
	 * Converts an array to string array.
	 */
	public static String[] toStringArray(long[] array) {
		if (array == null) {
			return null;
		}
		String[] result = new String[array.length];
		for (int i = 0; i < array.length; i++) {
			result[i] = String.valueOf(array[i]);
		}
		return result;
	}

	/**
	 * Converts an array to string array.
	 */
	public static String[] toStringArray(float[] array) {
		if (array == null) {
			return null;
		}
		String[] result = new String[array.length];
		for (int i = 0; i < array.length; i++) {
			result[i] = String.valueOf(array[i]);
		}
		return result;
	}

	/**
	 * Converts an array to string array.
	 */
	public static String[] toStringArray(double[] array) {
		if (array == null) {
			return null;
		}
		String[] result = new String[array.length];
		for (int i = 0; i < array.length; i++) {
			result[i] = String.valueOf(array[i]);
		}
		return result;
	}

	/**
	 * Converts an array to string array.
	 */
	public static String[] toStringArray(boolean[] array) {
		if (array == null) {
			return null;
		}
		String[] result = new String[array.length];
		for (int i = 0; i < array.length; i++) {
			result[i] = String.valueOf(array[i]);
		}
		return result;
	}
}