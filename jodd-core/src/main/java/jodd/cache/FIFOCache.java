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

import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * FIFO (first in first out) cache.
 *
 * <p>
 * FIFO (first in first out): just adds items to the cache as they are accessed, putting them in a queue or buffer and
 * not changing their location in the buffer; when the cache is full, items are ejected in the order they were
 * added. Cache access overhead is constant time regardless of the size of the cache. The advantage of this algorithm
 * is that it's simple and fast; it can be implemented using a simple array and an index. The disadvantage is that
 * it's not very smart; it doesn't make any effort to keep more commonly used items in cache.
 * <p>
 * Summary for FIFO: fast, not adaptive, not scan resistant.
 */
public class FIFOCache<K, V> extends AbstractCacheMap<K, V> {

	public FIFOCache(final int cacheSize) {
		this(cacheSize, 0);
	}

	/**
	 * Creates a new LRU cache.
	 */
	public FIFOCache(final int cacheSize, final long timeout) {
		this.cacheSize = cacheSize;
		this.timeout = timeout;
		cacheMap = new LinkedHashMap<>(cacheSize + 1, 1.0f, false);
	}


	// ---------------------------------------------------------------- prune

	/**
	 * Prune expired objects and, if cache is still full, the first one.
	 */
	@Override
	protected int pruneCache() {
        int count = 0;
		CacheObject<K,V> first = null;
		Iterator<CacheObject<K,V>> values = cacheMap.values().iterator();
		while (values.hasNext()) {
			CacheObject<K,V> co = values.next();
			if (co.isExpired()) {
				values.remove();
				onRemove(co.key, co.cachedObject);
				count++;
			}
			if (first == null) {
				first = co;
			}
		}
		if (isFull()) {
			if (first != null) {
				cacheMap.remove(first.key);
				onRemove(first.key, first.cachedObject);
				count++;
			}
		}
		return count;
	}
}
