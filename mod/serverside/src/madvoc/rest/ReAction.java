// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package madvoc.rest;

import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.InOut;
import jodd.madvoc.meta.MadvocAction;

import static jodd.madvoc.meta.Action.NONE;

@MadvocAction("/re/")
public class ReAction {

	@InOut
	long itemId;

	@Action(value = "view/${itemId}", extension = NONE)
	public void viewItem() {
		System.out.println("<1>" + itemId);
	}

	@Action(value = "view2/g-${itemId}")
	public String viewItem2() {
		System.out.println("<2>" + itemId);
		return "redirect:/re/view/${itemId}";
	}

	@Action(value = "view3/${itemId:^[0-9]+}", extension = NONE)
	public String viewItem3() {
		System.out.println("<3>" + itemId);
		return "#[method].OK";
	}
}
