// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package madvoc;

import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.InterceptedBy;

import jodd.madvoc.meta.Action;

@MadvocAction
@InterceptedBy(MyInterceptorStack.class)
public class OneRedirectAction {

	// redirect example
	String value;

	@Action
	public String execute() {
		System.out.println("redirect");
		value = "173";
		return "redirect:/%two%?value=${value}";
	}
	
}
