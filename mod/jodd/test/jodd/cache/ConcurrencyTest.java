// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.cache;

import junit.framework.TestCase;

import java.util.ConcurrentModificationException;
import java.util.concurrent.Semaphore;

public class ConcurrencyTest extends TestCase {

	/**
	 * http://code.google.com/p/jodd/issues/detail?id=4
 	 */
	public void testPutGetAndPrune() throws InterruptedException {
		LFUCache<String, String> lfuCache = new LFUCache<String, String>(2, 0);

		lfuCache.put("1", "value");
		assertFalse(lfuCache.isFull());
		lfuCache.put("2", "value");
		assertTrue(lfuCache.isFull());
		lfuCache.get("2");
		lfuCache.get("2");
		assertEquals(2, lfuCache.size());

		Semaphore semaphore = new Semaphore(2);
		Thread1 t1 = new Thread1(semaphore, lfuCache);
		Thread2 t2 = new Thread2(semaphore, lfuCache);

		t1.start();
		t2.start();
		semaphore.acquire();

		if (t1.exception != null) {
			t1.exception.printStackTrace();
		}
		assertNull(t1.exception);

		if (t2.exception != null) {
			t2.exception.printStackTrace();
		}
		assertNull(t2.exception);
	}

	public static class Thread1 extends SemaphoreThread {
		public Thread1(Semaphore semaphore, Cache<String, String> cache) {
			super(semaphore, cache);
		}
		@Override
		public void work() {
			long l = 100000;
			while (l-- > 0) {
				cache.put("3", "new value");
			}
		}
	}
	public static class Thread2 extends SemaphoreThread {
		public Thread2(Semaphore semaphore, Cache<String, String> cache) {
			super(semaphore, cache);
		}
		@Override
		public void work() {
			long l = 100000;
			while (l-- > 0) {
				cache.put("1", "back");
				cache.get("1");
				cache.put("2", "back");
			}
		}
	}

	public abstract static class SemaphoreThread extends Thread {
		final Cache<String, String> cache;
		final Semaphore semaphore;
		public ConcurrentModificationException exception;
		protected SemaphoreThread(Semaphore semaphore, Cache<String, String> cache) {
			this.semaphore = semaphore;
			this.cache = cache;
			try {
				semaphore.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		@Override
		public void run() {
			try {
				work();
			} catch (ConcurrentModificationException cmex) {
				exception = cmex;
			}
			semaphore.release();
		}
		protected abstract void work();
	}
}
