//  Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta;

import jodd.JoddProxetta;

import java.lang.reflect.Field;

/**
 * Proxetta utilities.
 */
public class ProxettaUtil {

	/**
	 * Returns target class if proxetta applied on given class.
	 * If not, returns given class as result.
	 */
	public static Class getTargetClass(Class proxy) {

		String name = proxy.getName();

		if (name.endsWith(JoddProxetta.proxyClassNameSuffix)) {
			return proxy.getSuperclass();
		}

		if (name.endsWith(JoddProxetta.wrapperClassNameSuffix)) {
			return getTargetWrapperType(proxy);
		}

		return proxy;
	}


	/**
	 * Injects some target instance into {@link jodd.proxetta.impl.WrapperProxetta wrapper} proxy
	 * in given {@link jodd.proxetta.impl.WrapperProxettaBuilder#setTargetFieldName(String) target field name}.
	 */
	public static void injectTargetIntoWrapper(Object target, Object wrapper, String targetFieldName) {
		try {
			Field field = wrapper.getClass().getField(targetFieldName);
			field.setAccessible(true);
			field.set(wrapper, target);
		} catch (Exception ex) {
			throw new ProxettaException(ex);
		}
	}

	/**
	 * Injects target instance into proxy using default target field name.
	 * @see #injectTargetIntoWrapper(Object, Object, String)
	 */
	public static void injectTargetIntoWrapper(Object target, Object wrapper) {
		injectTargetIntoWrapper(target, wrapper, JoddProxetta.wrapperTargetFieldName);
	}

	/**
	 * Returns wrapper target type.
	 */
	public static Class getTargetWrapperType(Class wrapperClass) {
		Field field;
		try {
			field = wrapperClass.getField(JoddProxetta.wrapperTargetFieldName);
		} catch (NoSuchFieldException nsfex) {
			throw new ProxettaException(nsfex);
		}
		return field.getType();
	}

}