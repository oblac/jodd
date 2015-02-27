// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.log.impl;

import jodd.log.Logger;
import jodd.log.LoggerFactoryInterface;

/**
 * Logger factory for dummy logger. Shares same instance of
 * {@link jodd.log.impl.NOPLogger} across all loggers.
 */
public class NOPLoggerFactory implements LoggerFactoryInterface {

	private NOPLogger logger = new NOPLogger("*");

	/**
	 * {@inheritDoc}
	 */
	public Logger getLogger(String name) {
		return logger;
	}
}