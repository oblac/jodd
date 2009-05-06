// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.cache;

import junit.framework.TestCase;
import jodd.util.ThreadUtil;

public class FIFOCacheTest extends TestCase {

	public void testCache() {
		Cache cache = new FIFOCache(3);
		cache.put("1", "1");
		cache.put("2", "2");
		assertFalse(cache.isFull());
		cache.put("3", "3");
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
	}

	public void testCacheTime() {
		Cache cache = new FIFOCache(3);
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

}
