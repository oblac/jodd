// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

import jodd.madvoc.ActionRequest;

/**
 * Injector interface.
 */
public interface Injector {

	/**
	 * Injects some content into action request.
	 * Usually, some parameters/attributes are injected into
	 * action object.
	 */
	public void inject(ActionRequest actionRequest);

}