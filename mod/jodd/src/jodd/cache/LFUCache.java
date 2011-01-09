// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.cache;

import java.util.HashMap;
import java.util.Iterator;

/**
 * LFU (least frequently used) cache.
 *
 * <p>
 * Frequency of use data is kept on all items. The most frequently used items are kept in the cache.
 * Because of the bookkeeping requirements, cache access overhead increases logarithmically with cache size.
 * The advantage is that long term usage patterns are captured well, incidentally making the algorithm scan resistant;
 * the disadvantage, besides the larger access overhead, is that the algorithm doesn't adapt quickly to changing
 * usage patterns, and in particular doesn't help with temporally clustered accesses.<br>
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
	public int prune() {
        int count = 0;
		CacheObject<K,V> comin = null;
		Iterator<CacheObject<K,V>> values = cacheMap.values().iterator();
		while (values.hasNext()) {
			CacheObject<K,V> co = values.next();
			if (co.isExpired() == true) {
				values.remove();
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
		if (comin != null) {
			values = cacheMap.values().iterator();
			while (values.hasNext()) {
				CacheObject co = values.next();
				co.accessCount -= comin.accessCount;
				if (co.accessCount <= 0) {
					values.remove();
					count++;					
				}
			}
		}
		return count;
	}

}
