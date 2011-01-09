// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import junit.framework.TestCase;

import java.util.Random;

public class MutexTest extends TestCase {
	
	static Mutex lock = new Mutex();
	
	static int count;
	static int end;

	static Random r = new Random();

	static class FooThread extends Thread {
		int id;
		FooThread(int id) {
			this.id = id;
		}
		@Override
		public void run() {
			if (r.nextBoolean() == true) {
				lock.lock();
			} else {
				while (lock.tryLock() == false) {
					ThreadUtil.sleep(30);
				}
			}
			assertEquals(0, count);
			count++;
			assertEquals(1, count);
			ThreadUtil.sleep(40);
			assertEquals(1, count);
			count--;
			assertEquals(0, count);
			lock.unlock();
			end++;
		}
	}

	public void testMutex() {
		for (int i = 0; i < 10; i++) {
			end = 0;
			int total = 100;
			while (total > 0) {
				FooThread thread = new FooThread(total);
				thread.setDaemon(false);
				thread.start();
				total--;
			}
			while (end < 100) {
				ThreadUtil.sleep(25);
			}
			assertEquals(100, end);
		}

	}
}
