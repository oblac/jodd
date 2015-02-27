// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.log.impl;

import jodd.log.Logger;

/**
 * JDK logger.
 */
public class JDKLogger implements Logger {

	private final java.util.logging.Logger logger;

	public JDKLogger(java.util.logging.Logger logger) {
		this.logger = logger;
	}

	/**
	 * Converts Jodd logging level to JDK.
	 */
	private java.util.logging.Level jodd2jdk(Level level) {
		switch (level) {
			case TRACE: return java.util.logging.Level.FINER;
			case DEBUG: return java.util.logging.Level.FINE;
			case INFO:	return java.util.logging.Level.INFO;
			case WARN:	return java.util.logging.Level.WARNING;
			case ERROR:	return java.util.logging.Level.SEVERE;
			default:
				throw new IllegalArgumentException();
		}
	}

	public String getName() {
		return logger.getName();
	}

	public boolean isEnabled(Level level) {
		return logger.isLoggable(jodd2jdk(level));
	}

	public void log(Level level, String message) {
		logger.log(jodd2jdk(level), message);
	}

	public boolean isTraceEnabled() {
		return logger.isLoggable(java.util.logging.Level.FINER);
	}

	public void trace(String message) {
		logger.log(java.util.logging.Level.FINER, message);
	}

	public boolean isDebugEnabled() {
		return logger.isLoggable(java.util.logging.Level.FINE);
	}

	public void debug(String message) {
		logger.log(java.util.logging.Level.FINE, message);
	}

	public boolean isInfoEnabled() {
		return logger.isLoggable(java.util.logging.Level.INFO);
	}

	public void info(String message) {
		logger.log(java.util.logging.Level.INFO, message);
	}

	public boolean isWarnEnabled() {
		return logger.isLoggable(java.util.logging.Level.WARNING);
	}

	public void warn(String message) {
		logger.log(java.util.logging.Level.WARNING, message);
	}

	public void warn(String message, Throwable throwable) {
		logger.log(java.util.logging.Level.WARNING, message, throwable);
	}

	public boolean isErrorEnabled() {
		return logger.isLoggable(java.util.logging.Level.SEVERE);
	}

	public void error(String message) {
		logger.log(java.util.logging.Level.SEVERE, message);
	}

	public void error(String message, Throwable throwable) {
		logger.log(java.util.logging.Level.SEVERE, message, throwable);
	}

}