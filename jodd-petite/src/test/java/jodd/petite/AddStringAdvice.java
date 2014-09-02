// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.proxetta.ProxyAdvice;
import jodd.proxetta.ProxyTarget;

public class AddStringAdvice implements ProxyAdvice {

	public Object execute() throws Exception {
		String value = ProxyTarget.invoke().toString();

		value += " And Universe, too!";

		return value;
	}
}