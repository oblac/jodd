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
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.StampedLock;

/**
 * Default implementation of timed and size cache map.
 * Implementations should:
 * <ul>
 * <li>create a new cache map</li>
 * <li>implements own <code>prune</code> strategy</li>
 * </ul>
 * Uses <code>ReentrantReadWriteLock</code> to synchronize access.
 * Since upgrading from a read lock to the write lock is not possible,
 * be careful withing {@link #get(Object)} method.
 */
public abstract class AbstractCacheMap<K,V> implements Cache<K,V> {

	class CacheObject<K2,V2> {
		CacheObject(final K2 key, final V2 object, final long ttl) {
			this.key = key;
			this.cachedObject = object;
			this.ttl = ttl;
			this.lastAccess = System.currentTimeMillis();
		}

		final K2 key;
		final V2 cachedObject;
		long lastAccess;		// time of last access
		long accessCount;		// number of accesses
		long ttl;				// objects timeout (time-to-live), 0 = no timeout

		boolean isExpired() {
			if (ttl == 0) {
				return false;
			}
			return lastAccess + ttl < System.currentTimeMillis();
		}
		V2 getObject() {
			lastAccess = System.currentTimeMillis();
			accessCount++;
			return cachedObject;
		}
    }

	protected Map<K,CacheObject<K,V>> cacheMap;
	private final StampedLock lock = new StampedLock();

	// ---------------------------------------------------------------- properties

	protected int cacheSize;      // max cache size, 0 = no limit

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int limit() {
		return cacheSize;
	}

	protected long timeout;     // default timeout, 0 = no timeout

	/**
	 * Returns default cache timeout or <code>0</code> if it is not set.
	 * Timeout can be set individually for each object.
	 */
	@Override
	public long timeout() {
		return timeout;
	}

	/**
	 * Identifies if objects has custom timeouts.
	 * Should be used to determine if prune for existing objects is needed.
	 */
	protected boolean existCustomTimeout;

	/**
	 * Returns <code>true</code> if prune of expired objects should be invoked.
	 * For internal use.
	 */
	protected boolean isPruneExpiredActive() {
		return (timeout != 0) || existCustomTimeout;
	}


	// ---------------------------------------------------------------- put


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void put(final K key, final V object) {
		put(key, object, timeout);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void put(final K key, final V object, final long timeout) {
		Objects.requireNonNull(object);

		final long stamp = lock.writeLock();

		try {
			CacheObject<K,V> co = new CacheObject<>(key, object, timeout);
			if (timeout != 0) {
				existCustomTimeout = true;
			}
			if (isReallyFull(key)) {
				pruneCache();
			}
			cacheMap.put(key, co);
		}
		finally {
			lock.unlockWrite(stamp);
		}
	}


	// ---------------------------------------------------------------- get

	protected int hitCount;
	protected int missCount;

	/**
	 * Returns hit count.
	 */
	public int getHitCount() {
		return hitCount;
	}

	/**
	 * Returns miss count.
	 */
	public int getMissCount() {
		return missCount;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public V get(final K key) {
		long stamp = lock.readLock();

		try {
			CacheObject<K,V> co = cacheMap.get(key);
			if (co == null) {
				missCount++;
				return null;
			}
			if (co.isExpired()) {
				final long newStamp = lock.tryConvertToWriteLock(stamp);

				if (newStamp != 0L) {
					stamp = newStamp;
					// lock is upgraded to write lock
				}
				else {
					// manually upgrade lock to write lock
					lock.unlockRead(stamp);
					stamp = lock.writeLock();
				}

				CacheObject<K,V> removedCo = cacheMap.remove(key);
				if (removedCo != null) {
					onRemove(removedCo.key, removedCo.cachedObject);
				}

				missCount++;
				return null;
			}

			hitCount++;
			return co.getObject();
		}
		finally {
			lock.unlock(stamp);
		}
	}

	// ---------------------------------------------------------------- prune

	/**
	 * Prune implementation.
	 */
	protected abstract int pruneCache();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int prune() {
		final long stamp = lock.writeLock();
		try {
			return pruneCache();
		}
		finally {
			lock.unlockWrite(stamp);
		}
	}

	// ---------------------------------------------------------------- common

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isFull() {
		if (cacheSize == 0) {
			return false;
		}
		return cacheMap.size() >= cacheSize;
	}

	protected boolean isReallyFull(final K key) {
		if (cacheSize == 0) {
			return false;
		}
		if (cacheMap.size() >= cacheSize) {
			return !cacheMap.containsKey(key);
		}
		else {
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public V remove(final K key) {
		V removedValue = null;
		final long stamp = lock.writeLock();
		try {
			CacheObject<K,V> co = cacheMap.remove(key);

			if (co != null) {
				onRemove(co.key, co.cachedObject);
				removedValue = co.cachedObject;
			}
		}
		finally {
			lock.unlockWrite(stamp);
		}
		return removedValue;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		final long stamp = lock.writeLock();
		try {
			cacheMap.clear();
		}
		finally {
			lock.unlockWrite(stamp);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return cacheMap.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<K, V> snapshot() {
		final long stamp = lock.writeLock();
		try {
			Map<K, V> map = new HashMap<>(cacheMap.size());
			cacheMap.forEach((key, cacheValue) -> map.put(key, cacheValue.getObject()));
			return map;
		}
		finally {
			lock.unlockWrite(stamp);
		}
	}

	// ---------------------------------------------------------------- protected

	/**
	 * Callback called on item removal. The cache is still locked.
	 */
	protected void onRemove(final K key, final V cachedObject) {
	}

}
