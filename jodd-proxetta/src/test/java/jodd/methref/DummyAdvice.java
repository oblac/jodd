// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.methref;

import jodd.proxetta.ProxyAdvice;
import jodd.proxetta.ProxyTarget;

public class DummyAdvice implements ProxyAdvice {

	public Object execute() throws Exception {
		return ProxyTarget.invoke();
	}

}