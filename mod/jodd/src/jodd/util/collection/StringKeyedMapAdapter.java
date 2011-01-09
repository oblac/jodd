// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.collection;

import java.util.AbstractMap;
import java.util.Set;
import java.util.Iterator;
import java.util.HashSet;

/**
 * Adapter for map whose keys are <code>String</code> values.
 */
public abstract class StringKeyedMapAdapter extends AbstractMap<String, Object> {

	// ---------------------------------------------------------------- hooks

	/**
	 * Hooks method that needs to be implemented by concrete subclasses.
	 * Gets a value associated with a key.
	 */
	protected abstract Object getAttribute(String key);

	/**
	 * Hooks method that needs to be implemented by concrete subclasses.
	 * Puts a key-value pair in the map, overwriting
	 * any possible earlier value associated with the same key.
	 */
	protected abstract void setAttribute(String key, Object value);

	/**
	 * Hooks method that needs to be implemented by concrete subclasses.
	 * Removes a key and its associated value from the
	 * map.
	 */
	protected abstract void removeAttribute(String key);

	/**
	 * Hook method that needs to be implemented by concrete subclasses.
	 * Returns an enumeration listing all keys known to the map.
	 */
	protected abstract Iterator<String> getAttributeNames();

	// ---------------------------------------------------------------- implementation

	private Set<Entry<String, Object>> entries;     // cached entries

	/**
	 * Removes all attributes from the request as well as clears entries in this map.
	 */
	@Override
	public void clear() {
		entries = null;
		Iterator<String> keys = getAttributeNames();
		while (keys.hasNext()) {
			removeAttribute(keys.next());
		}
	}

	/**
	 * Returns a Set of attributes from the http request.
	 */
	@Override
	public Set<Entry<String, Object>> entrySet() {
		if (entries == null) {
			entries = new HashSet<Entry<String, Object>>();
			Iterator<String> iterator = getAttributeNames();
			while (iterator.hasNext()) {
				final String key = iterator.next();
				final Object value = getAttribute(key);
				entries.add(new Entry<String, Object>() {
					@Override
					public boolean equals(Object obj) {
						Entry entry = (Entry) obj;
						return ((key == null) ? (entry.getKey() == null) : key.equals(entry.getKey())) && ((value == null) ? (entry.getValue() == null) : value.equals(entry.getValue()));
					}

					@Override
					public int hashCode() {
						return ((key == null) ? 0 : key.hashCode()) ^ ((value == null) ? 0 : value.hashCode());
					}

					public String getKey() {
						return key;
					}

					public Object getValue() {
						return value;
					}

					public Object setValue(Object obj) {
						setAttribute(key, obj);
						return value;
					}
				});
			}
		}

		return entries;
	}

	/**
	 * Returns the request attribute associated with the given key or <code>null</code> if it doesn't exist.
	 */
	@Override
	public Object get(Object key) {
		return getAttribute(key.toString());
	}

	/**
	 * Saves an attribute in the request.
	 */
	@Override
	public Object put(String key, Object value) {
		entries = null;
		Object previous = get(key);
		setAttribute(key, value);
		return previous;
	}

	/**
	 * Removes the specified request attribute.
	 */
	@Override
	public Object remove(Object key) {
		entries = null;
		Object value = get(key);
		removeAttribute(key.toString());
		return value;
	}
}