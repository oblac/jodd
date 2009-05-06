// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.pointcuts;

import jodd.proxetta.MethodSignature;

/**
 * Pointcut on all public getters methods.
 */
public class AllGettersPointcut extends ProxyPointcutSupport {

	public boolean apply(MethodSignature msign) {
		return
				isPublic(msign)
				&& hasReturnValue(msign)
				&& (matchMethodName(msign, "get*") || (matchMethodName(msign, "is*")))
				&& hasNoArguments(msign)
				;
	}
}
