// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package madvoc;

import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.InterceptedBy;
import jodd.madvoc.meta.In;

@MadvocAction
@InterceptedBy(MyInterceptorStack.class)
public class TwoAction {

	@In
	String value;

	@Action(alias = "two")
	public void view() {
		System.out.println("TwoAction.invoke");
		System.out.println("===> " + value);
	}
}
