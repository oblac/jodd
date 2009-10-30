// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package madvoc;

import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.InterceptedBy;
import jodd.madvoc.meta.Out;

@MadvocAction
@InterceptedBy(MyInterceptorStack.class)
public class OneMoveAction {

	// move example
	@Out
	String value;

	@Action
	public String execute() {
		System.out.println("move");
		value = "173";
		return "move:/%two%";
	}

}
