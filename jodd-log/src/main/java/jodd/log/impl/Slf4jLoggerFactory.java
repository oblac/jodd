// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.log.impl;

import jodd.log.Logger;
import jodd.log.LoggerFactoryInterface;

/**
 * Logger factory for {@link jodd.log.impl.Slf4jLogger}.
 */
public class Slf4jLoggerFactory implements LoggerFactoryInterface {

	/**
	 * {@inheritDoc}
	 */
	public Logger getLogger(String name) {
		return new Slf4jLogger(org.slf4j.LoggerFactory.getLogger(name));
	}
}