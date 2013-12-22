// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.log.impl;

import jodd.log.Logger;
import jodd.log.LoggerFactoryInterface;

/**
 * Factory for {@link jodd.log.impl.JCLLogger}.
 */
public class JCLLoggerFactory implements LoggerFactoryInterface {
	public Logger getLogger(String name) {
		return new JCLLogger(org.apache.commons.logging.LogFactory.getLog(name));
	}
}