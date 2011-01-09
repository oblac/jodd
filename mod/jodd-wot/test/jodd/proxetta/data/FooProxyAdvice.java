// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.data;

import jodd.proxetta.ProxyAdvice;
import jodd.proxetta.ProxyTarget;

public class FooProxyAdvice implements ProxyAdvice {

	static int count;

	public static int getCount() {
		return count++;
	}

	public Object execute() {
		return ProxyTarget.invoke();
	}
}