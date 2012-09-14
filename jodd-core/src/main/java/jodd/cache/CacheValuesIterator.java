// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

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
