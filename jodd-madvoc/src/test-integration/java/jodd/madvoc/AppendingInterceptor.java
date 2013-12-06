// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.madvoc.action.IntcptAction;
import jodd.madvoc.interceptor.ActionInterceptor;
import jodd.madvoc.interceptor.BaseActionInterceptor;

public class AppendingInterceptor extends BaseActionInterceptor {

	public static class Hey extends AppendingInterceptor {
	}

	private String addon = "<?>";
	public String getAddon() {
		return addon;
	}

	public void setAddon(String addon) {
		this.addon = addon;
	}

	public Object intercept(ActionRequest actionRequest) throws Exception {
		Object result =  actionRequest.invoke();

		Object action = actionRequest.getAction();

		if (action instanceof IntcptAction) {
			IntcptAction intcptAction = (IntcptAction) action;
			intcptAction.value += addon;
		}

		return result;
	}
}