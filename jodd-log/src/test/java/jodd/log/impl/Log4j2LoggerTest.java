package jodd.log.impl;

import jodd.log.Logger;
import jodd.log.impl.fixtures.LoggerConstants;
import org.apache.logging.log4j.Level;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class Log4j2LoggerTest extends LoggerTestBase {

	private org.apache.logging.log4j.Logger log;

	@BeforeEach
	public void setUp() throws Exception {
		log = mock(org.apache.logging.log4j.Logger.class);
		logger = new Log4j2Logger(log);
	}

	@Test
	public void testGetName() {
		//given
		when(log.getName()).thenReturn(LoggerConstants.LOG);

		//then
		assertEquals(logger.getName(), LoggerConstants.LOG);
	}

	@Test
	public void testLog() {
		//when
		logger.log(Logger.Level.TRACE, LoggerConstants.SIMPLE_MESSAGE);

		//then
		verify(log).log(Level.TRACE, LoggerConstants.SIMPLE_MESSAGE);

		//when
		logger.log(Logger.Level.DEBUG, LoggerConstants.SIMPLE_MESSAGE);

		//then
		verify(log).log(Level.DEBUG, LoggerConstants.SIMPLE_MESSAGE);

		//when
		logger.log(Logger.Level.INFO, LoggerConstants.SIMPLE_MESSAGE);

		//then
		verify(log).log(Level.INFO, LoggerConstants.SIMPLE_MESSAGE);

		//when
		logger.log(Logger.Level.WARN, LoggerConstants.SIMPLE_MESSAGE);

		//then
		verify(log).log(Level.WARN, LoggerConstants.SIMPLE_MESSAGE);

		//when
		logger.log(Logger.Level.ERROR, LoggerConstants.SIMPLE_MESSAGE);

		//then
		verify(log).log(Level.ERROR, LoggerConstants.SIMPLE_MESSAGE);
	}

	@Test
	public void testLevel() {
		//when
		logger.trace(LoggerConstants.TRACE_MESSAGE);

		//then
		verify(log).trace(LoggerConstants.TRACE_MESSAGE);

		//when
		logger.debug(LoggerConstants.DEBUG_MESSAGE);

		//then
		verify(log).debug(LoggerConstants.DEBUG_MESSAGE);

		//when
		logger.info(LoggerConstants.INFO_MESSAGE);

		//then
		verify(log).info(LoggerConstants.INFO_MESSAGE);

		//when
		logger.warn(LoggerConstants.WARN_MESSAGE);

		//then
		verify(log).warn(LoggerConstants.WARN_MESSAGE);

		//when
		logger.error(LoggerConstants.ERROR_MESSAGE);

		//then
		verify(log).error(LoggerConstants.ERROR_MESSAGE);
	}

	@Test
	public void testErrorWithThrowable() {
		//given
		throwable = mock(Throwable.class);

		//when
		logger.error(LoggerConstants.ERROR_MESSAGE, throwable);

		//then
		verify(log).error(LoggerConstants.ERROR_MESSAGE, throwable);
	}

	@Test
	public void testWarnWithThrowable() {
		//given
		throwable = mock(Throwable.class);

		//when
		logger.warn(LoggerConstants.WARN_MESSAGE, throwable);

		//then
		verify(log).warn(LoggerConstants.WARN_MESSAGE, throwable);
	}

	@Test
	public void testLog4j2LoggerFactory() {
		//given
		loggerProvider = Log4j2Logger.PROVIDER;

		//when
		logger = loggerProvider.createLogger(LoggerConstants.LOGGER);

		//then
		assertEquals(Log4j2Logger.class, logger.getClass());
	}
}
