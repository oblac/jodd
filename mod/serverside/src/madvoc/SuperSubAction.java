// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package madvoc;

import jodd.madvoc.meta.In;
import jodd.madvoc.meta.Action;
import madvoc.girl.Girl;

public class SuperSubAction extends SuperAction {

	@In
	Girl girl;

	@In("girl") Girl girl2;

	@Action
	public void view() {
		System.out.println("MethodAction.hello");
		System.out.println("girl = " + girl);
		System.out.println("girl2 = " + girl2);
		common();
	}

}
