// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.log;

/**
 * Default logger that does nothing.
 */
public class DummyLog extends Log {

	public DummyLog(String name) {
		super(name);
	}

	@Override
	public boolean isTraceEnabled() {
		return false;
	}

	@Override
	public void trace(String message) {
	}

	@Override
	public boolean isDebugEnabled() {
		return false;
	}

	@Override
	public void debug(String message) {
	}

	@Override
	public boolean isInfoEnabled() {
		return false;
	}

	@Override
	public void info(String message) {
	}

	@Override
	public boolean isWarnEnabled() {
		return false;
	}

	@Override
	public void warn(String message) {
	}

	@Override
	public void warn(String message, Throwable throwable) {
	}

	@Override
	public boolean isErrorEnabled() {
		return false;
	}

	@Override
	public void error(String message) {
	}

	@Override
	public void error(String message, Throwable throwable) {
	}

}
