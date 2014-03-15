// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

/**
 * Context injector. Context is injected only during the initialization.
 */
public interface ContextInjector<C> {

	/**
	 * Injects data from context object into the target.
	 */
	public void injectContext(Object target, C contextObject);

}