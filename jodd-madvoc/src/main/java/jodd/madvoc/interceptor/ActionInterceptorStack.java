// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.interceptor;


import jodd.madvoc.ActionRequest;
import jodd.madvoc.MadvocException;

/**
 * Groups common interceptors, avoiding lots of classes inside
 * an {@link jodd.madvoc.meta.InterceptedBy} annotation. It is only
 * used for grouping and will be not really added to the interceptors.
 */
public abstract class ActionInterceptorStack extends ActionInterceptor {

	protected Class<? extends ActionInterceptor>[] interceptors;

	/**
	 * Constructs an interceptor stack with the given interceptors
	 */
	protected ActionInterceptorStack(Class<? extends ActionInterceptor>... interceptorClasses) {
		if (interceptorClasses.length == 0) {
			throw new MadvocException("Empty action interceptor stack is not allowed.");
		}
		this.interceptors = interceptorClasses;
	}

	/**
	 * Interceptor is not used since this is just an interceptor container.
	 */
	@Override
	public final String intercept(ActionRequest actionRequest) throws Exception {
		throw new MadvocException("Interceptor stack can not be invoked as an interceptor.");
	}

	/**
	 * Returns an array of interceptors.
	 */
	public Class<? extends ActionInterceptor>[] getInterceptors() {
		return interceptors;
	}
}
