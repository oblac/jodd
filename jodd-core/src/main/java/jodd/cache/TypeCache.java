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
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
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
 */
public class TypeCache<T> {

	public static class Defaults {
		/**
		 * Default {@link TypeCache} implementation.
		 */
		public static Supplier<TypeCache> implementation = () -> TypeCache.create().get();
	}

	// ---------------------------------------------------------------- builder

	/**
	 * Creates a type cache by using a builder.
	 */
	public static <A> Builder<A> create() {
		return new Builder<>();
	}

	/**
	 * Creates default implementation of the type cache.
	 */
	@SuppressWarnings("unchecked")
	public static <A> TypeCache<A> createDefault() {
		return (TypeCache<A>)Defaults.implementation.get();
	}

	public static class Builder<A> {
		private boolean threadsafe;
		private boolean weak;
		private boolean none;

		/**
		 * No cache will be used.
		 * Setting other properties will not have any affect.
		 */
		public Builder<A> noCache() {
			none = true;
			return this;
		}

		/**
		 * Cache keys will be weak.
		 */
		public Builder<A> weak(final boolean weak) {
			this.weak = weak;
			return this;
		}
		/**
		 * Cache will be thread-safe.
		 */
		public Builder<A> threadsafe(final boolean threadsafe) {
			this.threadsafe = threadsafe;
			return this;
		}

		/**
		 * Builds a type cache.
		 */
		public TypeCache<A> get() {
			final Map<Class<?>, A> map;
			if (none) {
				map = new AbstractMap<Class<?>, A>() {
					@Override
					public A put(final Class<?> key, final A value) {
						return null;
					}

					@Override
					public A get(final Object key) {
						return null;
					}

					@Override
					public Set<Entry<Class<?>, A>> entrySet() {
						return Collections.emptySet();
					}
				};
			}
			else if (weak) {
				if (threadsafe) {
					map = Collections.synchronizedMap(new WeakHashMap<>());
				} else {
					map = new WeakHashMap<>();
				}
			} else {
				if (threadsafe) {
					map = new ConcurrentHashMap<>();
				} else {
					map = new IdentityHashMap<>();
				}
			}

			return new TypeCache<>(map);
		}
	}

	// ---------------------------------------------------------------- map

	private final Map<Class<?>, T> map;

	private TypeCache(final Map<Class<?>, T> backedMap) {
		this.map = backedMap;
	}

	/**
	 * Add values to the map.
	 */
	public T put(final Class<?> type, final T value) {
		return map.put(type, value);
	}

	/**
	 * Returns value from the map or {@code null} if value does not exist.
	 */
	public T get(final Class<?> key) {
		return map.get(key);
	}

	/**
	 * Returns existing value or add default supplied one.
	 * Use this method instead of {@code get-nullcheck-put} block when
	 * thread-safety is of importance.
	 */
	public T get(final Class<?> key, final Supplier<T> valueSupplier) {
		return map.computeIfAbsent(key, aClass -> valueSupplier.get());
	}

	/**
	 * Removes element from the type cache.
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
	 * Iterates all cached values.
	 */
	public void forEachValue(final Consumer<? super T> valueConsumer) {
		map.values().forEach(valueConsumer);
	}

}
