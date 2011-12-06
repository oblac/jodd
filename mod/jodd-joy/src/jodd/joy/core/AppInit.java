// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.core;

/**
 * Application initializer.
 */
public interface AppInit {

	/**
	 *  Invoked after the {@link DefaultAppCore app core} is started.
	 */
	void init();

	/**
	 * Invoked after the {@link DefaultAppCore app core} is stopped.
	 */
	void stop();
}
