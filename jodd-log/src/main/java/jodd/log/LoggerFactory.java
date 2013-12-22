// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.log;

import jodd.log.impl.NOPLoggerFactory;

/**
 * Logger factory.
 */
public final class LoggerFactory {

	private static LoggerFactoryInterface loggerFactory = new NOPLoggerFactory();

	/**
	 * Sets logger factory implementation.
	 */
	public static void setLoggerFactory(LoggerFactoryInterface loggerFactoryInterface) {
		loggerFactory = loggerFactoryInterface;
	}

	/**
	 * Returns logger for given class.
	 */
	public static Logger getLogger(Class clazz) {
		return getLogger(clazz.getName());
	}

	/**
	 * Returns logger for given name.
	 */
	public static Logger getLogger(String name) {
		return loggerFactory.getLogger(name);
	}

}