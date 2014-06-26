// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.action;

import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.In;
import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.Out;

@MadvocAction
public class ArgsAction {

	class Hello {
		@In Integer id;
		@Out String out;
	}

	static class Data2 {
		@In int id;
		@Out String value;
	}

	@In
	String who;

	@Out
	String name;

	@Action
	public void hello(Hello hello, Data2 data) {
		name = "mad " + hello.id;
		hello.out = "voc";
		data.value = "jodd " + data.id;
	}

	@Action
	public void world(@In String name, @In("hello") Data2 hello) {
		System.out.println(who);
		System.out.println(name);
		System.out.println(hello);

		this.name = who + "+" + name + "+" + hello.id;
	}

}