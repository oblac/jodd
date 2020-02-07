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
import java.util.function.Consumer;

/**
 * Super tool for getting method references (names) in compile-time.
 */
@SuppressWarnings({"UnusedDeclaration"})
public class Methref<C> {

	public static TypeCache<Class> cache = TypeCache.<Class>create().threadsafe(true).get();

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

		final Class proxyClass = cache.get(target, proxetta::defineProxy);

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
	 * Returns name of called method.
	 */
	public String name(final Consumer<C> consumer) {
		consumer.accept(instance);
		return ref();
	}

	private String ref() {
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

}
