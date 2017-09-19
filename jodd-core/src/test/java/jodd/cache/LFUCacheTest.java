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

import jodd.mutable.MutableInteger;
import jodd.util.ThreadUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LFUCacheTest {

	@Test
	public void testCache() {
		Cache<String, String> cache = new LFUCache<>(3);
		cache.put("1", "1");
		cache.put("2", "2");
		assertFalse(cache.isFull());
		cache.put("3", "3");
		assertTrue(cache.isFull());

		assertNotNull(cache.get("1"));
		assertNotNull(cache.get("2"));
		cache.put("4", "4");        // new element, cache is full, prune is invoked
		assertNull(cache.get("3"));
		assertNotNull(cache.get("1"));
		assertNotNull(cache.get("2"));
		cache.put("3", "3");
		assertNull(cache.get("4"));
		assertNotNull(cache.get("3"));
	}

	@Test
	public void testCache2() {
		Cache<String, String> cache = new LFUCache<>(3);
		cache.put("1", "1");
		cache.put("2", "2");
		assertFalse(cache.isFull());
		cache.put("3", "3");
		assertTrue(cache.isFull());

		assertNotNull(cache.get("3"));
		assertNotNull(cache.get("3"));
		assertNotNull(cache.get("3"));  // boost usage of a 3
		assertNotNull(cache.get("1"));
		assertNotNull(cache.get("2"));
		cache.put("4", "4");            // since this is LFU cache, 1 AND 2 will be removed, but not 3
		assertNotNull(cache.get("3"));
		assertNotNull(cache.get("4"));
		assertEquals(2, cache.size());
	}

	@Test
	public void testCacheTime() {
		Cache<String, String> cache = new LFUCache<>(3);
		cache.put("1", "1", 50);
		assertNotNull(cache.get("1"));
		assertNotNull(cache.get("1"));  // boost usage
		cache.put("2", "2");
		cache.get("2");
		assertFalse(cache.isFull());
		cache.put("3", "3");
		assertTrue(cache.isFull());

		ThreadUtil.sleep(100);
		assertNull(cache.get("1"));     // expired
		assertFalse(cache.isFull());

		cache.put("1", "1", 50);
		assertNotNull(cache.get("1"));
		assertNotNull(cache.get("1"));

		ThreadUtil.sleep(100);
		assertTrue(cache.isFull());
		cache.put("4", "4");
		assertNotNull(cache.get("3"));
		assertNotNull(cache.get("2"));
		assertNotNull(cache.get("4"));
		assertNull(cache.get("1"));
	}

	@Test
	public void testPrune() {
		Cache<String, String> cache = new LFUCache<>(3);
		cache.put("1", "1");
		cache.put("2", "2");
		cache.put("3", "3");

		assertEquals(3, cache.size());
		assertEquals(3, cache.prune());
		assertEquals(0, cache.size());

		cache.put("4", "4");
		assertEquals(0, cache.prune());
		assertEquals(1, cache.size());
	}

	@Test
	public void testBoosting() {
		Cache<String, String> cache = new LFUCache<>(3);
		cache.put("1", "1");
		cache.put("2", "2");
		cache.put("3", "3");

		cache.get("3");
		cache.get("3");
		cache.get("3");
		cache.get("3");
		cache.get("2");
		cache.get("2");
		cache.get("2");
		cache.get("1");
		cache.get("1");

		assertEquals(3, cache.size());

		cache.put("4", "4");

		assertNull(cache.get("1"));       // 1 is less frequent and it is out of cache
		assertNotNull(cache.get("4"));    // 4 is new and it is inside

		cache.get("3");
		cache.get("2");

		// bad sequence
		cache.put("5", "5");
		cache.get("5");                // situation: 2(1), 3(2), 5(1)   value(accessCount)
		cache.put("4", "4");
		cache.get("4");                // situation: 3(1), 4(1)
		cache.put("5", "5");
		cache.get("5");                // situation: 3(1), 4(1), 5(1)
		cache.put("4", "4");
		cache.get("4");                // situation: 4(1)

		assertEquals(3, cache.size());

		assertNull(cache.get("1"));
		assertNull(cache.get("2"));
		assertNotNull(cache.get("3"));
		assertNotNull(cache.get("4"));
		assertNotNull(cache.get("5"));
	}

	@Test
	public void testOnRemove() {
		final MutableInteger mutableInteger = new MutableInteger();
		Cache<String, String> cache = new LFUCache<String, String>(2) {
			@Override
			protected void onRemove(String key, String cachedObject) {
				mutableInteger.value++;
			}
		};

		cache.put("1", "val1");
		cache.put("2", "val2");
		assertEquals(0, mutableInteger.value);
		cache.put("3", "val3");
		assertEquals(2, mutableInteger.value);
	}


}
