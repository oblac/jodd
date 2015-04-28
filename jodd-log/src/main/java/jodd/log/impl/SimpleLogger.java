// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.log.impl;

import jodd.log.Logger;

/**
 * Simple logger.
 */
public class SimpleLogger implements Logger {

	private final String name;
	private final SimpleLoggerFactory slf;

	public SimpleLogger(SimpleLoggerFactory simpleLoggerFactory, String name) {
		this.name = name;
		this.slf = simpleLoggerFactory;
	}

	public String getName() {
		return name;
	}

	public boolean isEnabled(Level level) {
		return slf.getLevel().isEnabledFor(level);
	}

	public void log(Level level, String message) {
		print(level, message, null);
	}

	public boolean isTraceEnabled() {
		return slf.getLevel().isEnabledFor(Level.TRACE);
	}

	public void trace(String message) {
		print(Level.TRACE, message, null);
	}

	public boolean isDebugEnabled() {
		return slf.getLevel().isEnabledFor(Level.DEBUG);
	}

	public void debug(String message) {
		print(Level.DEBUG, message, null);
	}

	public boolean isInfoEnabled() {
		return slf.getLevel().isEnabledFor(Level.INFO);
	}

	public void info(String message) {
		print(Level.INFO, message, null);
	}

	public boolean isWarnEnabled() {
		return slf.getLevel().isEnabledFor(Level.WARN);
	}

	public void warn(String message) {
		print(Level.WARN, message, null);
	}

	public void warn(String message, Throwable throwable) {
		print(Level.WARN, message, throwable);
	}

	public boolean isErrorEnabled() {
		return slf.getLevel().isEnabledFor(Level.ERROR);
	}

	public void error(String message) {
		print(Level.ERROR, message, null);
	}

	public void error(String message, Throwable throwable) {
		print(Level.ERROR, message, throwable);
	}

	/**
	 * Prints error message.
	 */
	protected void print(Level level, String message, Throwable throwable) {
		if (isEnabled(level) == false) {
			return;
		}

		StringBuilder msg = new StringBuilder();
		msg.append(slf.getElapsedTime());
		msg.append(' ');
		msg.append('[');
		msg.append(level.toString());
		msg.append(']');
		msg.append(' ');
		msg.append(slf.getCallerClass());
		msg.append(' ');
		msg.append('-');
		msg.append(' ');
		msg.append(message);

		System.out.println(msg.toString());

		if (throwable != null) {
			throwable.printStackTrace(System.out);
		}
	}
}
