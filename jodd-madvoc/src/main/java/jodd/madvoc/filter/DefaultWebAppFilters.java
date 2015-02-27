// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.filter;

import jodd.madvoc.ActionRequest;
import jodd.madvoc.MadvocException;

/**
 * Marker for default filters for easy configuration.
 * It will be replaced with the result of {@link jodd.madvoc.component.MadvocConfig#getDefaultFilters()}
 * during action registration.
 */
public final class DefaultWebAppFilters extends BaseActionFilter {

	public String filter(ActionRequest actionRequest) throws Exception {
		throw new MadvocException(this.getClass().getSimpleName() + " is just a marker");
	}

}