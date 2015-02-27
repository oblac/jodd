// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.methref;

import jodd.proxetta.ProxyAdvice;
import jodd.proxetta.ProxyTarget;

import static jodd.proxetta.ProxyTarget.returnType;
import static jodd.proxetta.ProxyTarget.targetMethodName;

/**
 * Methref advice applied on all methods. It puts method name in
 * class variable that can be accessed later using reflection.
 */
public class MethrefAdvice implements ProxyAdvice {

	String methodName;

	/**
	 * Reads method name and stores it in local variable.
	 * For methods that return <code>String</code> returns the method name,
	 * otherwise returns <code>null</code>.
	 */
	public Object execute() {
		methodName = targetMethodName();

		Class returnType = returnType();

		if (returnType == String.class) {
			return ProxyTarget.returnValue(targetMethodName());
		}
		return ProxyTarget.returnValue(null);
	}

}