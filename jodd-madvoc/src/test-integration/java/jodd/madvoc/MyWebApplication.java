// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.madvoc.component.InterceptorsManager;

public class MyWebApplication extends WebApplication {

	@Override
	public void registerMadvocComponents() {
		super.registerMadvocComponents();

		registerComponent(MyRewriter.class);
	}

	@Override
	protected void initInterceptors(InterceptorsManager interceptorsManager) {
		interceptorsManager.register("ServletConfigAltInterceptor", new ServletConfigAltInterceptor());
	}
}