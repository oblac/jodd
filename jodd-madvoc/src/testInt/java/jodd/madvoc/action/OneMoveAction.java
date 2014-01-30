// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.action;

import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.Out;
import jodd.madvoc.result.Result;

@MadvocAction
public class OneMoveAction {

	Result result = new Result();

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