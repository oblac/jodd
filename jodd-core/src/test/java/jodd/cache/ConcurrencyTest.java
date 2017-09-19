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

package jodd.cache;

import org.junit.jupiter.api.Test;

import java.util.ConcurrentModificationException;
import java.util.concurrent.Semaphore;

import static org.junit.jupiter.api.Assertions.*;

public class ConcurrencyTest {

	/**
	 * http://code.google.com/p/jodd/issues/detail?id=4
	 */
	@Test
	public void testPutGetAndPrune() throws InterruptedException {
		LFUCache<String, String> lfuCache = new LFUCache<>(2, 0);

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
