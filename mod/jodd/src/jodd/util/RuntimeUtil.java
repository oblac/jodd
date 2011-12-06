// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

/**
 * Runtime utilities.
 */
public class RuntimeUtil {

	public static final Runtime RUNTIME = Runtime.getRuntime();

	// ---------------------------------------------------------------- execution

	/**
	 * Returns current method signature.
	 */
	public static String currentMethod() {
		StackTraceElement[] ste = new Exception().getStackTrace();
		int ndx = (ste.length > 1) ? 1 : 0;
		return new Exception().getStackTrace()[ndx].toString();
	}

	// ---------------------------------------------------------------- memory

	/**
	 * Returns the amount of available memory (free memory plus never allocated memory).
	 */
	public static long availableMemory() {
		return RUNTIME.freeMemory() + (RUNTIME.maxMemory() - RUNTIME.totalMemory());
	}

	/**
	 * Returns the amount of available memory in percents.
	 */
	public static float availableMemoryPercent() {
		return availableMemory() * 100.0f / RUNTIME.maxMemory();
	}

	/**
	 * Compacts memory as much as possible by forcing garbage collection.
	 */
	public static void compactMemory() {
		try {
			final byte[][] unused = new byte[128][];
			for(int i = unused.length; i-- != 0;) {
				unused[i] = new byte[2000000000];
			}
		} catch(OutOfMemoryError ignore) {
		}
		System.gc();
	}

}
