// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.interceptor;

import jodd.madvoc.MadvocException;
import jodd.madvoc.ActionRequest;
import jodd.madvoc.component.MadvocConfig;

/**
 * Marker for default interceptors for easy configuration purposes.
 * It will be replaced with the result of {@link MadvocConfig#getDefaultInterceptors()}
 * during action registration.
 */
public final class DefaultWebAppInterceptors extends ActionInterceptor {

	@Override
	public String intercept(ActionRequest actionRequest) throws Exception {
		throw new MadvocException(this.getClass().getSimpleName() + " must be used only for actions configuration.");
	}
}
