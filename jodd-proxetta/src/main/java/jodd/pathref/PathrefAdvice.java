// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.pathref;

import jodd.proxetta.ProxyAdvice;
import jodd.proxetta.ProxyTarget;

import static jodd.proxetta.ProxyTarget.returnType;
import static jodd.proxetta.ProxyTarget.targetMethodName;

/**
 * Pathref advice applied on all methods.
 */
public class PathrefAdvice implements ProxyAdvice {

	Pathref pathref;

	/**
	 * Reads method name and appends it. Creates object for next call and
	 * returns that value. If next object is unsupported, it will return null;
	 */
	public Object execute() {
		String methodName = targetMethodName();

		Class returnType = returnType();

		Object next = pathref.continueWith(this, methodName, returnType);

		return ProxyTarget.returnValue(next);
	}

}