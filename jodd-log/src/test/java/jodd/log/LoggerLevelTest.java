// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.log;

import org.junit.Test;

import static jodd.log.Logger.Level.TRACE;
import static jodd.log.Logger.Level.DEBUG;
import static jodd.log.Logger.Level.INFO;
import static jodd.log.Logger.Level.WARN;
import static jodd.log.Logger.Level.ERROR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LoggerLevelTest {

	@Test
	public void testIsTraceEnabledFor() {
		final Logger.Level level = TRACE;
		assertTrue(level.isEnabledFor(TRACE));
		assertFalse(level.isEnabledFor(DEBUG));
		assertFalse(level.isEnabledFor(INFO));
		assertFalse(level.isEnabledFor(WARN));
		assertFalse(level.isEnabledFor(ERROR));
	}

	@Test
	public void testIsDebugEnabledFor() {
		final Logger.Level level = DEBUG;
		assertTrue(level.isEnabledFor(TRACE));
		assertTrue(level.isEnabledFor(DEBUG));
		assertFalse(level.isEnabledFor(INFO));
		assertFalse(level.isEnabledFor(WARN));
		assertFalse(level.isEnabledFor(ERROR));
	}

	@Test
	public void testIsInfoEnabledFor() {
		final Logger.Level level = INFO;
		assertTrue(level.isEnabledFor(TRACE));
		assertTrue(level.isEnabledFor(DEBUG));
		assertTrue(level.isEnabledFor(INFO));
		assertFalse(level.isEnabledFor(WARN));
		assertFalse(level.isEnabledFor(ERROR));
	}

	@Test
	public void testIsWarnEnabledFor() {
		final Logger.Level level = WARN;
		assertTrue(level.isEnabledFor(TRACE));
		assertTrue(level.isEnabledFor(DEBUG));
		assertTrue(level.isEnabledFor(INFO));
		assertTrue(level.isEnabledFor(WARN));
		assertFalse(level.isEnabledFor(ERROR));
	}

	@Test
	public void testIsErrorEnabledFor() {
		final Logger.Level level = ERROR;
		assertTrue(level.isEnabledFor(TRACE));
		assertTrue(level.isEnabledFor(DEBUG));
		assertTrue(level.isEnabledFor(INFO));
		assertTrue(level.isEnabledFor(WARN));
		assertTrue(level.isEnabledFor(ERROR));
	}

	@Test
	public void testLevelValueOf() {
		assertEquals(ERROR, Logger.Level.valueOf("ERROR"));
		assertEquals(INFO, Logger.Level.valueOf("INFO"));
		assertEquals(WARN, Logger.Level.valueOf("WARN"));
		assertEquals(DEBUG, Logger.Level.valueOf("DEBUG"));
		assertEquals(TRACE, Logger.Level.valueOf("TRACE"));
	}

}