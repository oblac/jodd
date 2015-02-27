// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.pointcuts;

import jodd.proxetta.MethodInfo;

/**
 * Pointcut on all public methods that are <b>not</b> getters or setters
 */
public class AllRealMethodsPointcut extends ProxyPointcutSupport {

	public boolean apply(MethodInfo methodInfo) {

		if (hasReturnValue(methodInfo)
				&& (matchMethodName(methodInfo, "get*") || (matchMethodName(methodInfo, "is*")))
				&& hasNoArguments(methodInfo)) {
			// getter
			return false;
		}

		if (matchMethodName(methodInfo, "set*")
				&& hasOneArgument(methodInfo)) {
			// setter
			return false;
		}

		return isPublic(methodInfo);
	}
}
