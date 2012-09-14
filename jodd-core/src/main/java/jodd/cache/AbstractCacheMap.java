// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.cache;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Default implementation of timed and size cache map.
 * Implementations should:
 * <ul>
 * <li>create a new cache map
 * <li>implements own <code>prune</code> strategy
 * </ul>
 * Uses <code>ReentrantReadWriteLock</code> to synchronize access.
 * Since upgrading from a read lock to the write lock is not possible,
 * be careful withing {@link #get(Object)} method.
 */
public abstract class AbstractCacheMap<K,V> implements Cache<K,V> {

	class CacheObject<K2,V2> {
		CacheObject(K2 key, V2 object, long ttl) {
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

	private final ReentrantReadWriteLock cacheLock = new ReentrantReadWriteLock();
	private final Lock readLock = cacheLock.readLock();
	private final Lock writeLock = cacheLock.writeLock();


	// ---------------------------------------------------------------- properties

	protected int cacheSize;      // max cache size, 0 = no limit

	/**
	 * {@inheritDoc}
	 */
	public int getCacheSize() {
		return cacheSize;
	}

	protected long timeout;     // default timeout, 0 = no timeout

	/**
	 * Returns default cache timeout or <code>0</code> if it is not set.
	 * Timeout can be set individually for each object.
	 */
	public long getCacheTimeout() {
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
	public void put(K key, V object) {
		put(key, object, timeout);
	}


	/**
	 * {@inheritDoc}
	 */
	public void put(K key, V object, long timeout) {
		writeLock.lock();

		try {
			CacheObject<K,V> co = new CacheObject<K,V>(key, object, timeout);
			if (timeout != 0) {
				existCustomTimeout = true;
			}
			if (isFull()) {
				pruneCache();
			}
			cacheMap.put(key, co);
		}
		finally {
			writeLock.unlock();
		}
	}


	// ---------------------------------------------------------------- get

	/**
	 * {@inheritDoc}
	 */
	public V get(K key) {
		readLock.lock();

		try {
			CacheObject<K,V> co = cacheMap.get(key);
			if (co == null) {
				return null;
			}
			if (co.isExpired() == true) {
				// remove(key);		// can't upgrade the lock
				cacheMap.remove(key);
				return null;
			}
			return co.getObject();
		}
		finally {
			readLock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Iterator<V> iterator() {
		return new CacheValuesIterator<V>(this);
	}

	// ---------------------------------------------------------------- prune

	/**
	 * Prune implementation.
	 */
	protected abstract int pruneCache();

	/**
	 * {@inheritDoc}
	 */
	public final int prune() {
		writeLock.lock();
		try {
			return pruneCache();
		}
		finally {
			writeLock.unlock();
		}
	}

	// ---------------------------------------------------------------- common

	/**
	 * {@inheritDoc}
	 */
	public boolean isFull() {
		if (cacheSize == 0) {
			return false;
		}
		return cacheMap.size() >= cacheSize;
	}

	/**
	 * {@inheritDoc}
	 */
	public void remove(K key) {
		writeLock.lock();
		try {
			cacheMap.remove(key);
		}
		finally {
			writeLock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void clear() {
		writeLock.lock();
		try {
			cacheMap.clear();
		}
		finally {
			writeLock.unlock();
		}
	}


	/**
	 * {@inheritDoc}
	 */
	public int size() {
		return cacheMap.size();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isEmpty() {
		return size() == 0;
	}
}
