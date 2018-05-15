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

import jodd.cache.TypeCache;
import jodd.proxetta.ProxettaUtil;
import jodd.util.ClassUtil;
import jodd.util.StringPool;
import jodd.util.StringUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Super tool for getting calling path reference in compile-time.
 */
@SuppressWarnings({"UnusedDeclaration"})
public class Pathref<C> {

	public static final int ALL = -1;

	public static TypeCache<Class> cache = TypeCache.createDefault();

	private static final PathrefProxetta proxetta = new PathrefProxetta();

	private final C instance;

	/**
	 * Creates new proxified instance of target.
	 * Proxy instances are cached. If given target is also
	 * proxified, it's real target will be used.
	 */
	@SuppressWarnings({"unchecked"})
	public Pathref(final Class<C> target) {
		C proxy = createProxyObject(target);

		this.instance = proxy;

		injectPathRef(this, instance);

		this.path = StringPool.EMPTY;
	}

	private Pathref(final Class<C> target, final Pathref root) {
		C proxy = createProxyObject(target);

        this.instance = proxy;

		injectPathRef(root, instance);

		this.path = null;
	}

	/**
	 * Creates proxy object.
	 */
	protected C createProxyObject(Class<C> target) {
		target = ProxettaUtil.resolveTargetClass(target);

		Class proxyClass = cache.get(target);

		if (proxyClass == null) {
			proxyClass = proxetta.defineProxy(target);

			cache.put(target, proxyClass);
		}

		C proxy;

		try {
			proxy = (C) ClassUtil.newInstance(proxyClass);
		} catch (Exception ex) {
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
	public static <T> Pathref<T> on(final Class<T> target) {
		return new Pathref<>(target);
	}

	/**
	 * Static factory of next target. It handles special cases of maps, sets
	 * and lists. In case target can not be proxified (like for Java classes)
	 * it returns <code>null</code>.
	 */
	@SuppressWarnings("unchecked")
	public <T> T continueWith(final Object currentInstance, final String methodName, final Class<T> target) {
		Class currentClass = currentInstance.getClass();

		Method method;

		try {
			method = currentClass.getDeclaredMethod(methodName);
		}
		catch (NoSuchMethodException e) {
			throw new PathrefException("Not a getter: " + methodName, e);
		}

		if (!ClassUtil.isBeanPropertyGetter(method)) {
			throw new PathrefException("Not a getter: " + methodName);
		}

		String getterName = ClassUtil.getBeanPropertyGetterName(method);

		append(getterName);

		if (ClassUtil.isTypeOf(target, List.class)) {
			final Class componentType =
				ClassUtil.getComponentType(method.getGenericReturnType(), currentClass, 0);

			if (componentType == null) {
				throw new PathrefException("Unknown component name for: " + methodName);
			}

			return (T) new ArrayList() {
				@Override
				public Object get(final int index) {
					if (index >= 0) {
						append("[" + index + "]");
					}
					return new Pathref<>(componentType, Pathref.this).to();
				}
			};
		}

		try {
			return new Pathref<>(target, this).to();
		}
		catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Returns proxy instance of target class, so methods can be called
	 * immediately after (fluent interface).
	 */
	public C to() {
		path = StringPool.EMPTY;
		return instance;
	}

	// ---------------------------------------------------------------- ref

	public String path(final int dummy) {
		return path(null);
	}
	public String path(final short dummy) {
		return path(null);
	}
	public String path(final byte dummy) {
		return path(null);
	}
	public String path(final char dummy) {
		return path(null);
	}
	public String path(final long dummy) {
		return path(null);
	}
	public String path(final float dummy) {
		return path(null);
	}
	public String path(final double dummy) {
		return path(null);
	}
	public String path(final boolean dummy) {
		return path(null);
	}

	/**
	 * Returns the path.
	 */
	public String path(final Object object) {
		return path;
	}

	/**
	 * Returns the path.
	 */
	public String path() {
		return path;
	}

	protected void injectPathRef(final Pathref pathref, final Object instance) {
		try {
			Field f = instance.getClass().getDeclaredField("$__pathref$0");
			f.setAccessible(true);
			f.set(instance, pathref);
		} catch (Exception ex) {
			throw new PathrefException("Pathref field not found", ex);
		}
	}

}