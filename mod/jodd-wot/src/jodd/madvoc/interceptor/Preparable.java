// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.interceptor;

/**
 * Preparable actions will have their <code>prepare()</code> method called if
 * intercepted by {@link PrepareInterceptor}.
 */
public interface Preparable {

	/**
	 * Prepares the action.
	 */
	void prepare();

}
