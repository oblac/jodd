// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.pointcuts;

import jodd.proxetta.MethodInfo;

/**
 * Pointcut on all public non final getters methods.
 */
public class AllGettersPointcut extends ProxyPointcutSupport {

	public boolean apply(MethodInfo methodInfo) {
		return
				isPublic(methodInfo)
				&& isAcceptable(methodInfo)
				&& hasReturnValue(methodInfo)
				&& (matchMethodName(methodInfo, "get*") || (matchMethodName(methodInfo, "is*")))
				&& hasNoArguments(methodInfo)
				;
	}
}
