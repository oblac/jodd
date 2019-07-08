// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.log.impl;

import jodd.log.Logger;
import jodd.log.LoggerProvider;

/**
 * JDK logger.
 */
public class JDKLogger implements Logger {

	public static final LoggerProvider<JDKLogger> PROVIDER =
		name -> new JDKLogger(java.util.logging.Logger.getLogger(name));

	final java.util.logging.Logger logger;

	public JDKLogger(final java.util.logging.Logger logger) {
		this.logger = logger;
	}

	/**
	 * Converts Jodd logging level to JDK.
	 */
	private java.util.logging.Level jodd2jdk(final Level level) {
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

	@Override
	public String getName() {
		return logger.getName();
	}

	@Override
	public boolean isEnabled(final Level level) {
		return logger.isLoggable(jodd2jdk(level));
	}

	@Override
	public void log(final Level level, final String message) {
		logger.log(jodd2jdk(level), message);
	}

	@Override
	public void log(final Level level, final String message, final Throwable throwable) {
		logger.log(jodd2jdk(level), message, throwable);
	}

	@Override
	public void setLevel(final Level level) {
		logger.setLevel(jodd2jdk(level));
	}

	@Override
	public boolean isTraceEnabled() {
		return logger.isLoggable(java.util.logging.Level.FINER);
	}

	@Override
	public void trace(final String message) {
		logger.log(java.util.logging.Level.FINER, message);
	}

	@Override
	public boolean isDebugEnabled() {
		return logger.isLoggable(java.util.logging.Level.FINE);
	}

	@Override
	public void debug(final String message) {
		logger.log(java.util.logging.Level.FINE, message);
	}

	@Override
	public boolean isInfoEnabled() {
		return logger.isLoggable(java.util.logging.Level.INFO);
	}

	@Override
	public void info(final String message) {
		logger.log(java.util.logging.Level.INFO, message);
	}

	@Override
	public boolean isWarnEnabled() {
		return logger.isLoggable(java.util.logging.Level.WARNING);
	}

	@Override
	public void warn(final String message) {
		logger.log(java.util.logging.Level.WARNING, message);
	}

	@Override
	public void warn(final String message, final Throwable throwable) {
		logger.log(java.util.logging.Level.WARNING, message, throwable);
	}

	@Override
	public boolean isErrorEnabled() {
		return logger.isLoggable(java.util.logging.Level.SEVERE);
	}

	@Override
	public void error(final String message) {
		logger.log(java.util.logging.Level.SEVERE, message);
	}

	@Override
	public void error(final String message, final Throwable throwable) {
		logger.log(java.util.logging.Level.SEVERE, message, throwable);
	}

}