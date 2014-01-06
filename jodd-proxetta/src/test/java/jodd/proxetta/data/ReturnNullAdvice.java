// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.data;

import jodd.proxetta.ProxyAdvice;
import jodd.proxetta.ProxyTarget;

public class ReturnNullAdvice implements ProxyAdvice {

	public Object execute() throws Exception {
		Object returnValue = null;

		if (returnValue != null) {
			returnValue = "1";
		}
		return ProxyTarget.returnValue(returnValue);
	}
}
