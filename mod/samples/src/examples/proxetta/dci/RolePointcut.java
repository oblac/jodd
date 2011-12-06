// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package examples.proxetta.dci;

import jodd.proxetta.MethodInfo;
import jodd.proxetta.pointcuts.ProxyPointcutSupport;

/**
 * This pointcut looks for all public top level methods.
 * That's are the places where we gonna insert our logic.
 */
public class RolePointcut extends ProxyPointcutSupport {

	public boolean apply(MethodInfo methodInfo) {
		if (methodInfo.isTopLevelMethod() && isPublic(methodInfo)) {
			return true;
		}
		return false;

	}
}
