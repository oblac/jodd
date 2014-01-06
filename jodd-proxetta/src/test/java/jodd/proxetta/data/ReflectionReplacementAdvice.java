// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.data;

import jodd.proxetta.ProxyAdvice;
import jodd.proxetta.ProxyTarget;

import java.lang.reflect.Method;

public class ReflectionReplacementAdvice implements ProxyAdvice {

	public Object target;

	public Object execute() throws Exception {
		String methodName = ProxyTarget.targetMethodName();

		Class[] methodArgumentTypes = ProxyTarget.createArgumentsClassArray();

		Object[] methodArguments = ProxyTarget.createArgumentsArray();

		Method targetMethod = target.getClass().getMethod(methodName, methodArgumentTypes);

		Object result = targetMethod.invoke(target, methodArguments);

		return ProxyTarget.returnValue(result);
	}
}
