// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.log;

/**
 * Simple logger that outputs to console.
 */
public class ConsoleLog extends Log {

	public enum Level {
		TRACE(1), DEBUG(2), INFO(3), WARN(4), ERROR(5);

		private final int level;
		private Level(int level) {
			this.level = level;
		}

		/**
		 * Returns <code>true</code> if required level is enabled.
		 */
		public boolean isEnabled(Level someLevel) {
			return level >= someLevel.level;
		}
	}

	/**
	 * Returns log factory that creates <code>ConsoleLog</code> instances.
	 */
	public static LogFactory getFactory(final Level currentLevel) {
		return new LogFactory() {
			@Override
			public Log getLogger(String name) {
				return new ConsoleLog(name, currentLevel);
			}
		};
	}

	public ConsoleLog(String name, Level logLevel) {
		super(name);
		this.logLevel = logLevel;
	}

	protected Level logLevel = Level.INFO;

	@Override
	public boolean isTraceEnabled() {
		return logLevel.isEnabled(Level.TRACE);
	}

	@Override
	public void trace(String message) {
		System.out.println("TRACE [" + getName() + "]: " + message);
	}

	@Override
	public boolean isDebugEnabled() {
		return logLevel.isEnabled(Level.DEBUG);
	}

	@Override
	public void debug(String message) {
		System.out.println("DEBUG [" + getName() + "]: " + message);
	}

	@Override
	public boolean isInfoEnabled() {
		return logLevel.isEnabled(Level.INFO);
	}

	@Override
	public void info(String message) {
		System.out.println("INFO  [" + getName() + "]: " + message);
	}

	@Override
	public boolean isWarnEnabled() {
		return logLevel.isEnabled(Level.WARN);
	}

	@Override
	public void warn(String message) {
		System.out.println("WARN  [" + getName() + "]: " + message);
	}

	@Override
	public void warn(String message, Throwable throwable) {
		System.out.println("WARN  [" + getName() + "]: " + message);
		throwable.printStackTrace();
	}

	@Override
	public boolean isErrorEnabled() {
		return logLevel.isEnabled(Level.ERROR);
	}

	@Override
	public void error(String message) {
		System.out.println("ERROR [" + getName() + "]: " + message);
	}

	@Override
	public void error(String message, Throwable throwable) {
		System.out.println("ERROR [" + getName() + "]: " + message);
		throwable.printStackTrace();
	}

}