// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package madvoc;

import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.MadvocAction;

@MadvocAction
public class DefaultAction {

	@Action
	public String view() {
		System.out.println("default action name");
		return "ok";
	}

}
