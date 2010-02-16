// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.interceptor;

import jodd.madvoc.ActionRequest;
import jodd.madvoc.ScopeType;
import jodd.madvoc.component.MadvocConfig;
import jodd.madvoc.meta.In;

/**
 * This is a simple join of {@link IdRequestInjectorInterceptor} and {@link PrepareInterceptor}.
 * Since it is common to use these interceptors together, this one joins then into one interceptor.
 * This join is a bit more efficient, since ids will be injected only if action is
 * {@link Preparable}. 
 */
public class PrepareAndIdInjectorInterceptor extends ActionInterceptor {

	@In(scope = ScopeType.CONTEXT)
	protected MadvocConfig madvocConfig;

	private final IdRequestInjectorInterceptor idInjector;

	public PrepareAndIdInjectorInterceptor() {
		idInjector = new IdRequestInjectorInterceptor();
	}

	@Override
	public void init() {
		idInjector.madvocConfig = madvocConfig;
		idInjector.init();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object intercept(ActionRequest actionRequest) throws Exception {
		Object action = actionRequest.getAction();
		if (action instanceof Preparable) {
			idInjector.intercept(actionRequest);
			((Preparable) action).prepare();
		}
		return actionRequest.invoke();
	}

}
