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
 * Dummy logger.
 */
public class NOPLogger implements Logger {

	private static final NOPLogger INSTANCE = new NOPLogger("*");

	public static final LoggerProvider<NOPLogger> PROVIDER = name -> INSTANCE;

	private final String name;

	public NOPLogger(final String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isEnabled(final Level level) {
		return false;
	}

	@Override
	public void log(final Level level, final String message) {
	}

	@Override
	public void log(final Level level, final String message, final Throwable throwable) {
	}

	@Override
	public void setLevel(final Level level) {
	}

	@Override
	public boolean isTraceEnabled() {
		return false;
	}

	@Override
	public void trace(final String message) {

	}

	@Override
	public boolean isDebugEnabled() {
		return false;
	}

	@Override
	public void debug(final String message) {
	}

	@Override
	public boolean isInfoEnabled() {
		return false;
	}

	@Override
	public void info(final String message) {
	}

	@Override
	public boolean isWarnEnabled() {
		return false;
	}

	@Override
	public void warn(final String message) {
	}

	@Override
	public void warn(final String message, final Throwable throwable) {
	}

	@Override
	public boolean isErrorEnabled() {
		return false;
	}

	@Override
	public void error(final String message) {
	}

	@Override
	public void error(final String message, final Throwable throwable) {
	}

}