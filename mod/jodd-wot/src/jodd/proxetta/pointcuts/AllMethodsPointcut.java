// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.pointcuts;

import jodd.proxetta.MethodInfo;

/**
 * Pointcut on all <b>public</b> and <b>non final</b> methods.
 */
public class AllMethodsPointcut extends ProxyPointcutSupport {

	public boolean apply(MethodInfo methodInfo) {
		return isPublic(methodInfo) && isAcceptable(methodInfo);
	}
}
