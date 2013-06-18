//  Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.advice;

import jodd.proxetta.ProxyAdvice;
import jodd.proxetta.ProxyTarget;

import java.lang.reflect.Method;

/**
 * Delegates calls to target using reflection. Offers
 * separation between between points where class is loaded
 * and class is used.
 * <p>
 * Allows usage of classes loaded by different class loader.
 * For example, if you have an instance of class loaded by
 * <b>parent-last</b> class loader, delegate allows
 * to still call it using plain java. Under the hood,
 * each method will actually invoke target method
 * using reflection.
 */
public class DelegateAdvice implements ProxyAdvice {

	/**
	 * Target object.
	 */
	public Object _target;

	/**
	 * Looks up for method in target object and invokes it using reflection.
	 */
	public Object execute() throws Exception {
		String methodName = ProxyTarget.targetMethodName();
		Class[] argTypes = ProxyTarget.createArgumentsClassArray();
		Object[] args = ProxyTarget.createArgumentsArray();

		Class type = _target.getClass();
		Method method = type.getMethod(methodName, argTypes);

		Object result = method.invoke(_target, args);

		return ProxyTarget.returnValue(result);
	}
}