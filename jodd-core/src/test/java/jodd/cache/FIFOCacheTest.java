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

import jodd.util.ThreadUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FIFOCacheTest extends BaseCacheTest {

	@Test
	public void testCache() {
		Cache<String, String> cache = new FIFOCache<>(3);
		assertEquals(3, cache.limit());
		assertEquals(0, cache.size());

		cache.put("1", "1");
		cache.put("2", "2");
		assertEquals(2, cache.size());
		assertFalse(cache.isFull());
		cache.put("3", "3");
		assertEquals(3, cache.size());
		assertTrue(cache.isFull());

		assertNotNull(cache.get("1"));
		assertNotNull(cache.get("2"));
		cache.put("4", "4");        // new element, cache is full, prune is invoked
		assertNull(cache.get("1"));
		assertNotNull(cache.get("2"));
		assertNotNull(cache.get("3"));
		assertNotNull(cache.get("4"));
		cache.put("1", "1");

		assertNull(cache.get("2"));
		assertNotNull(cache.get("3"));
		assertNotNull(cache.get("4"));
		assertNotNull(cache.get("1"));

		cache.clear();
		assertEquals(3, cache.limit());
		assertEquals(0, cache.size());
	}

	@Test
	public void testCacheTime() {
		Cache<String, String> cache = new FIFOCache<>(3);
		cache.put("1", "1");
		cache.put("2", "2");
		cache.put("3", "3", 50);

		ThreadUtil.sleep(100);
		cache.put("4", "4");

		assertNotNull(cache.get("1"));
		assertNotNull(cache.get("2"));
		assertNull(cache.get("3"));
		assertNotNull(cache.get("4"));
	}

	@Test
	public void testCacheTime2() {
		Cache<String, String> cache = new FIFOCache<>(3, 50);
		cache.put("1", "1");
		cache.put("2", "2");
		assertEquals(2, cache.size());
		assertEquals(50, cache.timeout());

		ThreadUtil.sleep(100);
		assertEquals(2, cache.prune());
		assertEquals(0, cache.size());
		assertTrue(cache.isEmpty());
	}

	@Test
	public void testPrune() {
		Cache<String, String> cache = new FIFOCache<>(3);
		cache.put("1", "1");
		cache.put("2", "2");
		cache.put("3", "3");

		assertEquals(1, cache.prune());
		assertEquals(2, cache.size());
	}

	@Test
	public void testOrder() {
		FIFOCache<String, Integer> fifoCache = new FIFOCache<>(3);
		fifoCache.put("1", Integer.valueOf(1));
		fifoCache.put("2", Integer.valueOf(2));
		fifoCache.put("3", Integer.valueOf(3));
		fifoCache.put("1", Integer.valueOf(1));
		fifoCache.put("1", Integer.valueOf(11));

		assertEquals(3, fifoCache.size());

		assertEquals(Integer.valueOf(11), fifoCache.get("1"));
		assertEquals(Integer.valueOf(2), fifoCache.get("2"));
		assertEquals(Integer.valueOf(3), fifoCache.get("3"));
	}

	@Override
	protected final <K,V> Cache<K,V> createCache(int size) {
		return new FIFOCache<>(size);
	}

}
