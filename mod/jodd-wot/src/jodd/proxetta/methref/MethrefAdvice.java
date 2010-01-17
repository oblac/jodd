// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.methref;

import jodd.proxetta.ProxyAdvice;
import jodd.proxetta.ProxyTarget;

/**
 * Advice applied on all other methods (i.e. that doesn't return String).
 * Puts method name in class variable that can be accessed using reflection later.
 */
public class MethrefAdvice implements ProxyAdvice {

	String methodName;

	public Object execute() {
		methodName = ProxyTarget.targetMethodName();

		ProxyTarget.pushDefaultResultValue();
		return null;
	}
}