// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.log;

import org.slf4j.Logger;
import org.slf4j.spi.LocationAwareLogger;

/**
 * Adapter for <code>slf4j</code> logger.
 */
public class Slf4jLog extends Log {

	private static final String FQCN = Slf4jLog.class.getName();

	private final Logger logger;
	private final LocationAwareLogger locationAwareLogger;

	public Slf4jLog(Logger logger) {
		super(logger.getName());
		this.logger = logger;
		if (logger instanceof LocationAwareLogger) {
			locationAwareLogger = (LocationAwareLogger) logger;
		} else {
			locationAwareLogger = null;
		}
	}

	@Override
	public boolean isTraceEnabled() {
		return logger.isTraceEnabled();
	}

	@Override
	public void trace(String message) {
		if (locationAwareLogger != null) {
			locationAwareLogger.log(null, FQCN, LocationAwareLogger.TRACE_INT, message, null, null);
		} else {
			logger.trace(message);
		}
	}

	@Override
	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	@Override
	public void debug(String message) {
		if (locationAwareLogger != null) {
			locationAwareLogger.log(null, FQCN, LocationAwareLogger.DEBUG_INT, message, null, null);
		} else {
			logger.debug(message);
		}
	}

	@Override
	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	@Override
	public void info(String message) {
		if (locationAwareLogger != null) {
			locationAwareLogger.log(null, FQCN, LocationAwareLogger.INFO_INT, message, null, null);
		} else {
			logger.info(message);
		}
	}

	@Override
	public boolean isWarnEnabled() {
		return logger.isWarnEnabled();
	}

	@Override
	public void warn(String message) {
		if (locationAwareLogger != null) {
			locationAwareLogger.log(null, FQCN, LocationAwareLogger.WARN_INT, message, null, null);
		} else {
			logger.warn(message);
		}
	}

	@Override
	public void warn(String message, Throwable throwable) {
		if (locationAwareLogger != null) {
			locationAwareLogger.log(null, FQCN, LocationAwareLogger.WARN_INT, message, null, throwable);
		} else {
			logger.warn(message, throwable);
		}
	}

	@Override
	public boolean isErrorEnabled() {
		return logger.isErrorEnabled();
	}

	@Override
	public void error(String message) {
		if (locationAwareLogger != null) {
			locationAwareLogger.log(null, FQCN, LocationAwareLogger.ERROR_INT, message, null, null);
		} else {
			logger.error(message);
		}
	}

	@Override
	public void error(String message, Throwable throwable) {
		if (locationAwareLogger != null) {
			locationAwareLogger.log(null, FQCN, LocationAwareLogger.ERROR_INT, message, null, throwable);
		} else {
			logger.error(message, throwable);
		}
	}
}
