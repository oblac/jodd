// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.action;

import jodd.madvoc.interceptor.DefaultWebAppInterceptors;
import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.InterceptedBy;
import jodd.madvoc.meta.MadvocAction;

@MadvocAction
public class ExcAction {

	@Action
	@InterceptedBy({ExcInterceptor.class, DefaultWebAppInterceptors.class})
	public void view() {
		int a = 0;
		int b = 4 / a;
		System.out.println(b);
	}

	@Action
	public String red() {
		return "redirect:/500.html";
	}

}