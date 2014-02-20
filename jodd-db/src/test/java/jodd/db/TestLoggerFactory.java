// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db;


import jodd.exception.UncheckedException;
import jodd.log.Logger;
import jodd.log.impl.NOPLogger;
import jodd.log.impl.NOPLoggerFactory;

public class TestLoggerFactory extends NOPLoggerFactory {

	private static NOPLogger NOP_LOGGER = new NOPLogger("") {
		@Override
		public boolean isWarnEnabled() {
			return true;
		}

		@Override
		public void warn(String message) {
			throw new UncheckedException("NO WARNING ALLOWED: " + message);
		}

		@Override
		public void warn(String message, Throwable throwable) {
			throw new UncheckedException("NO WARNING ALLOWED: " + message);
		}
	};

	@Override
	public Logger getLogger(String name) {
		return NOP_LOGGER;
	}
}