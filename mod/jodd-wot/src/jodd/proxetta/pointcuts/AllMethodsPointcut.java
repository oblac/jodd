// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.pointcuts;

import jodd.proxetta.MethodSignature;

/**
 * Pointcut on all <b>public</b> methods.
 */
public class AllMethodsPointcut extends ProxyPointcutSupport {

	public boolean apply(MethodSignature msign) {
		return isPublic(msign);
	}
}
