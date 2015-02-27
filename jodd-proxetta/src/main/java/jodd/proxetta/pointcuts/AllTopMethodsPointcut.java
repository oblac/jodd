// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.pointcuts;

import jodd.proxetta.MethodInfo;

/**
 * Filter for top-level public methods
 */
public class AllTopMethodsPointcut extends ProxyPointcutSupport {

	public boolean apply(MethodInfo methodInfo) {
		return
				isTopLevelMethod(methodInfo) &&
				isPublic(methodInfo);
	}
}
