package jodd.aop;

import jodd.util.ClassUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * Factory for proxies, created using Java own library.
 */
public class AopProxy {

	/**
	 * Creates a proxy of given target and the aspect.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T proxyOf(T target, Class<? extends Aspect> aspectClass) {
		final Aspect aspect;

		try {
			aspect = ClassUtil.newInstance(aspectClass, target);
		}
		catch (Exception e) {
			throw new IllegalArgumentException("Can't create new instance of aspect class", e);
		}

		return (T) newProxyInstance(target.getClass().getClassLoader(), aspect, target.getClass().getInterfaces());
	}

	/**
	 * Creates a proxy from given {@link Aspect}.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T proxyOf(Aspect aspect) {
		final Object target = aspect.getTarget();
		return (T) newProxyInstance(target.getClass().getClassLoader(), aspect, target.getClass().getInterfaces());
	}

	/**
	 * Simple wrapper for javas {@code newProxyInstance}.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newProxyInstance(ClassLoader classloader, InvocationHandler invocationHandler, Class<?>... interfaces) {
		if (interfaces.length == 0) {
			throw new IllegalArgumentException("No interfaces of target class found.");
		}
		return (T) Proxy.newProxyInstance(classloader, interfaces, invocationHandler);
	}

}
