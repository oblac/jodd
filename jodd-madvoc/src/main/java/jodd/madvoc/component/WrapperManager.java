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

package jodd.madvoc.component;

import jodd.madvoc.ActionWrapper;
import jodd.madvoc.BaseActionWrapperStack;
import jodd.madvoc.MadvocException;
import jodd.petite.meta.PetiteInject;
import jodd.util.ClassUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Base wrapper manager implements common logic of a wrapper.
 */
public abstract class WrapperManager<T extends ActionWrapper> {

	@PetiteInject
	protected ContextInjectorComponent contextInjectorComponent;

	protected WrapperManager() {
		wrappers = new HashMap<>();
	}

	// ---------------------------------------------------------------- container

	protected final Map<String, T> wrappers;

	/**
	 * Returns all action wrappers. Returns a copy in new set.
	 */
	protected Set<T> getAll() {
		final Set<T> set = new HashSet<>(wrappers.size());
		set.addAll(wrappers.values());
		return set;
	}


	/**
	 * Looks up for existing wrapper. Returns <code>null</code> if wrapper is not already registered.
	 */
	public T lookup(final String name) {
		return wrappers.get(name);
	}

	/**
	 * Resolves single wrapper. Creates new wrapper instance if not already registered.
	 * Does not expand the wrappers.
	 */
	public T resolve(final Class<? extends T> wrapperClass) {
		String wrapperClassName = wrapperClass.getName();

		T wrapper = lookup(wrapperClassName);

		if (wrapper == null) {
			wrapper = createWrapper(wrapperClass);

			initializeWrapper(wrapper);

			wrappers.put(wrapperClassName, wrapper);
		}
		return wrapper;
	}

	/**
	 * Resolves wrappers. Unregistered wrappers will be registered. Returned array may be
	 * different size than size of provided array, due to {@link #expand(Class[]) expanding}.
	 */
	public T[] resolveAll(Class<? extends T>[] wrapperClasses) {
		if (wrapperClasses == null) {
			return null;
		}
		wrapperClasses = expand(wrapperClasses);
		T[] result = createArray(wrapperClasses.length);

		for (int i = 0; i < wrapperClasses.length; i++) {
			result[i] = resolve(wrapperClasses[i]);
		}
		return result;
	}

	/**
	 * Creates an array of wrapper instances.
	 */
	protected abstract T[] createArray(int len);

	// ---------------------------------------------------------------- init

	/**
	 * Initializes action wrapper.
	 */
	protected void initializeWrapper(final T wrapper) {
		contextInjectorComponent.injectContext(wrapper);

		wrapper.init();
	}

	// ---------------------------------------------------------------- expander

	/**
	 * Replaces all {@link BaseActionWrapperStack} with stack values.
	 */
  protected Class<? extends T>[] expand(final Class<? extends T>[] actionWrappers) {
		if (actionWrappers == null) {
			return null;
		}
		List<Class<? extends T>> list = new ArrayList<>(actionWrappers.length);
		list.addAll(Arrays.asList(actionWrappers));

		int i = 0;
		while (i < list.size()) {
			Class<? extends T> wrapperClass = list.get(i);
			if (wrapperClass == null) {
				continue;
			}
			if (ClassUtil.isTypeOf(wrapperClass, BaseActionWrapperStack.class)) {
				BaseActionWrapperStack stack = (BaseActionWrapperStack) resolve(wrapperClass);
				list.remove(i);
				Class<? extends T>[] stackWrappers = stack.getWrappers();
				if (stackWrappers != null) {
					list.addAll(i, Arrays.asList(stackWrappers));
				}
				i--;
				//continue;
			}
			i++;
		}
		return list.toArray(new Class[0]);
	}

	// ---------------------------------------------------------------- create

	/**
	 * Creates new wrapper.
	 */
	protected <R extends T> R createWrapper(final Class<R> wrapperClass) {
		try {
		    return ClassUtil.newInstance(wrapperClass);
		} catch (Exception ex) {
			throw new MadvocException("Invalid Madvoc wrapper: " + wrapperClass, ex);
		}
	}

}