// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package examples.proxetta;

import examples.proxetta.log.Log;

public class Zoo extends Zoo2 {

	@Override
	@Log
	public void zoo() {
		System.out.println("zoo");
		this.doo();
	}

	public void foo() {
		super.zoo();
	}
}
