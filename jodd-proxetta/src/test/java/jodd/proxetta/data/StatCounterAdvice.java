// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.data;

import jodd.proxetta.ProxyTarget;
import jodd.proxetta.ProxyAdvice;

public class StatCounterAdvice implements ProxyAdvice {

	static {
		StatCounter.counter++;
	}

	public Object execute() {
		StatCounter.counter++;
		System.out.println(">>" + ProxyTarget.targetMethodName());
		return ProxyTarget.invoke();
	}
}
