// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.core;

/**
 * Application initializer callback interface.
 * Create an implementation and register it as Petite bean
 * under the name "appInit". It will be called once when
 * application core has been started,
 * so user can initialize the web application.
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
