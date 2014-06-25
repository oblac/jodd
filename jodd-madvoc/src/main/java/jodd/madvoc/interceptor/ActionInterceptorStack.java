// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.interceptor;

import jodd.madvoc.ActionRequest;
import jodd.madvoc.BaseActionWrapperStack;

/**
 * Groups common interceptors, avoiding lots of classes inside
 * an {@link jodd.madvoc.meta.InterceptedBy} annotation. It is only
 * used for grouping and will be not really added to the interceptors.
 */
public class ActionInterceptorStack extends BaseActionWrapperStack<ActionInterceptor> implements ActionInterceptor {

	public ActionInterceptorStack() {
	}

	public ActionInterceptorStack(Class<? extends ActionInterceptor>... interceptorClasses) {
		super(interceptorClasses);
	}

	/**
	 * Sets interceptor classes.
	 */
	public void setInterceptors(Class<? extends ActionInterceptor>... interceptors) {
		this.wrappers = interceptors;
	}

	/**
	 * Returns an array of interceptors.
	 */
	public Class<? extends ActionInterceptor>[] getInterceptors() {
		return getWrappers();
	}

	/**
	 * Interceptor is not used since this is just an interceptor container.
	 */
	public final Object intercept(ActionRequest actionRequest) throws Exception {
		return invoke(actionRequest);
	}

}