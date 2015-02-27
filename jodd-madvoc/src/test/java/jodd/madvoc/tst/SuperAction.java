// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.tst;

import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.In;
import jodd.madvoc.meta.MadvocAction;

@MadvocAction
public class SuperAction {

	public static class Foo {
		public String value;
	}

	@Action
	public void add(@In String name, @In Foo foo) {
		System.out.println(name);
		System.out.println(foo.value);
	}

}