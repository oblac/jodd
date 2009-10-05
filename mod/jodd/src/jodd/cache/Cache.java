// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.cache;

/**
 * Cache interface.
 */
public interface Cache<K, V> {

	/**
	 * Returns cache size or <code>0</code> if there is no size limit.
	 */
	int getCacheSize();

	/**
	 * Returns default timeout or <code>0</code> if it is not set.
	 */
	long getCacheTimeout();

	/**
	 * Adds an object to the cache with default timeout.
	 * @see Cache#put(Object, Object, long)
	 */
	void put(K key, V object);

	/**
	 * Adds an object to the cache with specified timeout after which it becomes expired.
	 * If cache is full, {@link #prune()} is invoked to make room for new object.
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
	 * Removes an object from the cache.
	 */
	void remove(K key);

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
}
