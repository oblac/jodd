// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * This class provides classloader-local variables.
 * <p>
 * It is designed to look very much like <code>ThreadLocal</code>.
 */
public class ClassLoaderLocal<T> {

	private final Map<ClassLoader, T> weakMap = new WeakHashMap<ClassLoader, T>();
	private T value;
	private boolean initialized;

	/**
	 * Returns the current classloader "initial value" for this classloader-local variable.
	 * This method will be invoked the first time it is accessed the get() method, unless
	 * the thread previously invoked the set(T) method, in which case the initialValue method
	 * will not be invoked for the classloader. Normally, this method is invoked at most once
	 * per classloader, but it may be invoked again in case of subsequent invocations of remove()
	 * followed by get().
	 * <p>
	 * This implementation simply returns null; if the programmer desires classloader-local variables to
	 * have an initial value other than null, ClassLoaderLocal must be subclassed, and this method overridden.
	 * Typically, an anonymous inner class will be used.
	 */
	protected T initialValue() {
		return null;
	}

	/**
	 * Returns the value in the current classloader copy of this variable.
	 * If the variable has no value for the current classloader, it is first initialized to the value returned
	 * by an invocation of the initialValue() method.
	 */
	public synchronized T get() {
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

		if (contextClassLoader != null) {
			T value = weakMap.get(contextClassLoader);

			if ((value == null) && !weakMap.containsKey(contextClassLoader)) {
				value = initialValue();
				weakMap.put(contextClassLoader, value);
			}

			return value;
		}

		if (initialized == false) {
			value = initialValue();
			initialized = true;
		}

		return value;
	}

	/**
	 * Sets the current classloaders's copy of this variable to the specified value.
	 * Most subclasses will have no need to override this method, relying solely on the initialValue()
	 * method to set the values of classloader-locals.
	 */
	public synchronized void set(T value) {

		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

		if (contextClassLoader != null) {
			weakMap.put(contextClassLoader, value);
			return;
		}

		this.value = value;
		this.initialized = true;
	}

	/**
	 * Removes the current classloader's value for this variable.
	 */
	public synchronized void remove() {
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		weakMap.remove(contextClassLoader);
	}

}