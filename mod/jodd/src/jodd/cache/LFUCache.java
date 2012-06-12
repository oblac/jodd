// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.cache;

import java.util.HashMap;
import java.util.Iterator;

/**
 * LFU (least frequently used) cache. Frequency is calculated as access count. This cache
 * is resistant on 'new usages scenario': when some object is removed from the cache,
 * access count of all items in cache is decreased by access count of removed value.
 * This allows new frequent elements to come into the cache.
 * <p>
 * Frequency of use data is kept on all items. The most frequently used items are kept in the cache.
 * Because of the bookkeeping requirements, cache access overhead increases logarithmically with cache size.
 * The advantage is that long term usage patterns are captured well, incidentally making the algorithm scan resistant;
 * the disadvantage, besides the larger access overhead, is that the algorithm doesn't adapt quickly to changing
 * usage patterns, and in particular doesn't help with temporally clustered accesses.
 * <p>
 * Summary for LFU: not fast, captures frequency of use, scan resistant.
 */
public class LFUCache<K,V> extends AbstractCacheMap<K,V> {

	public LFUCache(int maxSize) {
		this(maxSize, 0);
	}

	public LFUCache(int maxSize, long timeout) {
		this.cacheSize = maxSize;
		this.timeout = timeout;
		cacheMap = new HashMap<K, CacheObject<K,V>>(maxSize + 1);
	}

	// ---------------------------------------------------------------- prune

	/**
	 * Prunes expired and, if cache is still full, the LFU element(s) from the cache.
	 * On LFU removal, access count is normalized to value which had removed object.
	 * Returns the number of removed objects.
	 */
	@Override
	protected int pruneCache() {
        int count = 0;
		CacheObject<K,V> comin = null;

		// remove expired items and find cached object with minimal access count
		Iterator<CacheObject<K,V>> values = cacheMap.values().iterator();
		while (values.hasNext()) {
			CacheObject<K,V> co = values.next();
			if (co.isExpired() == true) {
				values.remove();
				onRemove(co.key, co.cachedObject);
				count++;
				continue;
			}
			
			if (comin == null) {
				comin = co;
			} else {
				if (co.accessCount < comin.accessCount) {
					comin = co;
				}
			}
		}

		if (isFull() == false) {
			return count;
		}

		// decrease access count to all cached objects
		if (comin != null) {
			long minAccessCount = comin.accessCount;

			values = cacheMap.values().iterator();
			while (values.hasNext()) {
				CacheObject<K, V> co = values.next();
				co.accessCount -= minAccessCount;
				if (co.accessCount <= 0) {
					values.remove();
					onRemove(co.key, co.cachedObject);
					count++;					
				}
			}
		}
		return count;
	}

	/**
	 * Callback method invoked on cached object removal.
	 * By default does nothing.
	 */
	protected void onRemove(K key, V cachedObject) {
	}

}
