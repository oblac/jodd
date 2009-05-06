
// Copyright (c) 2003-2006, Jodd Team (jodd.sf.net). All Rights Reserved.

package jodd.util;

import java.lang.reflect.Array;

/**
 * More array utilities.
 * <b>DO NOT MODIFY: this source is generated.</b> 
 */
public class ArraysUtil {



	// ---------------------------------------------------------------- merge

	/**
	 * Merge arrays.
	 */
	@SuppressWarnings({"unchecked"})
	public static <T> T[] merge(T[]... arrays) {
		Class componentType =  arrays.getClass().getComponentType().getComponentType();
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
	 * Merge arrays.
	 */
	public static String[] merge(String[]... arrays) {
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
	 * Merge arrays.
	 */
	public static byte[] merge(byte[]... arrays) {
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
	 * Merge arrays.
	 */
	public static char[] merge(char[]... arrays) {
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
	 * Merge arrays.
	 */
	public static short[] merge(short[]... arrays) {
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
	 * Merge arrays.
	 */
	public static int[] merge(int[]... arrays) {
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
	 * Merge arrays.
	 */
	public static long[] merge(long[]... arrays) {
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
	 * Merge arrays.
	 */
	public static float[] merge(float[]... arrays) {
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
	 * Merge arrays.
	 */
	public static double[] merge(double[]... arrays) {
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
	 * Merge arrays.
	 */
	public static boolean[] merge(boolean[]... arrays) {
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


	// ---------------------------------------------------------------- join

	/**
	 * Joins two arrays.
	 */
	public static <T> T[] join(T[] first, T[] second) {
		return join(first, second, null);
	}

	/**
	 * Joins two arrays.
	 */
	@SuppressWarnings({"unchecked"})
	public static <T> T[] join(T[] first, T[] second, Class componentType) {
		if (componentType == null) {
			componentType = first.getClass().getComponentType();
		}
		T[] temp = (T[]) Array.newInstance(componentType, first.length + second.length);
		System.arraycopy(first, 0, temp, 0, first.length);
		System.arraycopy(second, 0, temp, first.length, second.length);
		return temp;
	}


	/**
	 * Joins two arrays.
	 */
	public static String[] join(String[] first, String[] second) {
		String[] temp = new String[first.length + second.length];
		System.arraycopy(first, 0, temp, 0, first.length);
		System.arraycopy(second, 0, temp, first.length, second.length);
		return temp;
	}

	/**
	 * Joins two arrays.
	 */
	public static byte[] join(byte[] first, byte[] second) {
		byte[] temp = new byte[first.length + second.length];
		System.arraycopy(first, 0, temp, 0, first.length);
		System.arraycopy(second, 0, temp, first.length, second.length);
		return temp;
	}

	/**
	 * Joins two arrays.
	 */
	public static char[] join(char[] first, char[] second) {
		char[] temp = new char[first.length + second.length];
		System.arraycopy(first, 0, temp, 0, first.length);
		System.arraycopy(second, 0, temp, first.length, second.length);
		return temp;
	}

	/**
	 * Joins two arrays.
	 */
	public static short[] join(short[] first, short[] second) {
		short[] temp = new short[first.length + second.length];
		System.arraycopy(first, 0, temp, 0, first.length);
		System.arraycopy(second, 0, temp, first.length, second.length);
		return temp;
	}

	/**
	 * Joins two arrays.
	 */
	public static int[] join(int[] first, int[] second) {
		int[] temp = new int[first.length + second.length];
		System.arraycopy(first, 0, temp, 0, first.length);
		System.arraycopy(second, 0, temp, first.length, second.length);
		return temp;
	}

	/**
	 * Joins two arrays.
	 */
	public static long[] join(long[] first, long[] second) {
		long[] temp = new long[first.length + second.length];
		System.arraycopy(first, 0, temp, 0, first.length);
		System.arraycopy(second, 0, temp, first.length, second.length);
		return temp;
	}

	/**
	 * Joins two arrays.
	 */
	public static float[] join(float[] first, float[] second) {
		float[] temp = new float[first.length + second.length];
		System.arraycopy(first, 0, temp, 0, first.length);
		System.arraycopy(second, 0, temp, first.length, second.length);
		return temp;
	}

	/**
	 * Joins two arrays.
	 */
	public static double[] join(double[] first, double[] second) {
		double[] temp = new double[first.length + second.length];
		System.arraycopy(first, 0, temp, 0, first.length);
		System.arraycopy(second, 0, temp, first.length, second.length);
		return temp;
	}

	/**
	 * Joins two arrays.
	 */
	public static boolean[] join(boolean[] first, boolean[] second) {
		boolean[] temp = new boolean[first.length + second.length];
		System.arraycopy(first, 0, temp, 0, first.length);
		System.arraycopy(second, 0, temp, first.length, second.length);
		return temp;
	}


	// ---------------------------------------------------------------- resize

	/**
	 * Resizes an array.
	 */
	public static <T> T[] resize(T[] buffer, int newSize) {
		return resize(buffer, newSize, null);
	}
		
	/**
	 * Resizes an array.
	 */
	@SuppressWarnings({"unchecked"})
	public static <T> T[] resize(T[] buffer, int newSize, Class<?> componentType) {
		if (componentType == null) {
			componentType =  buffer.getClass().getComponentType();
		}
		T[] temp = (T[]) Array.newInstance(componentType, newSize);
		System.arraycopy(buffer, 0, temp, 0, buffer.length >= newSize ? newSize : buffer.length);
		return temp;
	}

	/**
	 * Resizes an array.
	 */
	public static String[] resize(String buffer[], int newSize) {
		String temp[] = new String[newSize];
		System.arraycopy(buffer, 0, temp, 0, buffer.length >= newSize ? newSize : buffer.length);
		return temp;
	}

	/**
	 * Resizes an array.
	 */
	public static byte[] resize(byte buffer[], int newSize) {
		byte temp[] = new byte[newSize];
		System.arraycopy(buffer, 0, temp, 0, buffer.length >= newSize ? newSize : buffer.length);
		return temp;
	}

	/**
	 * Resizes an array.
	 */
	public static char[] resize(char buffer[], int newSize) {
		char temp[] = new char[newSize];
		System.arraycopy(buffer, 0, temp, 0, buffer.length >= newSize ? newSize : buffer.length);
		return temp;
	}

	/**
	 * Resizes an array.
	 */
	public static short[] resize(short buffer[], int newSize) {
		short temp[] = new short[newSize];
		System.arraycopy(buffer, 0, temp, 0, buffer.length >= newSize ? newSize : buffer.length);
		return temp;
	}

	/**
	 * Resizes an array.
	 */
	public static int[] resize(int buffer[], int newSize) {
		int temp[] = new int[newSize];
		System.arraycopy(buffer, 0, temp, 0, buffer.length >= newSize ? newSize : buffer.length);
		return temp;
	}

	/**
	 * Resizes an array.
	 */
	public static long[] resize(long buffer[], int newSize) {
		long temp[] = new long[newSize];
		System.arraycopy(buffer, 0, temp, 0, buffer.length >= newSize ? newSize : buffer.length);
		return temp;
	}

	/**
	 * Resizes an array.
	 */
	public static float[] resize(float buffer[], int newSize) {
		float temp[] = new float[newSize];
		System.arraycopy(buffer, 0, temp, 0, buffer.length >= newSize ? newSize : buffer.length);
		return temp;
	}

	/**
	 * Resizes an array.
	 */
	public static double[] resize(double buffer[], int newSize) {
		double temp[] = new double[newSize];
		System.arraycopy(buffer, 0, temp, 0, buffer.length >= newSize ? newSize : buffer.length);
		return temp;
	}

	/**
	 * Resizes an array.
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
		T[] t = resize(buffer, buffer.length + 1, newElement.getClass());
		t[buffer.length] = newElement;
		return t;
	}

	/**
	 * Appends an element to array.
	 */
	public static String[] append(String buffer[], String newElement) {
		String[] t = resize(buffer, buffer.length + 1);
		t[buffer.length] = newElement;
		return t;
	}

	/**
	 * Appends an element to array.
	 */
	public static byte[] append(byte buffer[], byte newElement) {
		byte[] t = resize(buffer, buffer.length + 1);
		t[buffer.length] = newElement;
		return t;
	}

	/**
	 * Appends an element to array.
	 */
	public static char[] append(char buffer[], char newElement) {
		char[] t = resize(buffer, buffer.length + 1);
		t[buffer.length] = newElement;
		return t;
	}

	/**
	 * Appends an element to array.
	 */
	public static short[] append(short buffer[], short newElement) {
		short[] t = resize(buffer, buffer.length + 1);
		t[buffer.length] = newElement;
		return t;
	}

	/**
	 * Appends an element to array.
	 */
	public static int[] append(int buffer[], int newElement) {
		int[] t = resize(buffer, buffer.length + 1);
		t[buffer.length] = newElement;
		return t;
	}

	/**
	 * Appends an element to array.
	 */
	public static long[] append(long buffer[], long newElement) {
		long[] t = resize(buffer, buffer.length + 1);
		t[buffer.length] = newElement;
		return t;
	}

	/**
	 * Appends an element to array.
	 */
	public static float[] append(float buffer[], float newElement) {
		float[] t = resize(buffer, buffer.length + 1);
		t[buffer.length] = newElement;
		return t;
	}

	/**
	 * Appends an element to array.
	 */
	public static double[] append(double buffer[], double newElement) {
		double[] t = resize(buffer, buffer.length + 1);
		t[buffer.length] = newElement;
		return t;
	}

	/**
	 * Appends an element to array.
	 */
	public static boolean[] append(boolean buffer[], boolean newElement) {
		boolean[] t = resize(buffer, buffer.length + 1);
		t[buffer.length] = newElement;
		return t;
	}


	// ---------------------------------------------------------------- subarray

	/**
	 * Returns subarray.
	 */
	public static <T> T[] subarray(T[] buffer, int offset, int length) {
		return subarray(buffer, offset, length, null);
	}

	/**
	 * Returns subarray.
	 */
	@SuppressWarnings({"unchecked"})
	public static <T> T[] subarray(T[] buffer, int offset, int length, Class componentType) {
		if (componentType == null) {
			componentType = buffer.getClass().getComponentType();
		}
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
	 * Inserts one array into another.
	 */
	public static <T> T[] insert(T[] dest, T[] src, int offset) {
		return insert(dest, src, offset, null);
	}

	/**
	 * Inserts one array into another.
	 */
	@SuppressWarnings({"unchecked"})
	public static <T> T[] insert(T[] dest, T[] src, int offset, Class componentType) {
		if (componentType == null) {
			componentType = dest.getClass().getComponentType();
		}
		T[] temp = (T[]) Array.newInstance(componentType, dest.length + src.length);
		System.arraycopy(dest, 0, temp, 0, offset);
		System.arraycopy(src, 0, temp, offset, src.length);
		System.arraycopy(dest, offset, temp, src.length + offset, dest.length - offset);
		return temp;
	}

	/**
	 * Inserts one array into another.
	 */
	public static String[] insert(String[] dest, String[] src, int offset) {
		String[] temp = new String[dest.length + src.length];
		System.arraycopy(dest, 0, temp, 0, offset);
		System.arraycopy(src, 0, temp, offset, src.length);
		System.arraycopy(dest, offset, temp, src.length + offset, dest.length - offset);
		return temp;
	}

	/**
	 * Inserts one array into another.
	 */
	public static byte[] insert(byte[] dest, byte[] src, int offset) {
		byte[] temp = new byte[dest.length + src.length];
		System.arraycopy(dest, 0, temp, 0, offset);
		System.arraycopy(src, 0, temp, offset, src.length);
		System.arraycopy(dest, offset, temp, src.length + offset, dest.length - offset);
		return temp;
	}

	/**
	 * Inserts one array into another.
	 */
	public static char[] insert(char[] dest, char[] src, int offset) {
		char[] temp = new char[dest.length + src.length];
		System.arraycopy(dest, 0, temp, 0, offset);
		System.arraycopy(src, 0, temp, offset, src.length);
		System.arraycopy(dest, offset, temp, src.length + offset, dest.length - offset);
		return temp;
	}

	/**
	 * Inserts one array into another.
	 */
	public static short[] insert(short[] dest, short[] src, int offset) {
		short[] temp = new short[dest.length + src.length];
		System.arraycopy(dest, 0, temp, 0, offset);
		System.arraycopy(src, 0, temp, offset, src.length);
		System.arraycopy(dest, offset, temp, src.length + offset, dest.length - offset);
		return temp;
	}

	/**
	 * Inserts one array into another.
	 */
	public static int[] insert(int[] dest, int[] src, int offset) {
		int[] temp = new int[dest.length + src.length];
		System.arraycopy(dest, 0, temp, 0, offset);
		System.arraycopy(src, 0, temp, offset, src.length);
		System.arraycopy(dest, offset, temp, src.length + offset, dest.length - offset);
		return temp;
	}

	/**
	 * Inserts one array into another.
	 */
	public static long[] insert(long[] dest, long[] src, int offset) {
		long[] temp = new long[dest.length + src.length];
		System.arraycopy(dest, 0, temp, 0, offset);
		System.arraycopy(src, 0, temp, offset, src.length);
		System.arraycopy(dest, offset, temp, src.length + offset, dest.length - offset);
		return temp;
	}

	/**
	 * Inserts one array into another.
	 */
	public static float[] insert(float[] dest, float[] src, int offset) {
		float[] temp = new float[dest.length + src.length];
		System.arraycopy(dest, 0, temp, 0, offset);
		System.arraycopy(src, 0, temp, offset, src.length);
		System.arraycopy(dest, offset, temp, src.length + offset, dest.length - offset);
		return temp;
	}

	/**
	 * Inserts one array into another.
	 */
	public static double[] insert(double[] dest, double[] src, int offset) {
		double[] temp = new double[dest.length + src.length];
		System.arraycopy(dest, 0, temp, 0, offset);
		System.arraycopy(src, 0, temp, offset, src.length);
		System.arraycopy(dest, offset, temp, src.length + offset, dest.length - offset);
		return temp;
	}

	/**
	 * Inserts one array into another.
	 */
	public static boolean[] insert(boolean[] dest, boolean[] src, int offset) {
		boolean[] temp = new boolean[dest.length + src.length];
		System.arraycopy(dest, 0, temp, 0, offset);
		System.arraycopy(src, 0, temp, offset, src.length);
		System.arraycopy(dest, offset, temp, src.length + offset, dest.length - offset);
		return temp;
	}


	// ---------------------------------------------------------------- insertAt

	/**
	 * Inserts one array into another by replacing specified offset.
	 */
	public static <T> T[] insertAt(T[] dest, T[] src, int offset) {
		return insertAt(dest, src, offset, null);
	}

	/**
	 * Inserts one array into another by replacing specified offset.
	 */
	@SuppressWarnings({"unchecked"})
	public static <T> T[] insertAt(T[] dest, T[] src, int offset, Class componentType) {
		if (componentType == null) {
			componentType = dest.getClass().getComponentType();
		}
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
	 * Finds the first occurrence in an array.
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
	 * Finds the first occurrence in an array from specified given position.
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
	 * Finds the first occurrence in an array.
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
	 * Finds the first occurrence in an array from specified given position.
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
	 * Finds the first occurrence in an array.
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
	 * Finds the first occurrence in an array from specified given position.
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
	 * Finds the first occurrence in an array.
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
	 * Finds the first occurrence in an array from specified given position.
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
	 * Finds the first occurrence in an array.
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
	 * Finds the first occurrence in an array from specified given position.
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
	 * Finds the first occurrence in an array.
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
	 * Finds the first occurrence in an array from specified given position.
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
	 * Finds the first occurrence in an array.
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
	 * Finds the first occurrence in an array from specified given position.
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
	 * Finds the first occurrence in an array from specified given position and upto given length.
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
	 * Finds the first occurrence in an array.
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
	 * Finds the first occurrence in an array from specified given position.
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
	 * Finds the first occurrence in an array from specified given position and upto given length.
	 */
	public static int indexOf(double[] array, double value, int startIndex, int endIndex) {
		for (int i = startIndex; i < endIndex; i++) {
			if (Double.compare(array[i], value) == 0) {
				return i;
			}
		}
		return -1;
	}


	// ---------------------------------------------------------------- indexof 2


	/**
	 * Finds the first occurrence in an array.
	 */
	public static int indexOf(byte[] array, byte[] sub) {
		return indexOf(array, sub, 0, array.length);
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
}