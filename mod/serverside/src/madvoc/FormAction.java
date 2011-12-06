// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package madvoc;

import jodd.madvoc.meta.In;
import jodd.madvoc.meta.InterceptedBy;
import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.Out;
import jodd.madvoc.meta.Action;
import jodd.madvoc.interceptor.EchoInterceptor;
import jodd.madvoc.interceptor.DefaultWebAppInterceptors;

@MadvocAction
@InterceptedBy({EchoInterceptor.class, DefaultWebAppInterceptors.class})
public class FormAction {

	@In @Out
	FooFormBean foo;

	@Action
	public void view() {
	}
	
	@Action(method = "POST")
	public String post() {
		System.out.println("FormAction.post");
		System.out.println(foo.toString());
		return "#";
	}
}
