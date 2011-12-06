// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.interceptor;

import jodd.madvoc.ActionRequest;

/**
 * Prepares action by calling <code>prepare()</code> before action method invocation.
 * <p>
 * A typical use of this is to run some logic to load an object from the database, so that when parameters are set
 * they can be set on this object. For example, suppose you have a User object with two properties: id and name.
 * Provided that the params interceptor is called twice (once before and once after this interceptor), you can load the
 * User object using the id property, and then when the second params interceptor is called the parameter user.name will
 * be set, as desired, on the actual object loaded from the database.
 * @see PrepareAndIdInjectorInterceptor
 */
public class PrepareInterceptor extends ActionInterceptor {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object intercept(ActionRequest actionRequest) throws Exception {
		Object action = actionRequest.getAction();
		if (action instanceof Preparable) {
			((Preparable) action).prepare();
		}
		return actionRequest.invoke();
	}
}
