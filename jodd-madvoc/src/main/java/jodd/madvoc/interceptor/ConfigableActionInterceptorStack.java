// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.interceptor;


public abstract class ConfigableActionInterceptorStack extends ActionInterceptorStack {

	protected ConfigableActionInterceptorStack() {
		super(DefaultWebAppInterceptors.class);
	}

	public void setInterceptors(Class<? extends ActionInterceptor>... interceptors) {
		this.interceptors = interceptors;
	}
}
