// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.cache;

import junit.framework.TestCase;
import jodd.util.ThreadUtil;

import java.util.Iterator;

public class FIFOCacheTest extends TestCase {

	public void testCache() {
		Cache<String, String> cache = new FIFOCache<String, String>(3);
		assertEquals(3, cache.getCacheSize());
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
		assertEquals(3, cache.getCacheSize());
		assertEquals(0, cache.size());
	}

	public void testCacheTime() {
		Cache<String, String> cache = new FIFOCache<String, String>(3);
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

	public void testCacheIterator() {
		Cache<String, String> cache = new FIFOCache<String, String>(3);
		cache.put("1", "1");
		cache.put("2", "2");
		cache.put("3", "3", 50);

		ThreadUtil.sleep(100);

		Iterator<String> it = cache.iterator();
		int count = 0;
		while (it.hasNext()) {
			String s = it.next();
			if (s.equals("3")) {
				fail();
			}
			count++;
		}
		assertEquals(2, count);
	}

	public void testCacheTime2() {
		Cache<String, String> cache = new FIFOCache<String, String>(3, 50);
		cache.put("1", "1");
		cache.put("2", "2");
		assertEquals(2, cache.size());
		assertEquals(50, cache.getCacheTimeout());

		ThreadUtil.sleep(100);
		assertEquals(2, cache.prune());
		assertEquals(0, cache.size());
		assertTrue(cache.isEmpty());
	}


	public void testPrune() {
		Cache<String, String> cache = new FIFOCache<String, String>(3);
		cache.put("1", "1");
		cache.put("2", "2");
		cache.put("3", "3");

		assertEquals(1, cache.prune());
		assertEquals(2, cache.size());
	}

}