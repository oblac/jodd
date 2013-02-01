// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.madvoc.MadvocException;
import jodd.madvoc.interceptor.ActionInterceptor;
import jodd.madvoc.interceptor.DefaultWebAppInterceptors;
import jodd.madvoc.interceptor.ActionInterceptorStack;
import jodd.util.ReflectUtil;
import jodd.petite.meta.PetiteInject;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import jodd.madvoc.WebApplication;
import jodd.madvoc.injector.MadvocParamsInjector;
import jodd.madvoc.interceptor.ConfigableActionInterceptorStack;
import jodd.petite.PetiteContainer;
import jodd.petite.meta.PetiteInitMethod;

/**
 * Manager for Madvoc interceptors. By default, all interceptors are pooled so there will be only one
 * instance per its type.
 */
public class InterceptorsManager {

	@PetiteInject
	protected MadvocConfig madvocConfig;
	@PetiteInject
	protected PetiteContainer madpc;

 	protected MadvocParamsInjector madvocParamsInjector;
	
	@PetiteInitMethod
	void createInjector() {
	    if (madpc != null) {
		madvocParamsInjector = new MadvocParamsInjector(madpc);
	    }
	}

	public InterceptorsManager() {
		interceptors = new HashMap<Class<? extends ActionInterceptor>, ActionInterceptor>();
	}

	// ---------------------------------------------------------------- container

	protected Map<Class<? extends ActionInterceptor>, ActionInterceptor> interceptors;

	/**
	 * Returns all action results. Should be used with care.
	 */
	public Map<Class<? extends ActionInterceptor>, ActionInterceptor> getAllActionInterceptors() {
		return interceptors;
	}


	/**
	 * Looks up for existing interceptor. Returns <code>null</code> if interceptor is not already registered.
	 */
	public ActionInterceptor lookup(Class<? extends ActionInterceptor> interceptorClass) {
		return interceptors.get(interceptorClass);
	}

	/**
	 * Resolves single interceptor. Creates new interceptor instance if not already registered.
	 * Does not expand the interceptors.
	 */
	public ActionInterceptor resolve(Class<? extends ActionInterceptor> interceptorClass) {
		ActionInterceptor interceptor = lookup(interceptorClass);
		if (interceptor == null) {
			interceptor = createInterceptor(interceptorClass);
			interceptors.put(interceptorClass, interceptor);
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
		    ActionInterceptor interceptor = interceptorClass.newInstance();
		    //just inject subclass of ConfigableActionInterceptorStack, other will be Initialized later
		    if (ReflectUtil.isSubclass(interceptorClass, ConfigableActionInterceptorStack.class) && madvocParamsInjector != null) {
			madvocParamsInjector.inject(interceptor);
		    }
		    return interceptor;
		} catch (InstantiationException iex) {
			throw new MadvocException("Unable to create Madvoc interceptor: " + interceptorClass, iex);
		} catch (IllegalAccessException iaex) {
			throw new MadvocException("Not enough rights to create Madvoc action interceptor: " + interceptorClass, iaex);
		}
	}

}
