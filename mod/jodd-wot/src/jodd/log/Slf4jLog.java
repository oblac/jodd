// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.log;

import org.slf4j.Logger;

/**
 * Adapter for <code>slf4j</code> logger.
 */
public class Slf4jLog extends Log {

	private final Logger logger;

	public Slf4jLog(Logger logger) {
		super(logger.getName());
		this.logger = logger;
	}

	@Override
	public boolean isTraceEnabled() {
		return logger.isTraceEnabled();
	}

	@Override
	public void trace(String message) {
		logger.trace(message);
	}

	@Override
	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	@Override
	public void debug(String message) {
		logger.debug(message);
	}

	@Override
	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	@Override
	public void info(String message) {
		logger.info(message);
	}

	@Override
	public boolean isWarnEnabled() {
		return logger.isWarnEnabled();
	}

	@Override
	public void warn(String message) {
		logger.warn(message);
	}

	@Override
	public void warn(String message, Throwable throwable) {
		logger.warn(message, throwable);
	}

	@Override
	public boolean isErrorEnabled() {
		return logger.isErrorEnabled();
	}

	@Override
	public void error(String message) {
		logger.error(message);
	}

	@Override
	public void error(String message, Throwable throwable) {
		logger.error(message, throwable);
	}
}
