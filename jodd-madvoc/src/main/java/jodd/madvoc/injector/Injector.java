// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

import jodd.madvoc.ActionRequest;

/**
 * Injector interface. Injections happen on action invocation.
 * All injection data should be cached in {@link jodd.madvoc.ActionRequest}
 * or {@link jodd.madvoc.ActionConfig}.
 */
public interface Injector {

	/**
	 * Injects some content into action request.
	 * Usually, some parameters/attributes are injected into
	 * action object.
	 */
	public void inject(ActionRequest actionRequest);

}