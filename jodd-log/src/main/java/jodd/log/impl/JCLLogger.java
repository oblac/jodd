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
import org.apache.commons.logging.Log;

/**
 * Java Commons logging logger.
 */
public class JCLLogger implements Logger {

	public static final LoggerProvider<JCLLogger> PROVIDER =
		name -> new JCLLogger(org.apache.commons.logging.LogFactory.getLog(name));

	final Log logger;

	public JCLLogger(final Log log) {
		this.logger = log;
	}

	@Override
	public String getName() {
		return logger.toString();
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
			case TRACE: logger.trace(message); break;
			case DEBUG: logger.debug(message); break;
			case INFO: logger.info(message); break;
			case WARN: logger.warn(message); break;
			case ERROR: logger.error(message); break;
		}
	}

	@Override
	public void log(final Level level, final String message, final Throwable throwable) {
		switch (level) {
			case TRACE: logger.trace(message, throwable); break;
			case DEBUG: logger.debug(message, throwable); break;
			case INFO: logger.info(message, throwable); break;
			case WARN: logger.warn(message, throwable); break;
			case ERROR: logger.error(message, throwable); break;
		}
	}

	@Override
	public void setLevel(final Level level) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isTraceEnabled() {
		return logger.isTraceEnabled();
	}

	@Override
	public void trace(final String message) {
		logger.trace(message);
	}

	@Override
	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	@Override
	public void debug(final String message) {
		logger.debug(message);
	}

	@Override
	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	@Override
	public void info(final String message) {
		logger.info(message);
	}

	@Override
	public boolean isWarnEnabled() {
		return logger.isWarnEnabled();
	}

	@Override
	public void warn(final String message) {
		logger.warn(message);
	}

	@Override
	public void warn(final String message, final Throwable throwable) {
		logger.warn(message, throwable);
	}

	@Override
	public boolean isErrorEnabled() {
		return logger.isErrorEnabled();
	}

	@Override
	public void error(final String message) {
		logger.error(message);
	}

	@Override
	public void error(final String message, final Throwable throwable) {
		logger.error(message, throwable);
	}
}