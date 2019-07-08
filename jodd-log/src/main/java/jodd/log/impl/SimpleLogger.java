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
 * Simple logger.
 */
public class SimpleLogger implements Logger {

	public static final LoggerProvider<SimpleLogger> PROVIDER = new SimpleLoggerProvider();

	private final String name;
	private Level level;
	private final SimpleLoggerProvider slf;

	public SimpleLogger(final SimpleLoggerProvider simpleLoggerProvider, final String name, final Level defaultLevel) {
		this.name = name;
		this.slf = simpleLoggerProvider;
		this.level = defaultLevel;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isEnabled(final Level level) {
		return level.isEnabledFor(this.level);
	}

	@Override
	public void log(final Level level, final String message) {
		print(level, message, null);
	}

	@Override
	public void log(final Level level, final String message, final Throwable throwable) {
		print(level, message, throwable);
	}

	@Override
	public void setLevel(final Level level) {
		this.level = level;
	}

	@Override
	public boolean isTraceEnabled() {
		return Level.TRACE.isEnabledFor(level);
	}

	@Override
	public void trace(final String message) {
		print(Level.TRACE, message, null);
	}

	@Override
	public boolean isDebugEnabled() {
		return Level.DEBUG.isEnabledFor(level);
	}

	@Override
	public void debug(final String message) {
		print(Level.DEBUG, message, null);
	}

	@Override
	public boolean isInfoEnabled() {
		return Level.INFO.isEnabledFor(level);
	}

	@Override
	public void info(final String message) {
		print(Level.INFO, message, null);
	}

	@Override
	public boolean isWarnEnabled() {
		return Level.WARN.isEnabledFor(level);
	}

	@Override
	public void warn(final String message) {
		print(Level.WARN, message, null);
	}

	@Override
	public void warn(final String message, final Throwable throwable) {
		print(Level.WARN, message, throwable);
	}

	@Override
	public boolean isErrorEnabled() {
		return Level.ERROR.isEnabledFor(level);
	}

	@Override
	public void error(final String message) {
		print(Level.ERROR, message, null);
	}

	@Override
	public void error(final String message, final Throwable throwable) {
		print(Level.ERROR, message, throwable);
	}

	/**
	 * Prints error message if level is enabled.
	 */
	protected void print(final Level level, final String message, final Throwable throwable) {
		if (!isEnabled(level)) {
			return;
		}

		StringBuilder msg = new StringBuilder()
			.append(slf.getElapsedTime()).append(' ').append('[')
			.append(level).append(']').append(' ')
			.append(getCallerClass()).append(' ').append('-')
			.append(' ').append(message);

		System.out.println(msg.toString());

		if (throwable != null) {
			throwable.printStackTrace(System.out);
		}
	}

	/**
	 * Returns called class.
	 */
	protected String getCallerClass() {
		Exception exception = new Exception();

		StackTraceElement[] stackTrace = exception.getStackTrace();

		for (StackTraceElement stackTraceElement : stackTrace) {
			String className = stackTraceElement.getClassName();
			if (className.equals(SimpleLoggerProvider.class.getName())) {
				continue;
			}
			if (className.equals(SimpleLogger.class.getName())) {
				continue;
			}
			if (className.equals(Logger.class.getName())) {
				continue;
			}
			return shortenClassName(className)
				+ '.' + stackTraceElement.getMethodName()
				+ ':' + stackTraceElement.getLineNumber();
		}
		return "N/A";
	}

	/**
	 * Returns shorten class name.
	 */
	protected String shortenClassName(final String className) {
		int lastDotIndex = className.lastIndexOf('.');
		if (lastDotIndex == -1) {
			return className;
		}

		StringBuilder shortClassName = new StringBuilder(className.length());

		int start = 0;
		while(true) {
			shortClassName.append(className.charAt(start));

			int next = className.indexOf('.', start);
			if (next == lastDotIndex) {
				break;
			}
			start = next + 1;
			shortClassName.append('.');
		}
		shortClassName.append(className.substring(lastDotIndex));

		return shortClassName.toString();
	}

}