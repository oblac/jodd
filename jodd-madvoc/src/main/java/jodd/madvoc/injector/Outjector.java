// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

import jodd.madvoc.ActionRequest;

/**
 * Outjector interface.
 */
public interface Outjector {

	/**
	 * Outjects some content from action request.
	 * Usually, action object fields values are stored
	 * as attributes.
	 */
	public void outject(ActionRequest actionRequest);

}