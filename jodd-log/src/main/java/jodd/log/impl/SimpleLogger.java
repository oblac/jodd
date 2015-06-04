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
		return level.isEnabledFor(slf.getLevel());
	}

	public void log(Level level, String message) {
		print(level, message, null);
	}

	public boolean isTraceEnabled() {
		return Level.TRACE.isEnabledFor(slf.getLevel());
	}

	public void trace(String message) {
		print(Level.TRACE, message, null);
	}

	public boolean isDebugEnabled() {
		return Level.DEBUG.isEnabledFor(slf.getLevel());
	}

	public void debug(String message) {
		print(Level.DEBUG, message, null);
	}

	public boolean isInfoEnabled() {
		return Level.INFO.isEnabledFor(slf.getLevel());
	}

	public void info(String message) {
		print(Level.INFO, message, null);
	}

	public boolean isWarnEnabled() {
		return Level.WARN.isEnabledFor(slf.getLevel());
	}

	public void warn(String message) {
		print(Level.WARN, message, null);
	}

	public void warn(String message, Throwable throwable) {
		print(Level.WARN, message, throwable);
	}

	public boolean isErrorEnabled() {
		return Level.ERROR.isEnabledFor(slf.getLevel());
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