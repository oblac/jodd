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

import java.util.function.Supplier;

/**
 * Simple Logger interface. It defines only logger methods with string
 * argument as our coding style and approach insist in always using if block
 * around the logging.
 */
public interface Logger {

	/**
	 * Logger level.
	 */
	enum Level {
		TRACE(1),
		DEBUG(2),
		INFO(3),
		WARN(4),
		ERROR(5);

		private final int value;
		Level(final int value) {
			this.value = value;
		}

		/**
		 * Returns <code>true</code> if this level
		 * is enabled for given required level.
		 */
		public boolean isEnabledFor(final Level level) {
			return this.value >= level.value;
		}
	}

	/**
	 * Returns the logger name.
	 */
	String getName();

	/**
	 * Returns <code>true</code> if certain logging
	 * level is enabled.
	 */
	boolean isEnabled(Level level);

	/**
	 * Logs a message at provided logging level.
	 */
	void log(Level level, String message);

	/**
	 * Logs a message at provided logging level.
	 */
	default void log(final Level level, final Supplier<String> messageSupplier) {
		if (isEnabled(level)) {
			log(level, messageSupplier.get());
		}
	}

	/**
	 * Logs a message and displays exception info.
	 */
	void log(Level level, String message, Throwable throwable);

	/**
	 * Logs a message and displays exception info.
	 */
	default void log(final Level level, final Supplier<String> messageSupplier, final Throwable throwable) {
		if (isEnabled(level)) {
			log(level, messageSupplier.get(), throwable);
		}
	}


	// ---------------------------------------------------------------- level

	/**
	 * Sets new level dynamically. Some implementations may not support it.
	 */
	void setLevel(Level level);

	// ---------------------------------------------------------------- trace

	/**
	 * Returns <code>true</code> if TRACE level is enabled.
	 */
	boolean isTraceEnabled();

	/**
	 * Logs a message at TRACE level.
	 */
	void trace(String message);

	/**
	 * Logs a message at TRACE level.
	 */
	default void trace(final Supplier<String> messageSupplier) {
		if (isTraceEnabled()) {
			trace(messageSupplier.get());
		}
	}

	// ---------------------------------------------------------------- debug

	/**
	 * Returns <code>true</code> if DEBUG level is enabled.
	 */
	boolean isDebugEnabled();

	/**
	 * Logs a message at DEBUG level.
	 */
	void debug(String message);

	/**
	 * Logs a message at DEBUG level.
	 */
	default void debug(final Supplier<String> messageSupplier) {
		if (isDebugEnabled()) {
			debug(messageSupplier.get());
		}
	}

	// ---------------------------------------------------------------- info
	/**
	 * Returns <code>true</code> if INFO level is enabled.
	 */
	boolean isInfoEnabled();

	/**
	 * Logs a message at INFO level.
	 */
	void info(String message);

	/**
	 * Logs a message at INFO level.
	 */
	default void info(final Supplier<String> messageSupplier) {
		if (isInfoEnabled()) {
			info(messageSupplier.get());
		}
	}

	// ---------------------------------------------------------------- warn

	/**
	 * Returns <code>true</code> if WARN level is enabled.
	 */
	boolean isWarnEnabled();

	/**
	 * Logs a message at WARN level.
	 */
	void warn(String message);

	/**
	 * Logs a message at WARN level.
	 */
	void warn(String message, Throwable throwable);

	/**
	 * Logs a message at WARN level.
	 */
	default void warn(final Supplier<String> messageSupplier) {
		if (isWarnEnabled()) {
			warn(messageSupplier.get());
		}
	}

	/**
	 * Logs a message at WARN level.
	 */
	default void warn(final Supplier<String> messageSupplier, final Throwable throwable) {
		if (isWarnEnabled()) {
			warn(messageSupplier.get(), throwable);
		}
	}

	// ---------------------------------------------------------------- error

	/**
	 * Returns <code>true</code> if ERROR level is enabled.
	 */
	boolean isErrorEnabled();

	/**
	 * Logs a message at ERROR level.
	 */
	void error(String message);

	/**
	 * Logs a message at ERROR level.
	 */
	void error(String message, Throwable throwable);

	/**
	 * Logs a message at ERROR level.
	 */
	default void error(final Supplier<String> messageSupplier) {
		if (isErrorEnabled()) {
			error(messageSupplier.get());
		}
	}

	/**
	 * Logs a message at ERROR level.
	 */
	default void error(final Supplier<String> messageSupplier, final Throwable throwable) {
		if (isErrorEnabled()) {
			error(messageSupplier.get(), throwable);
		}
	}

}