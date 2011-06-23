// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.log;

/**
 * Default log implementation.
 */
public abstract class Log {

	public static Log getLogger(Class clazz) {
		return LogFactory.implementation.getLogger(clazz.getName());
	}

	public static Log getLogger(String loggerName) {
		return LogFactory.implementation.getLogger(loggerName);
	}

	// ---------------------------------------------------------------- name

	protected final String name;

	protected Log(String name) {
		this.name = name;
	}

	/**
	 * Returns logger name.
	 */
	public final String getName() {
		return name;
	}

	// ---------------------------------------------------------------- trace

	public abstract boolean isTraceEnabled();

	public abstract void trace(String message);

	// ---------------------------------------------------------------- debug

	public abstract boolean isDebugEnabled();

	public abstract void debug(String message);

	// ---------------------------------------------------------------- info

	public abstract boolean isInfoEnabled();

	public abstract void info(String message);

	// ---------------------------------------------------------------- warn

	public abstract boolean isWarnEnabled();

	public abstract void warn(String message);

	public abstract void warn(String message, Throwable throwable);

	// ---------------------------------------------------------------- error

	public abstract boolean isErrorEnabled();

	public abstract void error(String message);

	public abstract void error(String message, Throwable throwable);

}
