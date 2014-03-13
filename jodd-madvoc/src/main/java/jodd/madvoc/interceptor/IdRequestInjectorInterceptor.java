// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.interceptor;

import jodd.madvoc.component.ScopeDataResolver;
import jodd.madvoc.injector.RequestScopeInjector;
import jodd.madvoc.ScopeData;
import jodd.madvoc.ActionRequest;
import jodd.madvoc.ScopeType;
import jodd.madvoc.component.MadvocConfig;
import jodd.madvoc.meta.In;

/**
 * Injects only ID request attributes and parameters that ends with '.id'.
 * @see PrepareAndIdInjectorInterceptor
 */
public class IdRequestInjectorInterceptor extends BaseActionInterceptor {

	protected static final String ATTR_NAME_ID_SUFFIX = ".id";

	@In(scope = ScopeType.CONTEXT)
	protected MadvocConfig madvocConfig;

	@In(scope = ScopeType.CONTEXT)
	protected ScopeDataResolver scopeDataResolver;

	protected RequestScopeInjector requestInjector;

	@Override
	public void init() {
		requestInjector = new RequestScopeInjector(madvocConfig, scopeDataResolver) {
			@Override
			protected String getMatchedPropertyName(ScopeData.In in, String attrName) {
				if (attrName.endsWith(ATTR_NAME_ID_SUFFIX) == false) {
					return null;
				}
				return super.getMatchedPropertyName(in, attrName);
			}
		};
		requestInjector.getConfig().setInjectAttributes(false);
	}

	/**
	 * {@inheritDoc}
	 */
	public Object intercept(ActionRequest actionRequest) throws Exception {
		injectIdsFromRequest(actionRequest);
		return actionRequest.invoke();
	}

	/**
	 * Injects IDs from request. Invoked before action request is invoked.
	 */
	protected void injectIdsFromRequest(ActionRequest actionRequest) {
		requestInjector.inject(actionRequest);
	}

}
