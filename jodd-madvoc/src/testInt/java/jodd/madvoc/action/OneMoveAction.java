// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.action;

import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.Out;

@MadvocAction
public class OneMoveAction {

	MyResult result;

	@Out
	String value;

	@Action
	public String execute() {
		value = "777";
		return "move:/<two>";
	}

	@Action
	public void go() {
		value = "888";
		result.moveTo("two");
	}

}