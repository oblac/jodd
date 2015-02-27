// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.action;

import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.In;
import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.Out;

@MadvocAction
public class TwoAction {

	@In
	String value;

	@Out
	String val;

	@Action(alias = "two")
	public void view() {
		val = value;
	}
}
