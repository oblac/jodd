// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.methref;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.WeakHashMap;

@SuppressWarnings({"UnusedDeclaration"})
public class Methref<C> {

	protected static final MethrefProxetta proxetta = new MethrefProxetta();
	private static final Map<Class, Class> cache = new WeakHashMap<Class, Class>();

	private final C instance;

	/**
	 * Creates new proxified instance of target. Proxies are cached.
	 */
	@SuppressWarnings({"unchecked"})
	public Methref(Class<C> target) {
		Class<C> proxifiedTarget = cache.get(target);
		if (proxifiedTarget == null) {
			proxifiedTarget = proxetta.defineProxy(target);
			cache.put(target, proxifiedTarget);
		}
		// create new instance
		try {
			instance = proxifiedTarget.newInstance();
		} catch (InstantiationException iex) {
			throw new MethrefException(iex);
		} catch (IllegalAccessException iaex) {
			throw new MethrefException(iaex);
		}
	}


	// ---------------------------------------------------------------- use

	/**
	 * Shorten version for methods that returns strings.
	 */
	public static <T> T sref(Class<T> target) {
		return on(target).instance;
	}

	/**
	 * Static factory, for more convenient use.
	 */
	public static <T> Methref<T> on(Class<T> target) {
		return new Methref<T>(target);
	}

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
	 * Resolves method name of method reference. Argument is used so {@link #method()}
	 * can be called in convenient way. For methods that returns string,
	 * value will be returned immediately.
	 */
	public String ref(Object dummy) {
		if (dummy != null) {
			if (dummy instanceof String) {
				return (String) dummy;
			}
			throw new MethrefException("It seems that method reference was not collected with 'method()'.");
		}
		return ref();
	}

	/**
	 * Returns name of method reference. Target {@link #on(Class) method}  has to be {@link #method() called} before.
	 */
	public String ref() {
		if (instance == null) {
			return null;
		}
		try {
			Field f = instance.getClass().getDeclaredField("$__methodName$0");
			f.setAccessible(true);
			return f.get(instance).toString();
		} catch (NoSuchFieldException nsfex) {
			throw new MethrefException("Unable to find injected field.", nsfex);
		} catch (IllegalAccessException iaex) {
			throw new MethrefException("Unable to find injected field.", iaex);
		}
	}

	/**
	 * Returns proxified instance so method can be called immediately after (float interface).
	 */
	public C method() {
		return instance;
	}

}
