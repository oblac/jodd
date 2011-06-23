// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.log;

import org.slf4j.LoggerFactory;

/**
 * Slf4j factory.
 */
public class Slf4jLogFactory extends LogFactory {

	@Override
	public Log getLogger(String name) {
		return new Slf4jLog(LoggerFactory.getLogger(name));
	}
}
