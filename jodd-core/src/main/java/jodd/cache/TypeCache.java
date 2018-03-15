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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Type cache.
 */
public class TypeCache<T> {

	public static <A> TypeCache<A> create(final Implementation implementation) {
		return new TypeCache<>(implementation);
	}

	public enum Implementation {
		/**
		 * Just a simple map.
		 */
		MAP(false, false),
		/**
		 * Synchronized map.
		 */
		SYNC_MAP(false, true),
		/**
		 * Weak map.
		 */
		WEAK(true, false),
		/**
		 * Synchronized and weak map.
		 */
		SYNC_WEAK(true, true);

		private final boolean sync;
		private final boolean weak;

		Implementation(final boolean weak, final boolean sync) {
			this.weak = weak;
			this.sync = sync;
		}

		/**
		 * Creates a map based on type.
		 */
		public <A> Map<Class<?>, A> createMap() {
			if (weak) {
				if (sync) {
					return Collections.synchronizedMap(new WeakHashMap<>());
				} else {
					return new WeakHashMap<>();
				}
			} else {
				if (sync) {
					return new ConcurrentHashMap<>();
				} else {
					return new HashMap<>();
				}
			}
		}
	}

	private final Map<Class<?>, T> map;

	public TypeCache(final Implementation typeCacheImplementation) {
		this.map = typeCacheImplementation.createMap();
	}

	/**
	 * Returns value from the map or {@code null} if value does not exist.
	 */
	public T get(final Class<?> key) {
		return map.get(key);
	}

	/**
	 * Returns existing value of default one if key is not registered.
	 */
	public T getOrDefault(final Class<?> key, final T defaultValue) {
		return map.getOrDefault(key, defaultValue);
	}

	/**
	 * Cache some value for given class.
	 */
	public T put(final Class<?> key, final T value) {
		return map.put(key, value);
	}

	/**
	 * Removes element from type cache.
	 */
	public T remove(final Class<?> type) {
		return map.remove(type);
	}

	/**
	 * Clears complete cache.
	 */
	public void clear() {
		map.clear();
	}

	/**
	 * Returns cache size.
	 */
	public int size() {
		return map.size();
	}

	/**
	 * Returns {@code true} if cache is empty.
	 */
	public boolean isEmpty() {
		return map.isEmpty();
	}

	/**
	 * Returns collection of map values.
	 */
	public Collection<T> values() {
		return map.values();
	}

	/**
	 * Returns {@code true} if the key is contained in the cache.
	 */
	public boolean containsKey(final Class type) {
		return map.containsKey(type);
	}

}
