// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.madvoc.MadvocException;
import jodd.madvoc.filter.ActionFilter;
import jodd.madvoc.interceptor.ActionInterceptor;
import jodd.madvoc.interceptor.DefaultWebAppInterceptors;
import jodd.madvoc.interceptor.ActionInterceptorStack;
import jodd.util.ArraysUtil;
import jodd.util.ReflectUtil;
import jodd.petite.meta.PetiteInject;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Manager for Madvoc interceptors. By default, all interceptors are cached,
 * so there will be only one instance per its type.
 */
public class InterceptorsManager {

	@PetiteInject
	protected MadvocConfig madvocConfig;

	@PetiteInject
 	protected MadvocContextInjector madvocContextInjector;
	
	public InterceptorsManager() {
		interceptors = new HashMap<String, ActionInterceptor>();
	}

	// ---------------------------------------------------------------- container

	protected Map<String, ActionInterceptor> interceptors;

	/**
	 * Returns all action results. Should be used with care.
	 */
	public Map<String, ActionInterceptor> getAllActionInterceptors() {
		return interceptors;
	}

	/**
	 * Registers interceptor instance for given name. Interceptor
	 * instance gets injected with {@link MadvocContextInjector}.
	 */
	public void register(String interceptorName, ActionInterceptor actionInterceptor) {
		madvocContextInjector.injectMadvocContext(actionInterceptor);
		madvocContextInjector.injectMadvocParams(actionInterceptor);

		interceptors.put(interceptorName, actionInterceptor);
	}

	/**
	 * Looks up for existing interceptor. Returns <code>null</code> if interceptor is not already registered.
	 */
	public ActionInterceptor lookup(String interceptorName) {
		return interceptors.get(interceptorName);
	}

	/**
	 * Resolves single interceptor. Creates new interceptor instance if not already registered.
	 * Does not expand the interceptors.
	 */
	public ActionInterceptor resolve(Class<? extends ActionInterceptor> interceptorClass) {
		String interceptorClassName = interceptorClass.getName();

		ActionInterceptor interceptor = lookup(interceptorClassName);

		if (interceptor == null) {
			interceptor = createInterceptor(interceptorClass);

			madvocContextInjector.injectMadvocContext(interceptor);
			madvocContextInjector.injectMadvocParams(interceptor);

			interceptors.put(interceptorClassName, interceptor);
		}
		return interceptor;
	}

	/**
	 * Resolves interceptors. Unregistered interceptors will be registered. Returned array may be
	 * different size than size of provided array, due to {@link #expand(Class[]) expanding}.
	 */
	public ActionInterceptor[] resolveAll(Class<? extends ActionInterceptor>... interceptorClasses) {
		if (interceptorClasses == null) {
			return null;
		}
		interceptorClasses = expand(interceptorClasses);
		ActionInterceptor[] result = new ActionInterceptor[interceptorClasses.length];
		for (int i = 0; i < interceptorClasses.length; i++) {
			result[i] = resolve(interceptorClasses[i]);
		}
		return result;
	}

	/**
	 * Extracts all action filters from the array of action interceptors.
	 */
	public ActionFilter[] extractActionFilters(ActionInterceptor[] actionInterceptors) {
		ActionFilter[] result = new ActionFilter[actionInterceptors.length];

		int index = 0;
		for (ActionInterceptor interceptor : actionInterceptors) {
			if (interceptor instanceof ActionFilter) {
				result[index] = (ActionFilter) interceptor;
				index++;
			}
		}

		return ArraysUtil.subarray(result, 0, index);
	}

	/**
	 * Removes all action filters from the array of action interceptors.
	 */
	public ActionInterceptor[] extractActionInterceptor(ActionInterceptor[] actionInterceptors) {
		ActionInterceptor[] result = new ActionInterceptor[actionInterceptors.length];

		int index = 0;
		for (ActionInterceptor interceptor : actionInterceptors) {
			if ((interceptor instanceof ActionFilter) == false) {
				result[index] = interceptor;
				index++;
			}
		}

		return ArraysUtil.subarray(result, 0, index);
	}

	// ---------------------------------------------------------------- expander

	/**
	 * Expands all {@link jodd.madvoc.interceptor.DefaultWebAppInterceptors} and
	 * {@link jodd.madvoc.interceptor.ActionInterceptorStack} in action interceptor array,
	 * by replacing them with real values.
	 */
	@SuppressWarnings({"unchecked"})
	protected Class<? extends ActionInterceptor>[] expand(Class<? extends ActionInterceptor>[] actionInterceptors) {
		if (actionInterceptors == null) {
			return null;
		}
		List<Class<? extends ActionInterceptor>> list = new ArrayList<Class<? extends ActionInterceptor>>(actionInterceptors.length);
		list.addAll(Arrays.asList(actionInterceptors));

		int i = 0;
		while (i < list.size()) {
			Class<? extends ActionInterceptor> interceptorClass = list.get(i);
			if (interceptorClass == null) {
				continue;
			}
			if (interceptorClass.equals(DefaultWebAppInterceptors.class)) {
				list.remove(i);
				// add default interceptors list
				Class<? extends ActionInterceptor>[] defaultInterceptors = madvocConfig.getDefaultInterceptors();
				if (defaultInterceptors != null) {
					int ndx = i;
					for (Class<? extends ActionInterceptor> defaultInterceptor : defaultInterceptors) {
						// can't add default list stack to default list
						if (defaultInterceptor.equals(DefaultWebAppInterceptors.class)) {
							throw new MadvocException("Default interceptor list is self-contained (cyclic dependency)!");
						}
						list.add(ndx, defaultInterceptor);
						ndx++;
					}
				}
				continue;
			}
			if (ReflectUtil.isSubclass(interceptorClass, ActionInterceptorStack.class)) {
				ActionInterceptorStack stack = (ActionInterceptorStack) resolve(interceptorClass);
				list.remove(i);
				Class<? extends ActionInterceptor>[] stackInterceptors = stack.getInterceptors();
				if (stackInterceptors != null) {
					list.addAll(i, Arrays.asList(stackInterceptors));
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
	 * Creates new {@link jodd.madvoc.interceptor.ActionInterceptor}.
	 */
	protected ActionInterceptor createInterceptor(Class<? extends ActionInterceptor> interceptorClass) {
		try {
		    return interceptorClass.newInstance();
		} catch (Exception ex) {
			throw new MadvocException("Unable to create Madvoc interceptor: " + interceptorClass, ex);
		}
	}

}