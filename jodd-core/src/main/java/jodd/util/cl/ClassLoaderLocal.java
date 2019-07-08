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

package jodd.util.cl;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * This class provides classloader-local variables.
 * <p>
 * It is designed to look very much like <code>ThreadLocal</code>.
 */
public class ClassLoaderLocal<T> {

	private final Map<ClassLoader, T> weakMap = new WeakHashMap<>();
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

		if (!initialized) {
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
	public synchronized void set(final T value) {

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