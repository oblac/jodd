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

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread utilities.
 */
public class ThreadUtil {

	/**
	 * Puts a thread to sleep, without throwing an InterruptedException.
	 *
	 * @param ms     the length of time to sleep in milliseconds
	 */
	public static void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException iex) {
			Thread.currentThread().interrupt();
		}
	}


	/**
	 * Puts a thread to sleep forever.
	 */
	public static void sleep() {
		try {
			Thread.sleep(Long.MAX_VALUE);
		} catch (InterruptedException iex) {
			Thread.currentThread().interrupt();
		}
	}


	// ---------------------------------------------------------------- synchronization

	/**
	 * Waits for a object for synchronization purposes.
	 */
	public static void wait(Object obj) {
		synchronized (obj) {
			try {
				obj.wait();
			} catch (InterruptedException inex) {
				Thread.currentThread().interrupt();
			}
		}
	}

	/**
	 * Waits for a object or a timeout for synchronization purposes.
	 */
	public static void wait(Object obj, long timeout) {
		synchronized (obj) {
			try {
				obj.wait(timeout);
			} catch (InterruptedException inex) {
				Thread.currentThread().interrupt();
			}
		}
	}

	/**
	 * Notifies an object for synchronization purposes.
	 */
	public static void notify(Object obj){
		synchronized (obj) {
			obj.notify();
		}
	}

	/**
	 * Notifies an object for synchronization purposes.
	 */
	public static void notifyAll(Object obj){
		synchronized (obj) {
			obj.notifyAll();
		}
	}


	// ---------------------------------------------------------------- join


	public static void join(Thread thread) {
		try {
			thread.join();
		} catch (InterruptedException inex) {
			Thread.currentThread().interrupt();
		}
	}

	public static void join(Thread thread, long millis) {
		try {
			thread.join(millis);
		} catch (InterruptedException inex) {
			Thread.currentThread().interrupt();
		}
	}

	public static void join(Thread thread, long millis, int nanos) {
		try {
			thread.join(millis, nanos);
		} catch (InterruptedException inex) {
			Thread.currentThread().interrupt();
		}
	}


	// ---------------------------------------------------------------- pool

	/**
	 * Creates new daemon thread factory.
	 */
	public static ThreadFactory daemonThreadFactory(String name) {
		return daemonThreadFactory(name, Thread.NORM_PRIORITY);
	}
	/**
	 * Creates new daemon thread factory.
	 */
	public static ThreadFactory daemonThreadFactory(String name, int priority) {
		return new ThreadFactory() {
			private AtomicInteger count = new AtomicInteger();

			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
				thread.setName(name + '-' + count.incrementAndGet());
				thread.setDaemon(true);
				thread.setPriority(priority);
				return thread;
			}
		};
	}

}