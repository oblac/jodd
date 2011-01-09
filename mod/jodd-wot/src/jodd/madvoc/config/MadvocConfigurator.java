// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.config;

/**
 * Implementations provide full or partial Madvoc web application configuration.
 * May be used for manual configuration. There may be more than one configuration
 * per one web application.
 * 
 * @see AutomagicMadvocConfigurator
 */
public interface MadvocConfigurator {

	/**
	 * Configures provided web application instance.
	 */
	void configure();
}
