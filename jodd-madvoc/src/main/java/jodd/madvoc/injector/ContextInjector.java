// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

import jodd.madvoc.ScopeData;

/**
 * Context injector. Context is injected only during the initialization.
 * @see jodd.madvoc.component.ContextInjectorComponent
 */
public interface ContextInjector<C> {

	/**
	 * Injects data from context object into the target.
	 * @param target injection target
	 * @param scopeData all scope data for target's type (shared during context initialization)
	 * @param contextObject injection source or any key for retrieving context
	 */
	public void injectContext(Target target, ScopeData[] scopeData, C contextObject);

}