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