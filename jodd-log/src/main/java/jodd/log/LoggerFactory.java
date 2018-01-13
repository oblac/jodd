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

import jodd.log.impl.NOPLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Logger factory.
 */
public final class LoggerFactory {

	static {
		setLoggerProvider(NOPLogger.PROVIDER);
	}

	private static Function<String, Logger> loggerProvider;

	private static Map<String, Logger> loggers = new HashMap<>();

	/**
	 * Sets {@link LoggerProvider} instance used for creating new {@link Logger}s.
	 */
	public static void setLoggerProvider(final LoggerProvider loggerProvider) {
		LoggerFactory.loggerProvider = loggerProvider::createLogger;
		if (loggers != null) {
			loggers.clear();
		}
	}

	/**
	 * Returns logger for given class by simply using the class name.
	 * @see #getLogger(String)
	 */
	public static Logger getLogger(final Class clazz) {
		return getLogger(clazz.getName());
	}

	/**
	 * Enables cache. Previous cache is removed.
	 */
	public static void enableCache() {
		loggers = new HashMap<>();
	}

	/**
	 * Disables the cache.
	 */
	public static void disableCache() {
		loggers = null;
	}

	/**
	 * Returns logger for given name. Repeated calls to this method with the
	 * same argument should return the very same instance of the logger.
	 */
	public static Logger getLogger(final String name) {
		if (loggers == null) {
			return loggerProvider.apply(name);
		}
		return loggers.computeIfAbsent(name, loggerProvider);
	}

}