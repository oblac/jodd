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

public class LRUCacheTest extends BaseCacheTest {

	@Test
	public void testCache() {
		Cache<String, String> cache = new LRUCache<>(3);
		cache.put("1", "1");
		cache.put("2", "2");
		assertFalse(cache.isFull());
		cache.put("3", "3");
		assertTrue(cache.isFull());

		assertNotNull(cache.get("1"));
		assertNotNull(cache.get("2"));
		cache.put("4", "4");
		assertNull(cache.get("3"));
		assertNotNull(cache.get("1"));
		assertNotNull(cache.get("2"));
		cache.put("3", "3");
		assertNull(cache.get("4"));
	}

	@Test
	public void testCache2() {
		Cache<String, String> cache = new LRUCache<>(3);
		cache.put("1", "1");
		cache.put("2", "2");
		assertFalse(cache.isFull());
		cache.put("3", "3");
		assertTrue(cache.isFull());

		assertNotNull(cache.get("3"));
		assertNotNull(cache.get("3"));
		assertNotNull(cache.get("3"));  // boost usage of a 3, but this doesn't change a thing, since this is a LRU cache and not a LFU cache
		assertNotNull(cache.get("1"));
		assertNotNull(cache.get("2"));
		cache.put("4", "4");
		assertNull(cache.get("3"));
		assertNotNull(cache.get("1"));
		assertNotNull(cache.get("2"));
		assertNotNull(cache.get("4"));
		cache.put("3", "3");
		assertNull(cache.get("1"));
	}

	@Test
	public void testCacheTime() {
		Cache<String, String> cache = new LRUCache<>(3);
		cache.put("3", "3");
		cache.put("2", "2");
		assertNotNull(cache.get("2"));
		cache.put("1", "1", 50);
		assertNotNull(cache.get("1"));
		assertTrue(cache.isFull());

		ThreadUtil.sleep(100);
		assertNull(cache.get("1"));     // expired
		assertFalse(cache.isFull());
	}

	@Test
	public void testPrune() {
		Cache<String, String> cache = new LRUCache<>(3);
		cache.put("1", "1");
		cache.put("2", "2");
		cache.put("3", "3");

		assertEquals(0, cache.prune());
		assertEquals(3, cache.size());

		cache.put("4", "4");
		assertEquals(0, cache.prune());
		assertEquals(3, cache.size());
	}

	@Test
	public void testEndless() {
		Cache<String, String> cache = new LRUCache<>(0);
		assertFalse(cache.isFull());
		cache.put("1", "1");
		assertEquals(1, cache.size());
		assertFalse(cache.isFull());

		cache.put("2", "2");
		assertEquals(2, cache.size());
		assertFalse(cache.isFull());
	}

	@Override
	protected final <K,V> Cache<K,V> createCache(int size) {
		return new LRUCache<>(size);
	}
}
