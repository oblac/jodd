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

package jodd.log;

/**
 * Simple Logger interface. It defines only logger methods with string
 * argument as our coding style and approach insist in always using if block
 * around the logging.
 */
public interface Logger {

	/**
	 * Logger level.
	 */
	public enum Level {
		TRACE(1),
		DEBUG(2),
		INFO(3),
		WARN(4),
		ERROR(5);

		private final int value;
		Level(int value) {
			this.value = value;
		}

		/**
		 * Returns <code>true</code> if this level
		 * is enabled for given required level.
		 */
		public boolean isEnabledFor(Level level) {
			return this.value >= level.value;
		}
	}

	/**
	 * Returns Logger name.
	 */
	public String getName();

	/**
	 * Returns <code>true</code> if certain logging
	 * level is enabled.
	 */
	public boolean isEnabled(Level level);

	/**
	 * Logs a message at provided logging level.
	 */
	public void log(Level level, String message);


	// ---------------------------------------------------------------- trace

	/**
	 * Returns <code>true</code> if TRACE level is enabled.
	 */
	public boolean isTraceEnabled();

	/**
	 * Logs a message at TRACE level.
	 */
	public void trace(String message);

	// ---------------------------------------------------------------- debug

	/**
	 * Returns <code>true</code> if DEBUG level is enabled.
	 */
	public boolean isDebugEnabled();

	/**
	 * Logs a message at DEBUG level.
	 */
	public void debug(String message);

	// ---------------------------------------------------------------- info
	/**
	 * Returns <code>true</code> if INFO level is enabled.
	 */
	public boolean isInfoEnabled();

	/**
	 * Logs a message at INFO level.
	 */
	public void info(String message);

	// ---------------------------------------------------------------- warn

	/**
	 * Returns <code>true</code> if WARN level is enabled.
	 */
	public boolean isWarnEnabled();

	/**
	 * Logs a message at WARN level.
	 */
	public void warn(String message);

	/**
	 * Logs a message at WARN level.
	 */
	public void warn(String message, Throwable throwable);

	// ---------------------------------------------------------------- error

	/**
	 * Returns <code>true</code> if ERROR level is enabled.
	 */
	public boolean isErrorEnabled();

	/**
	 * Logs a message at ERROR level.
	 */
	public void error(String message);

	/**
	 * Logs a message at ERROR level.
	 */
	public void error(String message, Throwable throwable);

}