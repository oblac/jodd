// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.action;

import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.In;
import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.Out;

@MadvocAction
public class ArgsAction {

	public class Hello {
		@In Integer id;
		@Out String out;
	}

	@Out
	String name;

	@Action
	public void hello(Hello hello) {
		name = "mad " + hello.id;
	}
}