// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.filter;

import jodd.madvoc.ActionRequest;
import jodd.madvoc.ActionWrapper;

/**
 * Action filter is a wrapper that is invoked before action and after the rendering.
 */
public interface ActionFilter extends ActionWrapper {

	/**
	 * Filters action requests.
	 */
	Object filter(ActionRequest actionRequest) throws Exception;

}