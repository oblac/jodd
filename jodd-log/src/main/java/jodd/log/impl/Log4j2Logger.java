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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.spi.AbstractLogger;

/**
 * Log4j2 logger.
 */
public class Log4j2Logger implements Logger {

	public static final LoggerProvider<Log4j2Logger> PROVIDER =
		name -> new Log4j2Logger(LogManager.getLogger(name));

	private static final String FQCN = Log4j2Logger.class.getName();

	final org.apache.logging.log4j.Logger logger;
	private final AbstractLogger abstractLogger;

	public Log4j2Logger(org.apache.logging.log4j.Logger logger) {
		this.logger = logger;
		if (logger instanceof AbstractLogger) {
			abstractLogger = (AbstractLogger) logger;
		}
		else {
			abstractLogger = null;
		}
	}

	/**
	 * Converts Jodd logging level to JDK.
	 */
	private org.apache.logging.log4j.Level jodd2log4j2(Logger.Level level) {
		switch (level) {
			case TRACE: return org.apache.logging.log4j.Level.TRACE;
			case DEBUG: return org.apache.logging.log4j.Level.DEBUG;
			case INFO:	return org.apache.logging.log4j.Level.INFO;
			case WARN:	return org.apache.logging.log4j.Level.WARN;
			case ERROR:	return org.apache.logging.log4j.Level.ERROR;
			default:
				throw new IllegalArgumentException();
		}
	}

	@Override
	public String getName() {
		return logger.getName();
	}

	@Override
	public boolean isEnabled(Logger.Level level) {
		return logger.isEnabled(jodd2log4j2(level));
	}

	@Override
	public void log(Logger.Level level, String message) {
		if (abstractLogger != null) {
			abstractLogger.logIfEnabled(FQCN, jodd2log4j2(level), null, message);
		}
		else {
			logger.log(jodd2log4j2(level), message);
		}
	}
	@Override
	public void log(Logger.Level level, String message, Throwable throwable) {
		if (abstractLogger != null) {
			abstractLogger.logIfEnabled(FQCN, jodd2log4j2(level), null, message, throwable);
		}
		else {
			logger.log(jodd2log4j2(level), message, throwable);
		}
	}

	@Override
	public void setLevel(Logger.Level level) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isTraceEnabled() {
		return logger.isTraceEnabled();
	}

	@Override
	public void trace(String message) {
		if (abstractLogger != null) {
			abstractLogger.logIfEnabled(FQCN, org.apache.logging.log4j.Level.TRACE, null, message);
		}
		else {
			logger.trace(message);
		}
	}

	@Override
	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	@Override
	public void debug(String message) {
		if (abstractLogger != null) {
			abstractLogger.logIfEnabled(FQCN, org.apache.logging.log4j.Level.DEBUG, null, message);
		}
		else {
			logger.debug(message);
		}
	}

	@Override
	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	@Override
	public void info(String message) {
		if (abstractLogger != null) {
			abstractLogger.logIfEnabled(FQCN, org.apache.logging.log4j.Level.INFO, null, message);
		}
		else {
			logger.info(message);
		}
	}

	@Override
	public boolean isWarnEnabled() {
		return logger.isWarnEnabled();
	}

	@Override
	public void warn(String message) {
		if (abstractLogger != null) {
			abstractLogger.logIfEnabled(FQCN, org.apache.logging.log4j.Level.WARN, null, message);
		}
		else {
			logger.warn(message);
		}
	}

	@Override
	public void warn(String message, Throwable throwable) {
		if (abstractLogger != null) {
			abstractLogger.logIfEnabled(FQCN, org.apache.logging.log4j.Level.WARN, null, message, throwable);
		}
		else {
			logger.warn(message, throwable);
		}
	}

	@Override
	public boolean isErrorEnabled() {
		return logger.isErrorEnabled();
	}

	@Override
	public void error(String message) {
		if (abstractLogger != null) {
			abstractLogger.logIfEnabled(FQCN, org.apache.logging.log4j.Level.ERROR, null, message);
		}
		else {
			logger.error(message);
		}
	}

	@Override
	public void error(String message, Throwable throwable) {
		if (abstractLogger != null) {
			abstractLogger.logIfEnabled(FQCN, org.apache.logging.log4j.Level.ERROR, null, message, throwable);
		}
		else {
			logger.error(message, throwable);
		}
	}

}
