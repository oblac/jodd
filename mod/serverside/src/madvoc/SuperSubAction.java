// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package madvoc;

import jodd.madvoc.meta.In;
import jodd.madvoc.meta.Action;
import madvoc.item.Item;

public class SuperSubAction extends SuperAction {

	@In
	Item item;

	@In("item")
	Item item2;

	@Action
	public void view() {
		System.out.println("MethodAction.hello");
		System.out.println("item = " + item);
		System.out.println("item2 = " + item2);
		common();
	}

}
