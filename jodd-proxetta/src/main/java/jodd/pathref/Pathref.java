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

package jodd.pathref;

import jodd.proxetta.ProxettaUtil;
import jodd.util.ClassUtil;
import jodd.util.StringPool;
import jodd.util.StringUtil;
import jodd.util.TypeCache;

import java.lang.reflect.Field;
import java.util.function.Consumer;

/**
 * Super tool for getting calling path reference in compile-time.
 */
@SuppressWarnings({"UnusedDeclaration"})
public class Pathref<C> {

	public static final int ALL = -1;

	public static TypeCache<Class> cache = TypeCache.<Class>create().threadsafe(true).get();

	private static final PathrefProxetta proxetta = new PathrefProxetta();

	private final C instance;

	/**
	 * Creates new proxified instance of target.
	 * Proxy instances are cached. If given target is also
	 * proxified, it's real target will be used.
	 */
	@SuppressWarnings({"unchecked"})
	public Pathref(final Class<C> target) {
		final C proxy = createProxyObject(target);

		this.instance = proxy;

		injectPathRef(this, instance);

		this.path = StringPool.EMPTY;
	}

	Pathref(final Class<C> target, final Pathref root) {
		final C proxy = createProxyObject(target);

        this.instance = proxy;

		injectPathRef(root, instance);

		this.path = null;
	}

	/**
	 * Creates proxy object.
	 */
	protected C createProxyObject(Class<C> target) {
		target = ProxettaUtil.resolveTargetClass(target);

		final Class proxyClass = cache.get(target, proxetta::defineProxy);

		final C proxy;

		try {
			proxy = (C) ClassUtil.newInstance(proxyClass);
		} catch (final Exception ex) {
			throw new PathrefException(ex);
		}

		return proxy;
	}

	protected String path;

	// ---------------------------------------------------------------- use

	/**
	 * Appends method name to existing path.
	 */
	protected void append(final String methodName) {
		if (path.length() != 0) {
			path += StringPool.DOT;
		}
		if (methodName.startsWith(StringPool.LEFT_SQ_BRACKET)) {
			path = StringUtil.substring(path, 0, -1);
		}
		path += methodName;
	}

	/**
	 * Static factory, for convenient use.
	 */
	public static <T> Pathref<T> of(final Class<T> target) {
		return new Pathref<>(target);
	}

	/**
	 * Returns proxy instance of target class, so methods can be called
	 * immediately after (fluent interface).
	 */
	C get() {
		path = StringPool.EMPTY;
		return instance;
	}

	public String path(final Consumer<C> consumer) {
		path = StringPool.EMPTY;
		consumer.accept(proxy());
		return path();
	}

	protected static void injectPathRef(final Pathref pathref, final Object target) {
		try {
			final Field f = target.getClass().getDeclaredField("$__pathref$0");
			f.setAccessible(true);
			f.set(target, new PathrefContinue(pathref));
		} catch (final Exception ex) {
			throw new PathrefException("Pathref field not found", ex);
		}
	}

	// ----------------------------------------------------------------

	public C proxy() {
		return instance;
	}

	public String path() {
		final String collectedPath = path;
		this.path = StringPool.EMPTY;
		return collectedPath;
	}

}
