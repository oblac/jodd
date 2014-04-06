// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.action;

import jodd.madvoc.ActionRequest;
import jodd.madvoc.interceptor.BaseActionInterceptor;

public class ExcInterceptor extends BaseActionInterceptor {

	public Object intercept(ActionRequest actionRequest) throws Exception {
		try {
			return actionRequest.invoke();
		}
		catch (ArithmeticException ex) {
			return "redirect:/500.html";
		}
	}
}