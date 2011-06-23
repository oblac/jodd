// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.interceptor;

import jodd.log.Log;

/**
 * Echo interceptor that outputs to logger.
 */
public class LogEchoInterceptor extends EchoInterceptor {

	private static final Log log = Log.getLogger(LogEchoInterceptor.class);

	@Override
	protected void out(String message) {
		log.debug(message);
	}
}
