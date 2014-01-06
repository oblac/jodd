// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.proxetta.ProxyAdvice;
import jodd.proxetta.ProxyTarget;

import java.lang.reflect.Method;

/**
 * Scoped proxy advice. For each wrapped method,
 * it will lookup for the bean from the container
 * and will delegate the method call to it.
 */
public class ScopedProxyAdvice implements ProxyAdvice {

	/**
	 * Petite container.
	 */
	public PetiteContainer petiteContainer;
	/**
	 * Bean name for lookup.
	 */
	public String name;

	public Object execute() throws Exception {
		Object target = petiteContainer.getBean(name);

		// collect data about target method call

		String methodName = ProxyTarget.targetMethodName();

		Class[] methodArgumentTypes = ProxyTarget.createArgumentsClassArray();

		Object[] methodArguments = ProxyTarget.createArgumentsArray();

		// delegate method call to target

		Method targetMethod = target.getClass().getMethod(methodName, methodArgumentTypes);

		Object result = targetMethod.invoke(target, methodArguments);

		// return target result

		return ProxyTarget.returnValue(result);
	}
}
