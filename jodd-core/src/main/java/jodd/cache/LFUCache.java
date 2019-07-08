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

	public LFUCache(final int maxSize) {
		this(maxSize, 0);
	}

	public LFUCache(final int maxSize, final long timeout) {
		this.cacheSize = maxSize;
		this.timeout = timeout;
		cacheMap = new HashMap<>(maxSize + 1);
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
			if (co.isExpired()) {
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

		if (!isFull()) {
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

}
