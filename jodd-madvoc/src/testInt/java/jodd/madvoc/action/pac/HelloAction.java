// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.action.pac;

import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.MadvocAction;

@MadvocAction
public class HelloAction {

	@Action
	public void view() {
		System.out.println("/pac/hello.html");
	}

}