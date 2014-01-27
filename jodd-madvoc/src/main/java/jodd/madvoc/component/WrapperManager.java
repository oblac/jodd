// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.madvoc.ActionWrapper;
import jodd.madvoc.BaseActionWrapperStack;
import jodd.madvoc.MadvocException;
import jodd.petite.meta.PetiteInject;
import jodd.util.ReflectUtil;

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
	protected MadvocController madvocController;

	@PetiteInject
	protected MadvocConfig madvocConfig;

	@PetiteInject
	protected ServletContextInjector servletContextInjector;

	@PetiteInject
 	protected MadvocContextInjector madvocContextInjector;

	protected WrapperManager() {
		wrappers = new HashMap<String, T>();
	}

	// ---------------------------------------------------------------- container

	protected Map<String, T> wrappers;

	/**
	 * Returns all action wrappers.
	 */
	protected Set<T> getAll() {
		Set<T> set = new HashSet<T>(wrappers.size());
		set.addAll(wrappers.values());
		return set;
	}

	/**
	 * Registers wrapper instance for given name. Wrapper
	 * instance gets injected with {@link MadvocContextInjector}.
	 */
	public <R extends T> void register(String name, R actionWrapper) {
		madvocContextInjector.injectMadvocContext(actionWrapper);
		madvocContextInjector.injectMadvocParams(actionWrapper);

		wrappers.put(name, actionWrapper);
	}

	/**
	 * Looks up for existing wrapper. Returns <code>null</code> if wrapper is not already registered.
	 */
	public T lookup(String name) {
		return wrappers.get(name);
	}

	/**
	 * Resolves single wrapper. Creates new wrapper instance if not already registered.
	 * Does not expand the wrappers.
	 */
	public T resolve(Class<? extends T> wrapperClass) {
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
	protected void initializeWrapper(T wrapper) {
		madvocContextInjector.injectMadvocContext(wrapper);
		madvocContextInjector.injectMadvocParams(wrapper);
		servletContextInjector.injectContext(wrapper, madvocController.getApplicationContext());

		wrapper.init();
	}

	// ---------------------------------------------------------------- expander

	/**
	 * Returns default wrappers from the configuration.
	 */
	protected abstract Class<? extends T>[] getDefaultWrappers();

	/**
	 * Returns marker wrapper class, shortcut for default web app wrappers.
	 */
	protected abstract Class<? extends T> getDefaultWebAppWrapper();

	/**
	 * Replaces all {@link #getDefaultWebAppWrapper()} with {@link #getDefaultWebAppWrapper()}
	 * and {@link BaseActionWrapperStack} with stack values.
	 */
	protected Class<? extends T>[] expand(Class<? extends T>[] actionWrappers) {
		if (actionWrappers == null) {
			return null;
		}
		List<Class<? extends T>> list = new ArrayList<Class<? extends T>>(actionWrappers.length);
		list.addAll(Arrays.asList(actionWrappers));

		int i = 0;
		while (i < list.size()) {
			Class<? extends T> wrapperClass = list.get(i);
			if (wrapperClass == null) {
				continue;
			}
			if (wrapperClass.equals(getDefaultWebAppWrapper())) {
				list.remove(i);
				// add default wrappers list
				Class<? extends T>[] defaultWrappers = getDefaultWrappers();
				if (defaultWrappers != null) {
					int ndx = i;
					for (Class<? extends T> defaultWrapper : defaultWrappers) {
						// can't add default list stack to default list
						if (defaultWrapper.equals(getDefaultWebAppWrapper())) {
							throw new MadvocException("Default wrapper list is self-contained (cyclic dependency)!");
						}
						list.add(ndx, defaultWrapper);
						ndx++;
					}
				}
				continue;
			}
			if (ReflectUtil.isSubclass(wrapperClass, BaseActionWrapperStack.class)) {
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
		return list.toArray(new Class[list.size()]);
	}

	// ---------------------------------------------------------------- create

	/**
	 * Creates new wrapper.
	 */
	protected <R extends T> R createWrapper(Class<R> wrapperClass) {
		try {
		    return wrapperClass.newInstance();
		} catch (Exception ex) {
			throw new MadvocException("Invalid Madvoc wrapper: " + wrapperClass, ex);
		}
	}

}