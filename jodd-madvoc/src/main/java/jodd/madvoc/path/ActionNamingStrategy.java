// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.path;

import jodd.madvoc.ActionId;
import jodd.madvoc.ActionNames;

import java.lang.reflect.Method;

/**
 * Naming strategy for building paths and http method.
 */
public interface ActionNamingStrategy {

	/**
	 * Builds {@link jodd.madvoc.ActionId}.
	 *
	 * @param actionClass action class
	 * @param actionMethod action method
	 * @param actionNames action names
	 */
	public ActionId buildActionId(
			Class actionClass,
			Method actionMethod,
			ActionNames actionNames);

}