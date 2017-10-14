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

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class BaseCacheTest {

	/**
	 * Creates cache instance.
	 */
	protected abstract <K,V> Cache<K,V> createCache(int size);

	@Test
	public void testSnapshot() {
		Cache<String, Integer> cache = createCache(3);

		cache.put("1", 1);
		assertEquals(1, cache.snapshot().size());
		assertEquals(1, cache.snapshot().get("1").intValue());

		cache.put("2", 2);
		cache.put("3", 3);
		assertEquals(3, cache.snapshot().size());

		cache.put("4", 4);
		assertEquals(3, cache.snapshot().size());
	}

	@Test
	public void testConcurrency() throws InterruptedException {
		final int total = 100000;
		final int threads = 100;

		Cache<Integer, String> cache = createCache(total);
		ExecutorService executorService = Executors.newFixedThreadPool(threads);

		final LongAdder taskCount = new LongAdder();
		final Random random = new Random();

		for (int i = 0; i < total; i++) {
			executorService.submit(() -> {
				cache.put(random.nextInt(10), "value", random.nextInt(50));
				cache.get(random.nextInt(10));
				taskCount.increment();
			});
		}

		executorService.shutdown();
		executorService.awaitTermination(1, TimeUnit.DAYS);

		assertEquals(total, taskCount.intValue());
	}

}
