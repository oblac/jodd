// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.action;

import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.InOut;
import jodd.madvoc.meta.MadvocAction;

import static jodd.madvoc.meta.Action.NONE;

@MadvocAction("/re/")
public class RestAction {

	@InOut
	long itemId;

	@Action(value = "view/${itemId}", extension = NONE)
	public String viewItem() {
		return "#view";
	}

	@Action(value = "view2/g-${itemId}")
	public String viewItem2() {
		return "redirect:/re/view/${itemId}";
	}

	@Action(value = "view3/${itemId:^[0-9]+}", extension = NONE)
	public String viewItem3() {
		return "#view";
	}

}