// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.advice;

import jodd.proxetta.MethodInfo;
import jodd.proxetta.ProxettaException;
import jodd.proxetta.ProxyAspect;
import jodd.proxetta.impl.ProxyProxetta;
import jodd.proxetta.pointcuts.ProxyPointcutSupport;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Utilities for applying {@link DelegateAdvice} to target.
 */
public class DelegateAdviceUtil {

	private static HashMap<Class, Class> advicesMap = new HashMap<Class, Class>();

	/**
	 * Proxy Proxetta, applied on all public methods of the target class.
	 */
	private static final ProxyProxetta PROXY_PROXETTA =
			ProxyProxetta.withAspects(
				new ProxyAspect(DelegateAdvice.class, new ProxyPointcutSupport() {
					public boolean apply(MethodInfo methodInfo) {
						return isPublic(methodInfo);
					}
				}));

	/**
	 * Applies advice on given target class and returns proxy instance.
	 */
	public static <T> T applyAdvice(Class<T> targetClass) {
		Class adviceClass = advicesMap.get(targetClass);

		if (adviceClass == null) {
			// advice not yet created

			adviceClass = PROXY_PROXETTA.builder(targetClass).define();

			advicesMap.put(targetClass, adviceClass);
		}

		// create new advice instance and injects target instance to it

		try {
			Object advice = adviceClass.newInstance();

			Field field = adviceClass.getField("$___target$0");

			field.set(advice, targetClass);

			return (T) advice;
		} catch (Exception ex) {
			throw new ProxettaException(ex);
		}
	}

	/**
	 * Injects target into proxy.
	 */
	public static void injectTargetIntoProxy(Object proxy, Object target) {
		Class proxyClass = proxy.getClass();

		try {
			Field field = proxyClass.getField("$___target$0");

			field.set(proxy, target);
		} catch (Exception ex) {
			throw new ProxettaException(ex);
		}
	}
}