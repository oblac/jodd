// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.pathref;

import jodd.proxetta.ProxettaUtil;
import jodd.util.ReflectUtil;
import jodd.util.StringPool;
import jodd.util.StringUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Super tool for getting calling path reference in compile-time.
 */
@SuppressWarnings({"UnusedDeclaration"})
public class Pathref<C> {

	private static final PathrefProxetta proxetta = new PathrefProxetta();
	private static final Map<Class, Class> cache = new WeakHashMap<Class, Class>();

	private final C instance;

	/**
	 * Creates new proxified instance of target.
	 * Proxy instances are cached. If given target is also
	 * proxified, it's real target will be used.
	 */
	@SuppressWarnings({"unchecked"})
	public Pathref(Class<C> target) {
		C proxy = createProxyObject(target);

		this.instance = proxy;

		injectPathRef(this, instance);

		this.path = StringPool.EMPTY;
	}

	private Pathref(Class<C> target, Pathref root) {
		C proxy = createProxyObject(target);

        this.instance = proxy;

		injectPathRef(root, instance);

		this.path = null;
	}

	/**
	 * Creates proxy object.
	 */
	protected C createProxyObject(Class<C> target) {
		target = ProxettaUtil.getTargetClass(target);

		Class proxyClass = cache.get(target);

		if (proxyClass == null) {
			proxyClass = proxetta.defineProxy(target);

			cache.put(target, proxyClass);
		}

		C proxy;

		try {
			proxy = (C) proxyClass.newInstance();
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
	protected void append(String methodName) {
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
	public static <T> Pathref<T> on(Class<T> target) {
		return new Pathref<T>(target);
	}

	/**
	 * Static factory of next target. It handles special cases of maps, sets
	 * and lists. In case target can not be proxified (like for Java classes)
	 * it returns <code>null</code>.
	 */
	@SuppressWarnings("unchecked")
	public <T> T continueWith(Object currentInstance, String methodName, final Class<T> target) {
		Class currentClass = currentInstance.getClass();

		Method method;

		try {
			method = currentClass.getDeclaredMethod(methodName);
		}
		catch (NoSuchMethodException e) {
			throw new PathrefException("Not a getter: " + methodName, e);
		}

		if (!ReflectUtil.isBeanPropertyGetter(method)) {
			throw new PathrefException("Not a getter: " + methodName);
		}

		String getterName = ReflectUtil.getBeanPropertyGetterName(method);

		append(getterName);

		if (ReflectUtil.isTypeOf(target, List.class)) {
			final Class componentType =
				ReflectUtil.getComponentType(method.getGenericReturnType(), currentClass, 0);

			if (componentType == null) {
				throw new PathrefException("Unknown component name for: " + methodName);
			}

			return (T) new ArrayList() {
				@Override
				public Object get(int index) {
					append("[" + index + "]");
					return new Pathref<C>(componentType, Pathref.this).to();
				}
			};
		}

		try {
			return new Pathref<T>(target, this).to();
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

	public String path(int dummy) {
		return path(null);
	}
	public String path(short dummy) {
		return path(null);
	}
	public String path(byte dummy) {
		return path(null);
	}
	public String path(char dummy) {
		return path(null);
	}
	public String path(long dummy) {
		return path(null);
	}
	public String path(float dummy) {
		return path(null);
	}
	public String path(double dummy) {
		return path(null);
	}
	public String path(boolean dummy) {
		return path(null);
	}

	/**
	 * Returns the path.
	 */
	public String path(Object object) {
		return path;
	}

	/**
	 * Returns the path.
	 */
	public String path() {
		return path;
	}

	protected void injectPathRef(Pathref pathref, Object instance) {
		try {
			Field f = instance.getClass().getDeclaredField("$__pathref$0");
			f.setAccessible(true);
			f.set(instance, pathref);
		} catch (Exception ex) {
			throw new PathrefException("Pathref field not found", ex);
		}
	}

}