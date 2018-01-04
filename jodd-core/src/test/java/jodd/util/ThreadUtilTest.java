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

import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadFactory;

import static org.junit.jupiter.api.Assertions.*;

/**
 * test class for {@link ThreadUtil}
 */
class ThreadUtilTest {

	@Test
	void testDaemonThreadFactory() {

		ThreadFactory threadFactory = ThreadUtil.daemonThreadFactory("jodd-thread");

		final StringBuilder sb = new StringBuilder();
		final Runnable runnable = () -> {sb.append("runnable instance for jodd junit test");};

		Thread t1 = threadFactory.newThread(runnable);
		Thread t2 = threadFactory.newThread(runnable);
		Thread t3 = threadFactory.newThread(runnable);

		// asserts
		// Thread #1
		assertNotNull(t1);
		assertEquals(Thread.NORM_PRIORITY, t1.getPriority());
		assertEquals("jodd-thread-1", t1.getName());
		assertEquals(true, t1.isDaemon());

		// Thread #2
		assertNotNull(t2);
		assertEquals(Thread.NORM_PRIORITY, t2.getPriority());
		assertEquals("jodd-thread-2", t2.getName());
		assertEquals(true, t2.isDaemon());

		// Thread #3
		assertNotNull(t3);
		assertEquals(Thread.NORM_PRIORITY, t3.getPriority());
		assertEquals("jodd-thread-3", t3.getName());
		assertEquals(true, t3.isDaemon());

		// check that no thread has been runned
		assertEquals(0, sb.length());
	}

}