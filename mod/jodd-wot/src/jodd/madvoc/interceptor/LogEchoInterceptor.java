// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.interceptor;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Echo interceptor that outputs to logger.
 */
public class LogEchoInterceptor extends EchoInterceptor {

	private static final Logger log = LoggerFactory.getLogger(LogEchoInterceptor.class);

	@Override
	protected void out(String message) {
		log.debug(message);
	}
}
