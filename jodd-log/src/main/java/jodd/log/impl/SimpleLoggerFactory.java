// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.log.impl;

import jodd.log.Logger;
import jodd.log.LoggerFactoryInterface;

/**
 * Factory for {@link jodd.log.impl.SimpleLogger}.
 */
public class SimpleLoggerFactory implements LoggerFactoryInterface {

	private final Logger.Level globalLevel;
	private final long startTime;

	public SimpleLoggerFactory(Logger.Level globalLevel) {
		this.globalLevel = globalLevel;
		this.startTime = System.currentTimeMillis();
	}

	/**
	 * Returns global level.
	 */
	public Logger.Level getLevel() {
		return globalLevel;
	}

	/**
	 * Returns elapsed time in milliseconds.
	 */
	public long getElapsedTime() {
		return System.currentTimeMillis() - startTime;
	}

	public Logger getLogger(String name) {
		return new SimpleLogger(this, name);
	}

	/**
	 * Returns called class.
	 */
	protected String getCallerClass() {
		Exception exception = new Exception();

		StackTraceElement[] stackTrace = exception.getStackTrace();

		for (StackTraceElement stackTraceElement : stackTrace) {
			String className = stackTraceElement.getClassName();
			if (className.equals(SimpleLoggerFactory.class.getName())) {
				continue;
			}
			if (className.equals(SimpleLogger.class.getName())) {
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
	protected String shortenClassName(String className) {
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