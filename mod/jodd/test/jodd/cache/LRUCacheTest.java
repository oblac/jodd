// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.cache;

import junit.framework.TestCase;
import jodd.util.ThreadUtil;

public class LRUCacheTest extends TestCase {

	public void testCache() {
		Cache<String, String> cache = new LRUCache<String, String>(3);
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

	public void testCache2() {
		Cache<String, String> cache = new LRUCache<String, String>(3);
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
		cache.put("4", "4");
		assertNull(cache.get("3"));
		assertNotNull(cache.get("1"));
		assertNotNull(cache.get("2"));
		assertNotNull(cache.get("4"));
		cache.put("3", "3");
		assertNull(cache.get("1"));
	}


	public void testCacheTime() {
		Cache<String, String> cache = new LRUCache<String, String>(3);
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
	
	public void testPrune() {
		Cache<String, String> cache = new LRUCache<String, String>(3);
		cache.put("1", "1");
		cache.put("2", "2");
		cache.put("3", "3");

		assertEquals(0, cache.prune());
		assertEquals(3, cache.size());

		cache.put("4", "4");
		assertEquals(0, cache.prune());
		assertEquals(3, cache.size());
	}


}
