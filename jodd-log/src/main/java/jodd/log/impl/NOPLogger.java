// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.log.impl;

import jodd.log.Logger;

/**
 * Dummy logger.
 */
public class NOPLogger implements Logger {

	private final String name;

	public NOPLogger(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public boolean isEnabled(Level level) {
		return false;
	}

	public void log(Level level, String message) {
	}

	public boolean isTraceEnabled() {
		return false;
	}

	public void trace(String message) {

	}

	public boolean isDebugEnabled() {
		return false;
	}

	public void debug(String message) {
	}

	public boolean isInfoEnabled() {
		return false;
	}

	public void info(String message) {
	}

	public boolean isWarnEnabled() {
		return false;
	}

	public void warn(String message) {
	}

	public void warn(String message, Throwable throwable) {
	}

	public boolean isErrorEnabled() {
		return false;
	}

	public void error(String message) {
	}

	public void error(String message, Throwable throwable) {
	}

}