// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.cache;

import java.util.LinkedHashMap;
import java.util.Iterator;

/**
 * FIFO (first in first out) cache.
 *
 * <p>
 * FIFO (first in first out): just adds items to the cache as they are accessed, putting them in a queue or buffer and
 * not changing their location in the buffer; when the cache is full, items are ejected in the order they were
 * added. Cache access overhead is constant time regardless of the size of the cache. The advantage of this algorithm
 * is that it's simple and fast; it can be implemented using a simple array and an index. The disadvantage is that
 * it's not very smart; it doesn't make any effort to keep more commonly used items in cache.<br>
 * Summary for FIFO: fast, not adaptive, not scan resistant.
 */
public class FIFOCache<K, V> extends AbstractCacheMap<K, V> {

	public FIFOCache(int cacheSize) {
		this(cacheSize, 0);
	}

	/**
	 * Creates a new LRU cache.
	 */
	public FIFOCache(int cacheSize, long timeout) {
		this.cacheSize = cacheSize;
		this.timeout = timeout;
		cacheMap = new LinkedHashMap<K, CacheObject>(cacheSize + 1, 1.0f, false);
	}


	// ---------------------------------------------------------------- prune

	/**
	 * Prune expired objects and, if cache is still full, the first one.
	 */
	@Override
	public int prune() {
        int count = 0;
		CacheObject first = null;
		Iterator<CacheObject> values = cacheMap.values().iterator();
		while (values.hasNext()) {
			CacheObject co = values.next();
			if (co.isExpired() == true) {
				values.remove();
				count++;
			}
			if (first == null) {
				first = co;
			}
		}
		if (isFull()) {
			if (first != null) {
				cacheMap.remove(first.key);
				count++;
			}
		}
		return count;
	}
}
