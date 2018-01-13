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
import org.slf4j.spi.LocationAwareLogger;

/**
 * SLF4J logger adapter.
 */
public class Slf4jLogger implements Logger {

	public static final LoggerProvider<Slf4jLogger> PROVIDER =
		name -> new Slf4jLogger(org.slf4j.LoggerFactory.getLogger(name));

	private static final String FQCN = Slf4jLogger.class.getName();

	final org.slf4j.Logger logger;
	private final LocationAwareLogger locationAwareLogger;

	public Slf4jLogger(final org.slf4j.Logger logger) {
		this.logger = logger;
		if (logger instanceof LocationAwareLogger) {
			locationAwareLogger = (LocationAwareLogger) logger;
		}
		else {
			locationAwareLogger = null;
		}
	}

	@Override
	public String getName() {
		return logger.getName();
	}

	@Override
	public boolean isEnabled(final Level level) {
		switch (level) {
			case TRACE: return logger.isTraceEnabled();
			case DEBUG: return logger.isDebugEnabled();
			case INFO: return logger.isInfoEnabled();
			case WARN: return logger.isWarnEnabled();
			case ERROR: return logger.isErrorEnabled();
			default:
				throw new IllegalArgumentException();
		}
	}

	@Override
	public void log(final Level level, final String message) {
		switch (level) {
			case TRACE: trace(message); break;
			case DEBUG: debug(message); break;
			case INFO: info(message); break;
			case WARN: warn(message); break;
			case ERROR: error(message); break;
		}
	}

	@Override
	public void log(final Level level, final String message, final Throwable throwable) {
		switch (level) {
			case TRACE: trace(message); break;
			case DEBUG: debug(message); break;
			case INFO: info(message); break;
			case WARN: warn(message, throwable); break;
			case ERROR: error(message, throwable); break;
		}
	}

	@Override
	public void setLevel(final Level level) {
		if (logger instanceof ch.qos.logback.classic.Logger) {
			ch.qos.logback.classic.Level l = null;

			switch (level) {
				case TRACE: l = ch.qos.logback.classic.Level.TRACE; break;
				case DEBUG: l = ch.qos.logback.classic.Level.DEBUG; break;
				case INFO: l = ch.qos.logback.classic.Level.INFO; break;
				case WARN: l = ch.qos.logback.classic.Level.WARN; break;
				case ERROR: l = ch.qos.logback.classic.Level.WARN; break;
			}

			((ch.qos.logback.classic.Logger)logger).setLevel(l);

			return;
		}

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isTraceEnabled() {
		return logger.isTraceEnabled();
	}

	@Override
	public void trace(final String message) {
		if (locationAwareLogger != null) {
			locationAwareLogger.log(
				null, FQCN, LocationAwareLogger.TRACE_INT, message, null, null);
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
	public void debug(final String message) {
		if (locationAwareLogger != null) {
			locationAwareLogger.log(
				null, FQCN, LocationAwareLogger.DEBUG_INT, message, null, null);
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
	public void info(final String message) {
		if (locationAwareLogger != null) {
			locationAwareLogger.log(
				null, FQCN, LocationAwareLogger.INFO_INT, message, null, null);
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
	public void warn(final String message) {
		if (locationAwareLogger != null) {
			locationAwareLogger.log(
				null, FQCN, LocationAwareLogger.WARN_INT, message, null, null);
		}
		else {
			logger.warn(message);
		}
	}

	@Override
	public void warn(final String message, final Throwable throwable) {
		if (locationAwareLogger != null) {
			locationAwareLogger.log(
				null, FQCN, LocationAwareLogger.WARN_INT, message, null, throwable);
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
	public void error(final String message) {
		if (locationAwareLogger != null) {
			locationAwareLogger.log(
				null, FQCN, LocationAwareLogger.ERROR_INT, message, null, null);
		}
		else {
			logger.error(message);
		}
	}

	@Override
	public void error(final String message, final Throwable throwable) {
		if (locationAwareLogger != null) {
			locationAwareLogger.log(
				null, FQCN, LocationAwareLogger.ERROR_INT, message, null, throwable);
		}
		else {
			logger.error(message, throwable);
		}
	}
}