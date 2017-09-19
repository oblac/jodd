package jodd.log.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

public class SameInstanceTest {

	@Test
	public void testSameLogger_JCL() {
		JCLLogger logger1 = JCLLogger.PROVIDER.createLogger("hello");
		JCLLogger logger2 = JCLLogger.PROVIDER.createLogger("hello");

		assertSame(logger1.logger, logger2.logger);
	}

	@Test
	public void testSameLogger_JDK() {
		JDKLogger logger1 = JDKLogger.PROVIDER.createLogger("hello");
		JDKLogger logger2 = JDKLogger.PROVIDER.createLogger("hello");

		assertSame(logger1.logger, logger2.logger);
	}

	@Test
	public void testSameLogger_Log4j2() {
		Log4j2Logger logger1 = Log4j2Logger.PROVIDER.createLogger("hello");
		Log4j2Logger logger2 = Log4j2Logger.PROVIDER.createLogger("hello");

		assertSame(logger1.logger, logger2.logger);
	}

	@Test
	public void testSameLogger_Slf4j() {
		Slf4jLogger logger1 = Slf4jLogger.PROVIDER.createLogger("hello");
		Slf4jLogger logger2 = Slf4jLogger.PROVIDER.createLogger("hello");

		assertSame(logger1.logger, logger2.logger);
	}

	@Test
	public void testSameLogger_NOP() {
		NOPLogger logger1 = NOPLogger.PROVIDER.createLogger("hello");
		NOPLogger logger2 = NOPLogger.PROVIDER.createLogger("hello");

		assertSame(logger1, logger2);
	}

	@Test
	public void testSameLogger_Simple() {
		SimpleLogger logger1 = SimpleLogger.PROVIDER.createLogger("hello");
		SimpleLogger logger2 = SimpleLogger.PROVIDER.createLogger("hello");

		assertSame(logger1, logger2);
	}

}
