// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.cache;

import junit.framework.TestCase;
import jodd.util.ThreadUtil;

public class LFUCacheTest extends TestCase {

	public void testCache() {
		Cache<String, String> cache = new LFUCache<String, String>(3);
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

	public void testCache2() {
		Cache<String, String> cache = new LFUCache<String, String>(3);
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
		cache.put("4", "4");			// since this is LFU cache, 1 AND 2 will be removed, but not 3
		assertNotNull(cache.get("3"));
		assertNotNull(cache.get("4"));
		assertEquals(2, cache.size());
	}

	public void testCacheTime() {
		Cache<String, String> cache = new LFUCache<String, String>(3);
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

	public void testPrune() {
		Cache<String, String> cache = new LFUCache<String, String>(3);
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

	public void testBoosting() {
		Cache<String, String> cache = new LFUCache<String, String>(3);
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

		assertNull(cache.get("1"));		// 1 is less frequent and it is out of cache
		assertNotNull(cache.get("4"));	// 4 is new and it is inside

		cache.get("3");
		cache.get("2");

		// bad sequence
		cache.put("5", "5");
		cache.get("5");				// situation: 2(1), 3(2), 5(1)   value(accessCount)
		cache.put("4", "4");
		cache.get("4");				// situation: 3(1), 4(1)
		cache.put("5", "5");
		cache.get("5");				// situation: 3(1), 4(1), 5(1)
		cache.put("4", "4");
		cache.get("4");				// situation: 4(1)

		assertNull(cache.get("1"));
		assertNull(cache.get("2"));
		assertNull(cache.get("3"));
		assertNotNull(cache.get("4"));
		assertNull(cache.get("5"));
	}

}