// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package examples.proxetta.log;

import jodd.proxetta.ProxyAdvice;
import jodd.proxetta.ProxyTarget;

public class CustomProxyAdvice implements ProxyAdvice {

	public Object execute() {
		System.out.println("__");
		Object result = ProxyTarget.invoke();
		System.out.println("~~");
		return result; 
	}
}
