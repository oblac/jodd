// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.log;

/**
 * Log factory creates {@link Log} instances.
 */
public abstract class LogFactory {

	protected static LogFactory implementation = new DefaultLogFactory();

	/**
	 * Specifies log factory implementation.
	 */
	public static void setImplementation(LogFactory logFactory) {
		implementation = logFactory;
	}

	/**
	 * Returns log instance for given name.
	 */
	public abstract Log getLogger(String name);

}
