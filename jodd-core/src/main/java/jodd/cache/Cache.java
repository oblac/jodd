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

import java.util.Map;

/**
 * Cache interface.
 */
public interface Cache<K, V> {

	/**
	 * Returns cache size or <code>0</code> if there is no size limit.
	 */
	int limit();

	/**
	 * Returns default timeout or <code>0</code> if it is not set.
	 */
	long timeout();

	/**
	 * Adds an object to the cache with default timeout.
	 * @see Cache#put(Object, Object, long)
	 */
	void put(K key, V object);

	/**
	 * Adds an object to the cache with specified timeout after which it becomes expired.
	 * If cache is full, {@link #prune()} is invoked to make room for new object.
	 * Cached value must be non-null.
	 */
	void put(K key, V object, long timeout);

	/**
	 * Retrieves an object from the cache. Returns <code>null</code> if object
	 * is not longer in cache or if it is expired.
	 */
	V get(K key);

	/**
	 * Prunes objects from cache and returns the number of removed objects.
	 * Used strategy depends on cache implementation.
	 */
	int prune();

	/**
	 * Returns <code>true</code> if max cache capacity has been reached
	 * only if cache is size limited.
	 */
	boolean isFull();

	/**
	 * Removes an object from the cache and returns removed value of {@code null}
	 * if object was not in the cache or was expired.
	 */
	V remove(K key);

	/**
	 * Clears current cache.
	 */
	void clear();

	/**
	 * Returns current cache size.
	 */
	int size();

	/**
	 * Returns <code>true</code> if cache is empty.
	 */
	boolean isEmpty();

	/**
	 * Creates a snapshot from current cache values. Returned values may not
	 * longer be valid or they might be already expired! Cache is locked during
	 * the snapshot creation.
	 */
	Map<K, V> snapshot();
}
