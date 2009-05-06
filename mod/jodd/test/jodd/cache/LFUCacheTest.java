// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.cache;

import junit.framework.TestCase;
import jodd.util.ThreadUtil;

public class LFUCacheTest extends TestCase {

	public void testCache() {
		Cache cache = new LFUCache(3);
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

	public void testCacheTime() {
		Cache cache = new LFUCache(3);
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

}