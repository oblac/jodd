// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

import jodd.madvoc.ActionRequest;

/**
 * Outjector interface. Outjections happen on action invocation.
 * All injection data should be cached in {@link jodd.madvoc.ActionRequest}
 * or {@link jodd.madvoc.ActionConfig}.
 */
public interface Outjector {

	/**
	 * Outjects some content from action request.
	 * Usually, action object fields values are stored
	 * as attributes.
	 */
	public void outject(ActionRequest actionRequest);

}