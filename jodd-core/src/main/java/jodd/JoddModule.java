// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd;

/**
 * Optional interface for Jodd modules.
 */
public interface JoddModule {

	/**
	 * Invoked once when module has been created.
	 */
	public void start();

}