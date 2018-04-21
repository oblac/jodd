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

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Types cache. Provides several implementations depending on what you need to be addressed.
 * There are two things you should take care off:
 * <ul>
 *     <li>synchronization - especially on storing items. If not synchronized, one instance of an item may be put
 *     more then once into the map. This is usually fine, as it happens only during the initialization and makes not
 *     harm if something is created twice</li>
 *     <li>weak - if your key classes are replaced during the runtime, you should use weak map, in order to automatically
 *     remove obsolete keys.</li>
 * </ul>
 * This cache is specific as there is never a {@code put()} method. Putting is done only via {@link #get(Class, Supplier)}
 * by providing a supplier. This way we ensure synchronization if enabled.
 */
public class TypeCache<T> {

	public static <A> TypeCache<A> create(final Implementation implementation) {
		return new TypeCache<>(implementation);
	}

	public enum Implementation {
		/**
		 * Nothing is actually cached.
		 */
		NO_CACHE,
		/**
		 * Just a simple map: not synchronized and not weak.
		 */
		MAP(false, false),
		/**
		 * Synchronized map, but not weak.
		 */
		SYNC_MAP(false, true),
		/**
		 * Weak map, but not synchronized.
		 */
		WEAK(true, false),
		/**
		 * Synchronized and weak map.
		 */
		SYNC_WEAK(true, true);

		private final boolean sync;
		private final boolean weak;
		private final boolean none;

		Implementation() {
			sync = false;
			weak = false;
			none = true;
		}

		Implementation(final boolean weak, final boolean sync) {
			this.none = false;
			this.weak = weak;
			this.sync = sync;
		}

		/**
		 * Creates a map based on cache type.
		 */
		public <A> Map<Class<?>, A> createMap() {
			if (none) {
				return new AbstractMap<Class<?>, A>() {
					@Override
					public A put(Class<?> key, A value) {
						return null;
					}

					@Override
					public A get(Object key) {
						return null;
					}

					@Override
					public Set<Entry<Class<?>, A>> entrySet() {
						return Collections.EMPTY_SET;
					}
				};
			}
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
	 * Returns existing value or add default supplied one.
	 */
	public T get(final Class<?> key, final Supplier<T> valueSupplier) {
		return map.computeIfAbsent(key, aClass -> valueSupplier.get());
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
