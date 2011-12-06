// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package madvoc.rest;

import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.InOut;
import jodd.madvoc.meta.MadvocAction;

import static jodd.madvoc.meta.Action.NONE;

@MadvocAction("/re/")
public class ReAction {

	@InOut
	long girlId;

	@Action(value = "view/${girlId}", extension = NONE)
	public void viewGirl() {
		System.out.println("<1>" +girlId);
	}

	@Action(value = "view2/g-${girlId}")
	public String viewGirl2() {
		System.out.println("<2>" + girlId);
		return "redirect:/re/view/${girlId}";
	}

	@Action(value = "view3/${girlId:^[0-9]+}", extension = NONE)
	public String viewGirl3() {
		System.out.println("<3>" + girlId);
		return "#[method].OK";
	}
}
