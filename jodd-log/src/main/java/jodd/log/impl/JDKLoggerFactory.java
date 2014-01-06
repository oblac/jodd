// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.log.impl;

import jodd.log.Logger;
import jodd.log.LoggerFactoryInterface;

/**
 * Factory for {@link jodd.log.impl.JDKLogger}.
 */
public class JDKLoggerFactory implements LoggerFactoryInterface {

	public Logger getLogger(String name) {
		return new JDKLogger(java.util.logging.Logger.getLogger(name));
	}
}