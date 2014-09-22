// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.data;

import jodd.proxetta.ProxyAdvice;
import jodd.proxetta.ProxyTarget;

public class InvalidAdvice implements ProxyAdvice {

	public static class Aaa {
	}

	public Object execute() throws Exception {
		return ProxyTarget.invoke();
	}
}