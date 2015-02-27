// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import jodd.Jodd;

/**
 * Runtime utilities.
 */
public class RuntimeUtil {

	// ---------------------------------------------------------------- memory

	/**
	 * Returns the amount of available memory (free memory plus never allocated memory).
	 */
	public static long availableMemory() {
		return Runtime.getRuntime().freeMemory() + (Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory());
	}

	/**
	 * Returns the amount of available memory in percents.
	 */
	public static float availableMemoryPercent() {
		return availableMemory() * 100.0f / Runtime.getRuntime().maxMemory();
	}

	/**
	 * Compacts memory as much as possible by allocating huge memory block
	 * and then forcing garbage collection.
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

	// ---------------------------------------------------------------- location

	/**
	 * Returns location of the class. If class is not in a jar, it's classpath
	 * is returned; otherwise the jar location.
	 */
	public static String classLocation(Class clazz) {
		return clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
	}

	/**
	 * Returns Jodd {@link #classLocation(Class) location}.
	 * @see #classLocation
	 */
	public static String joddLocation() {
		return classLocation(Jodd.class);
	}

}
