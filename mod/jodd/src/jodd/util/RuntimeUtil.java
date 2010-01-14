// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

/**
 * Runtime utilities.
 */
public class RuntimeUtil {

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
	 * Returns amount of total memory in bytes.
	 */
	public static long totalMemory() {
		return Runtime.getRuntime().totalMemory();
	}

	/**
	 * Returns amount of free memory in bytes.
	 */
	public static long freeMemory() {
		return Runtime.getRuntime().freeMemory();
	}

	/**
	 * Returns percents of free memory.
	 */
	public static double freeMemoryPercents() {
		Runtime runtime = Runtime.getRuntime();
		return ((double) runtime.freeMemory() / runtime.totalMemory()) * 100.0;
	}

	/**
	 * Returns amount of used memory in bytes.
	 */
	public static long usedMemory() {
		return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	}

	/**
	 * Returns percents of used memory.
	 */
	public static double usedMemoryPercents() {
		Runtime runtime = Runtime.getRuntime();
		long totalMemory = runtime.totalMemory();
		return ((double) (totalMemory - runtime.freeMemory()) / totalMemory) * 100.0;
	}
	
}
