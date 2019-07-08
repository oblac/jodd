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

package jodd.log.impl;

import jodd.log.Logger.Level;
import jodd.log.impl.fixtures.LoggerConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class JDKLoggerTest extends LoggerTestBase {

	private java.util.logging.Logger log;

	@BeforeEach
	void setUp() throws Exception {
		log = mock(java.util.logging.Logger.class);
		logger = new JDKLogger(log);
	}

	@Test
	void testGetName() {
		//when
		logger.getName();

		//then
		verify(log).getName();
	}

	@Test
	void testLog() {
		//when
		logger.log(Level.TRACE, LoggerConstants.SIMPLE_MESSAGE);

		//then
		verify(log).log(java.util.logging.Level.FINER, LoggerConstants.SIMPLE_MESSAGE);

		//when
		logger.log(Level.DEBUG, LoggerConstants.SIMPLE_MESSAGE);

		//then
		verify(log).log(java.util.logging.Level.FINE, LoggerConstants.SIMPLE_MESSAGE);

		//when
		logger.log(Level.INFO, LoggerConstants.SIMPLE_MESSAGE);

		//then
		verify(log).log(java.util.logging.Level.INFO, LoggerConstants.SIMPLE_MESSAGE);

		//when
		logger.log(Level.WARN, LoggerConstants.SIMPLE_MESSAGE);

		//then
		verify(log).log(java.util.logging.Level.WARNING, LoggerConstants.SIMPLE_MESSAGE);

		//when
		logger.log(Level.ERROR, LoggerConstants.SIMPLE_MESSAGE);

		//then
		verify(log).log(java.util.logging.Level.SEVERE, LoggerConstants.SIMPLE_MESSAGE);
	}

	@Test
	void testLevel() {
		//when
		logger.trace(LoggerConstants.TRACE_MESSAGE);

		//then
		verify(log).log(java.util.logging.Level.FINER, LoggerConstants.TRACE_MESSAGE);

		//when
		logger.debug(LoggerConstants.DEBUG_MESSAGE);

		//then
		verify(log).log(java.util.logging.Level.FINE, LoggerConstants.DEBUG_MESSAGE);

		//when
		logger.info(LoggerConstants.INFO_MESSAGE);

		//then
		verify(log).log(java.util.logging.Level.INFO, LoggerConstants.INFO_MESSAGE);

		//when
		logger.warn(LoggerConstants.WARN_MESSAGE);

		//then
		verify(log).log(java.util.logging.Level.WARNING, LoggerConstants.WARN_MESSAGE);

		//when
		logger.error(LoggerConstants.ERROR_MESSAGE);

		//then
		verify(log).log(java.util.logging.Level.SEVERE, LoggerConstants.ERROR_MESSAGE);
	}

	@Test
	void testErrorWithThrowable() {
		//given
		throwable = mock(Throwable.class);

		//when
		logger.error(LoggerConstants.ERROR_MESSAGE, throwable);

		//then
		verify(log).log(java.util.logging.Level.SEVERE, LoggerConstants.ERROR_MESSAGE, throwable);
	}

	@Test
	void testWarnWithThrowable() {
		//given
		throwable = mock(Throwable.class);

		//when
		logger.warn(LoggerConstants.WARN_MESSAGE, throwable);

		//then
		verify(log).log(java.util.logging.Level.WARNING, LoggerConstants.WARN_MESSAGE, throwable);
	}

	@Test
	void testJDKLoggerFactory() {
		//given
		loggerProvider = JDKLogger.PROVIDER;

		//when
		logger = loggerProvider.createLogger(LoggerConstants.LOGGER);

		//then
		assertEquals(JDKLogger.class, logger.getClass());
	}
}
