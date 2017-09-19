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

import org.junit.jupiter.api.Test;

import static jodd.log.Logger.Level.TRACE;
import static jodd.log.Logger.Level.DEBUG;
import static jodd.log.Logger.Level.INFO;
import static jodd.log.Logger.Level.WARN;
import static jodd.log.Logger.Level.ERROR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
