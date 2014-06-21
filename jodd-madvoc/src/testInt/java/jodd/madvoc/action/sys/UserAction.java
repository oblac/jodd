// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.action.sys;

import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.InOut;
import jodd.madvoc.meta.MadvocAction;

// todo ugly
@MadvocAction("/[package]/")
public class UserAction {

	@InOut
	String id;

	//@Action("/[package]/[class]/${id}")
	@Action(value = "[class]/${id}", extension = Action.NONE)
	public String view() {
		return "#index";
	}
}