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

package jodd.util.collection;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
			entries = new HashSet<>();
			Iterator<String> iterator = getAttributeNames();
			while (iterator.hasNext()) {
				final String key = iterator.next();
				final Object value = getAttribute(key);
				entries.add(new Entry<String, Object>() {
					@Override
					public boolean equals(final Object obj) {
						if (obj == null) {
							return false;
						}

						if (this.getClass() != obj.getClass()) {
							return false;
						}

						Entry entry = (Entry) obj;
						return ((key == null) ? (entry.getKey() == null) : key.equals(entry.getKey())) && ((value == null) ? (entry.getValue() == null) : value.equals(entry.getValue()));
					}

					@Override
					public int hashCode() {
						return ((key == null) ? 0 : key.hashCode()) ^ ((value == null) ? 0 : value.hashCode());
					}

					@Override
					public String getKey() {
						return key;
					}

					@Override
					public Object getValue() {
						return value;
					}

					@Override
					public Object setValue(final Object obj) {
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
	public Object get(final Object key) {
		return getAttribute(key.toString());
	}

	/**
	 * Saves an attribute in the request.
	 */
	@Override
	public Object put(final String key, final Object value) {
		entries = null;
		Object previous = get(key);
		setAttribute(key, value);
		return previous;
	}

	/**
	 * Removes the specified request attribute.
	 */
	@Override
	public Object remove(final Object key) {
		entries = null;
		Object value = get(key);
		removeAttribute(key.toString());
		return value;
	}
}