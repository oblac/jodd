// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Iterator;


/**
 * LRU (least recently used) cache.
 *
 * <p>
 * Items are added to the cache as they are accessed; when the cache is full, the least recently used item is ejected.
 * This type of cache is typically implemented as a linked list, so that an item in cache, when it is accessed again,
 * can be moved back up to the head of the queue; items are ejected from the tail of the queue. Cache access overhead
 * is again constant time. This algorithm is simple and fast, and it has a significant advantage over FIFO in being
 * able to adapt somewhat to the data access pattern; frequently used items are less likely to be
 * ejected from the cache. The main disadvantage is that it can still get filled up with items that are
 * unlikely to be reaccessed soon; in particular, it can become useless in the face of scanning type accesses.
 * Nonetheless, this is by far the most frequently used caching algorithm.
 * <p>
 * Implementation note: unfortunately, it was not possible to have <code>onRemove</code> callback method,
 * since <code>LinkedHashMap</code> has its removal methods private.
 * <p>
 * Summary for LRU: fast, adaptive, not scan resistant.
 */
public class LRUCache<K, V> extends AbstractCacheMap<K, V> {

	public LRUCache(int cacheSize) {
		this(cacheSize, 0);
	}

	/**
	 * Creates a new LRU cache.
	 */
	public LRUCache(int cacheSize, long timeout) {
		this.cacheSize = cacheSize;
		this.timeout = timeout;
		cacheMap = new LinkedHashMap<K, CacheObject<K,V>>(cacheSize + 1, 1.0f, true) {
			@Override
			protected boolean removeEldestEntry(Map.Entry eldest) {
				return LRUCache.this.removeEldestEntry(size());
			}
		};
	}

	/**
	 * Removes the eldest entry if current cache size exceed cache size.
	 */
	protected boolean removeEldestEntry(int currentSize) {
		if (cacheSize == 0) {
			return false;
		}
		return currentSize > cacheSize;
	}

	// ---------------------------------------------------------------- prune

	/**
	 * Prune only expired objects, <code>LinkedHashMap</code> will take care of LRU if needed.
	 */
	@Override
	protected int pruneCache() {
		if (isPruneExpiredActive() == false) {
			return 0;
		}
        int count = 0;
		Iterator<CacheObject<K,V>> values = cacheMap.values().iterator();
		while (values.hasNext()) {
			CacheObject<K,V> co = values.next();
			if (co.isExpired() == true) {
				values.remove();
				count++;
			}
		}
		return count;
	}
}
