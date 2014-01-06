// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.filter;

import jodd.madvoc.ActionRequest;
import jodd.madvoc.BaseActionWrapper;

/**
 * Base {@link jodd.madvoc.filter.ActionFilter action filter}.
 */
public abstract class BaseActionFilter extends BaseActionWrapper implements ActionFilter {

	public final Object invoke(ActionRequest actionRequest) throws Exception {
		if (enabled) {
			return filter(actionRequest);
		} else {
			return actionRequest.invoke();
		}
	}

	@Override
	public String toString() {
		return "filter: " + this.getClass();
	}

}