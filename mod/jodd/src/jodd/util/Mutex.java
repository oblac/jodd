// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

/**
 * Provides simple mutual exclusion.
 * <p>
 * Interesting, the previous implementation based on Leslie Lamport's
 * "Fast Mutal Exclusion" algorithm was not working, probably due wrong
 * implementation.
 * <p>
 * Object (i.e. resource) that uses Mutex must be accessed only between
 * {@link #lock()} and {@link #unlock()}.
 */
public class Mutex {

	private Thread owner;

	/**
	 * Blocks execution and acquires a lock. If already inside of critical block,
	 * it simply returns.
	 */
	public synchronized void lock() {
		Thread currentThread = Thread.currentThread();
		if (owner == currentThread) {
			return;
		}
		while (owner != null) {
			try {
				wait();
			} catch (InterruptedException iex) {
				notify();
			}
		}
		owner = currentThread;
	}

	/**
	 * Acquires a lock. If lock already acquired, returns <code>false</code>,
	 */
	public synchronized boolean tryLock() {
		Thread currentThread = Thread.currentThread();
		if (owner == currentThread) {
			return true;
		}
		if (owner != null) {
			return false;
		}
		owner = currentThread;
		return true;
	}

	/**
	 * Releases a lock.
	 */
	public synchronized void unlock() {
		owner = null;
		notify();
	}
}
