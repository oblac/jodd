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

package jodd.methref;

import jodd.cache.TypeCache;
import jodd.proxetta.ProxettaUtil;
import jodd.util.ClassUtil;

import java.lang.reflect.Field;

/**
 * Super tool for getting method references (names) in compile-time.
 */
@SuppressWarnings({"UnusedDeclaration"})
public class Methref<C> {

	public static TypeCache<Class> cache = TypeCache.createDefault();

	private static final MethrefProxetta proxetta = new MethrefProxetta();

	private final C instance;

	/**
	 * Creates new proxified instance of target.
	 * Proxy classes are cached. If given target is also
	 * proxified, it's real target will be used.
	 */
	@SuppressWarnings({"unchecked"})
	public Methref(Class<C> target) {
		target = ProxettaUtil.resolveTargetClass(target);

		Class proxyClass = cache.get(target);

		if (proxyClass == null) {
			proxyClass = proxetta.defineProxy(target);

			cache.put(target, proxyClass);
		}

		final C proxy;

		try {
			proxy = (C) ClassUtil.newInstance(proxyClass);
		} catch (final Exception ex) {
			throw new MethrefException(ex);
		}

        this.instance = proxy;
	}

	// ---------------------------------------------------------------- use

	/**
	 * Static factory, for convenient use.
	 */
	public static <T> Methref<T> of(final Class<T> target) {
		return new Methref<>(target);
	}

	/**
	 * Static factory that immediately returns {@link #get() method picker}.
	 */
	public static <T> T get(final Class<T> target) {
		return new Methref<>(target).get();
	}

	/**
	 * Returns proxy instance of target class, so methods can be called
	 * immediately after (fluent interface).
	 */
	public C get() {
		return instance;
	}

	// ---------------------------------------------------------------- ref

	public String ref(final int dummy) {
		return ref(null);
	}
	public String ref(final short dummy) {
		return ref(null);
	}
	public String ref(final byte dummy) {
		return ref(null);
	}
	public String ref(final char dummy) {
		return ref(null);
	}
	public String ref(final long dummy) {
		return ref(null);
	}
	public String ref(final float dummy) {
		return ref(null);
	}
	public String ref(final double dummy) {
		return ref(null);
	}
	public String ref(final boolean dummy) {
		return ref(null);
	}

	/**
	 * Resolves method name of method reference. Argument is used so {@link #get()}
	 * can be called in convenient way. For methods that returns string,
	 * value will be returned immediately.
	 */
	public String ref(final Object dummy) {
		if (dummy != null) {
			if (dummy instanceof String) {
				return (String) dummy;
			}
			throw new MethrefException("Target method not collected");
		}
		return ref();
	}

	/**
	 * Returns name of method reference. Target {@link #of(Class) method} has
	 * to be {@link #get() called} before it can return its reference.
	 */
	public String ref() {
		if (instance == null) {
			return null;
		}
		try {
			final Field f = instance.getClass().getDeclaredField("$__methodName$0");
			f.setAccessible(true);
			final Object name = f.get(instance);
			if (name == null) {
				throw new MethrefException("Target method not collected");
			}
			return name.toString();
		} catch (final Exception ex) {
			if (ex instanceof MethrefException) {
				throw ((MethrefException) ex);
			}
			throw new MethrefException("Methref field not found", ex);
		}
	}

	/**
	 * Returns current count.
	 */
	public Integer count() {
		if (instance == null) {
			return null;
		}
		try {
			final Field f = instance.getClass().getDeclaredField("$__methodCount$0");
			f.setAccessible(true);
			final Integer count = (Integer) f.get(instance);
			if (count == null) {
				throw new MethrefException("Target method not collected");
			}
			return count;
		} catch (final Exception ex) {
			if (ex instanceof MethrefException) {
				throw ((MethrefException) ex);
			}
			throw new MethrefException("Methref field not found", ex);
		}
	}

}
