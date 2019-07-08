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

	public LRUCache(final int cacheSize) {
		this(cacheSize, 0);
	}

	/**
	 * Creates a new LRU cache.
	 */
	public LRUCache(final int cacheSize, final long timeout) {
		this.cacheSize = cacheSize;
		this.timeout = timeout;
		cacheMap = new LinkedHashMap<K, CacheObject<K,V>>(cacheSize + 1, 1.0f, true) {
			@Override
			protected boolean removeEldestEntry(final Map.Entry eldest) {
				return LRUCache.this.removeEldestEntry(size());
			}
		};
	}

	/**
	 * Removes the eldest entry if current cache size exceed cache size.
	 */
	protected boolean removeEldestEntry(final int currentSize) {
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
		if (!isPruneExpiredActive()) {
			return 0;
		}
        int count = 0;
		Iterator<CacheObject<K,V>> values = cacheMap.values().iterator();
		while (values.hasNext()) {
			CacheObject<K,V> co = values.next();
			if (co.isExpired()) {
				values.remove();
				onRemove(co.key, co.cachedObject);
				count++;
			}
		}
		return count;
	}
}
