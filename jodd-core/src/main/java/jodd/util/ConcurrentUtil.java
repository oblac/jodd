// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import java.util.concurrent.Semaphore;

/**
 * Misc concurrent utilities.
 */
public class ConcurrentUtil {

	public static void acquire(Semaphore semaphore) {
		try {
			semaphore.acquire();
		} catch (InterruptedException iex) {
			// ignore
		}
	}

	public static void acquire(Semaphore semaphore, int permits) {
		try {
			semaphore.acquire(permits);
		} catch (InterruptedException iex) {
			// ignore
		}
	}
	
	public static void waitForRelease(Semaphore semaphore) {
		try {
			semaphore.acquire();
		} catch (InterruptedException iex) {
			//ignore
		}
		semaphore.release();
	}
}
