// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.action;

import jodd.madvoc.AppendingInterceptor;
import jodd.madvoc.MyInterceptorStack;
import jodd.madvoc.ServletConfigAltInterceptor;
import jodd.madvoc.interceptor.DefaultWebAppInterceptors;
import jodd.madvoc.interceptor.EchoInterceptor;
import jodd.madvoc.interceptor.ServletConfigInterceptor;
import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.In;
import jodd.madvoc.meta.InterceptedBy;
import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.Out;

@MadvocAction("cpt")
public class IntcptAction {

	@Action
	@InterceptedBy({EchoInterceptor.class, ServletConfigInterceptor.class})
	public void in1() {
	}

	@In
	String foo2;

	@Action
	@InterceptedBy({EchoInterceptor.class, ServletConfigAltInterceptor.class})
	public String in2() {
		return "##in1";
	}

	// ----------------------------------------------------------------

	@Out
	public String value;

	@Action
	@InterceptedBy({DefaultWebAppInterceptors.class, AppendingInterceptor.class})
	public void inap() {
		value = "appending";
	}

	@Action
	@InterceptedBy({DefaultWebAppInterceptors.class, AppendingInterceptor.Hey.class})
	public void inap2() {
		value = "appending2";
	}

	@Action
	@InterceptedBy(MyInterceptorStack.class)
	public void inap3() {
		value = "appending3";
	}

}