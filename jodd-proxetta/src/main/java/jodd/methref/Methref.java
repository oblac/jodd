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

import jodd.proxetta.ProxettaUtil;
import jodd.util.ClassUtil;
import jodd.util.TypeCache;

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
	 * Last called method name. Don't use this field directly.
	 */
	private String lastName;


	/**
	 * Creates new proxified instance of target.
	 * Proxy classes are cached. If given target is also
	 * proxified, it's real target will be used.
	 */
	@SuppressWarnings({"unchecked"})
	public Methref(Class<C> target) {
		target = ProxettaUtil.resolveTargetClass(target);

		if (target.isInterface()) {
			this.instance = InterfaceImplementation.of(target).createInstanceFor(this);
			return;
		}

		final Class proxyClass = cache.get(target, proxetta::defineProxy);

		final C proxy;

		try {
			proxy = (C) ClassUtil.newInstance(proxyClass);
			injectMethref(proxy);
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
		consumer.accept(proxy());
		return lastName();
	}


	// ---------------------------------------------------------------- proxy method

	private boolean injectedMethref = false;

	/**
	 * Returns proxy instance that is ready to collect the method name of invoked methods.
	 */
	public C proxy() {
		if (!injectedMethref) {

			injectedMethref = true;
		}
		return instance;
	}

	/**
	 * Returns {@code true} if given object is proxified by this Methref.
	 */
	public boolean isMyProxy(final Object instance) {
		final Methref usedMethref = readMethref(instance);
		return this == usedMethref;
	}

	/**
	 * Returns method name of last invoked method on a proxy.
	 */
	public static String lastName(final Object instance) {
		final Methref m = readMethref(instance);
		if (m == null) {
			return null;
		}
		return m.lastName;
	}

	/**
	 * Returns name of last method invoked on proxy.
	 */
	public <T> String lastName() {
		return lastName;
	}
	
	public void lastName(final String name) {
		this.lastName = name;
	}


	// ---------------------------------------------------------------- detect

	private void injectMethref(final C instance) {
		try {
			final Field f = instance.getClass().getDeclaredField("$__methref$0");
			f.setAccessible(true);
			f.set(instance, this);
		} catch (final Exception ex) {
			if (ex instanceof MethrefException) {
				throw ((MethrefException) ex);
			}
			throw new MethrefException("Methref field not found", ex);
		}
	}

	private static Methref readMethref(final Object instance) {
		try {
			final Field f = instance.getClass().getDeclaredField("$__methref$0");
			f.setAccessible(true);
			return (Methref) f.get(instance);
		} catch (final Exception ex) {
			if (ex instanceof MethrefException) {
				throw ((MethrefException) ex);
			}
			return null;
		}
	}
}
