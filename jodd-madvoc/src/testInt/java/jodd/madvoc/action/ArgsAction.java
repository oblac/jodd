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

	public static class Data2 {
		@In Integer id;
		@Out String value;
	}

	@Out
	String name;

	@Action
	public void hello(Hello hello, Data2 data) {
		name = "mad " + hello.id;
		hello.out = "voc";
		data.value = "jodd " + data.id;
	}
}