// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.cache;

import java.util.Map;

/**
 * Default implementation of timed and size cache map.
 * Implementations should:
 * <ul>
 * <li>create a new cache map
 * <li>implements own <code>prune</code> strategy
 * </ul> 
 */
public abstract class AbstractCacheMap<K,V> implements Cache<K,V> {

	class CacheObject {
		CacheObject(K key, V object, long ttl) {
			this.key = key;
			this.cachedObject = object;
			this.ttl = ttl;
			this.lastAccess = System.currentTimeMillis();
		}

		final K key;
		final V cachedObject;
		long lastAccess;        // time of last access
		int accessCount;        // number of accesses
		long ttl; 				// objects timeout (time-to-live), 0 = no timeout

		boolean isExpired() {
			if (ttl == 0) {
				return false;
			}
			return lastAccess + ttl < System.currentTimeMillis();
		}
		V getObject() {
			lastAccess = System.currentTimeMillis();
			accessCount++;
			return cachedObject;
		}
    }

	protected Map<K, CacheObject> cacheMap;

	// ---------------------------------------------------------------- properties

	protected int cacheSize;      // max cache size, 0 = no limit

	/**
	 * Returns cache size or <code>0</code> if there is no size limit.
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
	 * Adds an object to the cache with default timeout.
	 * @see AbstractCacheMap#put(Object, Object, long)
	 */
	public void put(K key, V object) {
		put(key, object, timeout);
	}


	/**
	 * Adds an object to the cache with specified timeout after which it becomes expired.
	 * If cache is full, prune is invoked to make room for new object.
	 */
	public void put(K key, V object, long timeout) {
		CacheObject co = new CacheObject(key, object, timeout);
		if (timeout != 0) {
			existCustomTimeout = true;
		}
		if (isFull()) {
			prune();
		}
		cacheMap.put(key, co);
	}


	// ---------------------------------------------------------------- get

	/**
	 * Retrieves an object from the cache. Returns <code>null</code> if object
	 * is not longer in cache or if it is expired.
	 */
	public V get(K key) {
		CacheObject co = cacheMap.get(key);
		if (co == null) {
			return null;
		}
		if (co.isExpired() == true) {
			remove(key);
			return null;
		}
		return co.getObject();
	}

	// ---------------------------------------------------------------- prune

	/**
	 * Prunes objects from cache and returns the number of removed objects.
	 * Which strategy is used depends on cache implementation. 
	 */
	public abstract int prune();


	// ---------------------------------------------------------------- common

	/**
	 * Returns <code>true</code> if max cache capacity has been reached
	 * only if cache is size limited.
	 */
	public boolean isFull() {
		if (cacheSize == 0) {
			return false;
		}
		return cacheMap.size() >= cacheSize;
	}

	/**
	 * Removes an object from the cache.
	 */
	public void remove(K key) {
		cacheMap.remove(key);
	}

	/**
	 * Clears current cache.
	 */
	public void clear() {
		cacheMap.clear();
	}


	/**
	 * Returns current cache size.
	 */
	public int size() {
		return cacheMap.size();
	}	
}
