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

/**
 * Values iterator for {@link jodd.cache.AbstractCacheMap}.
 */
public class CacheValuesIterator<V> implements Iterator<V> {

	private Iterator<? extends AbstractCacheMap<?, V>.CacheObject<?, V>> iterator;

	private AbstractCacheMap<?,V>.CacheObject<?,V> nextValue;

	CacheValuesIterator(AbstractCacheMap<?,V> abstractCacheMap) {
		iterator = abstractCacheMap.cacheMap.values().iterator();
		nextValue();
	}

	/**
	 * Resolves next value. If next value doesn't exist, next value will be <code>null</code>.
	 */
	private void nextValue() {
		while (iterator.hasNext()) {
			nextValue = iterator.next();
			if (nextValue.isExpired() == false) {
				return;
			}
		}
		nextValue = null;
	}

	/**
	 * Returns <code>true</code> if there are more elements in the cache.
	 */
	public boolean hasNext() {
		return nextValue != null;
	}

	/**
	 * Returns next non-expired element from the cache.
	 */
	public V next() {
		V cachedObject = nextValue.cachedObject;
		nextValue();
		return cachedObject;
	}

	/**
	 * Removes current non-expired element from the cache.
	 */
	public void remove() {
		iterator.remove();
	}
}
