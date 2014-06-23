// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.action.sys;

import jodd.madvoc.meta.InOut;
import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.RestAction;

@MadvocAction
public class UserAction {

	@InOut
	String id;

	@RestAction(value = "${id}")
	public void get() {}

	@RestAction(value = "${id}")
	public String post() {
		return "#post";		// dont have to do this
	}
}