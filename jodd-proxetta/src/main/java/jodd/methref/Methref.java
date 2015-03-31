// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.methref;

import jodd.proxetta.ProxettaUtil;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Super tool for getting method references in compile-time.
 */
@SuppressWarnings({"UnusedDeclaration"})
public class Methref<C> {

	private static final MethrefProxetta proxetta = new MethrefProxetta();
	private static final Map<Class, Object> cache = new WeakHashMap<Class, Object>();

	private final C instance;

	/**
	 * Creates new proxified instance of target.
	 * Proxy instances are cached. If given target is also
	 * proxified, it's real target will be used.
	 */
	@SuppressWarnings({"unchecked"})
	public Methref(Class<C> target) {
		target = ProxettaUtil.getTargetClass(target);

		Object proxy = cache.get(target);

		if (proxy == null) {
			Class<C> proxifiedTarget = proxetta.defineProxy(target);

			try {
				proxy = proxifiedTarget.newInstance();
				cache.put(target, proxy);
			} catch (Exception ex) {
				throw new MethrefException(ex);
			}
		}

        this.instance = (C) proxy;
	}

	// ---------------------------------------------------------------- use

	/**
	 * Static factory, for convenient use.
	 */
	public static <T> Methref<T> on(Class<T> target) {
		return new Methref<T>(target);
	}

	/**
	 * Static factory that immediately returns {@link #to() method picker}.
	 */
	public static <T> T onto(Class<T> target) {
		return new Methref<T>(target).to();
	}

	/**
	 * Returns proxy instance of target class, so methods can be called
	 * immediately after (fluent interface).
	 */
	public C to() {
		return instance;
	}

	// ---------------------------------------------------------------- ref

	public String ref(int dummy) {
		return ref(null);
	}
	public String ref(short dummy) {
		return ref(null);
	}
	public String ref(byte dummy) {
		return ref(null);
	}
	public String ref(char dummy) {
		return ref(null);
	}
	public String ref(long dummy) {
		return ref(null);
	}
	public String ref(float dummy) {
		return ref(null);
	}
	public String ref(double dummy) {
		return ref(null);
	}
	public String ref(boolean dummy) {
		return ref(null);
	}

	/**
	 * Resolves method name of method reference. Argument is used so {@link #to()}
	 * can be called in convenient way. For methods that returns string,
	 * value will be returned immediately.
	 */
	public String ref(Object dummy) {
		if (dummy != null) {
			if (dummy instanceof String) {
				return (String) dummy;
			}
			throw new MethrefException("Target method not collected");
		}
		return ref();
	}

	/**
	 * Returns name of method reference. Target {@link #on(Class) method} has
	 * to be {@link #to() called} before it can return its reference.
	 */
	public String ref() {
		if (instance == null) {
			return null;
		}
		try {
			Field f = instance.getClass().getDeclaredField("$__methodName$0");
			f.setAccessible(true);
			Object name = f.get(instance);
			if (name == null) {
				throw new MethrefException("Target method not collected");
			}
			return name.toString();
		} catch (Exception ex) {
			if (ex instanceof MethrefException) {
				throw ((MethrefException) ex);
			}
			throw new MethrefException("Methref field not found", ex);
		}
	}

}