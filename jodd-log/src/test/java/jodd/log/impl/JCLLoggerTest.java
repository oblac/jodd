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
import org.apache.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class JCLLoggerTest extends LoggerTestBase {

	private Log log;

	@BeforeEach
	public void setUp() throws Exception {
		log = mock(Log.class);
		logger = new JCLLogger(log);
	}

	@Test
	public void testGetName() {
		assertEquals(log.toString(), logger.getName());
	}

	@Test
	public void testLog() {
		//when
		logger.log(Level.TRACE, LoggerConstants.SIMPLE_MESSAGE);

		//then
		verify(log).trace(LoggerConstants.SIMPLE_MESSAGE);

		//when
		logger.log(Level.DEBUG, LoggerConstants.SIMPLE_MESSAGE);

		//then
		verify(log).debug(LoggerConstants.SIMPLE_MESSAGE);

		//when
		logger.log(Level.INFO, LoggerConstants.SIMPLE_MESSAGE);

		//then
		verify(log).info(LoggerConstants.SIMPLE_MESSAGE);

		//when
		logger.log(Level.WARN, LoggerConstants.SIMPLE_MESSAGE);

		//then
		verify(log).warn(LoggerConstants.SIMPLE_MESSAGE);

		//when
		logger.log(Level.ERROR, LoggerConstants.SIMPLE_MESSAGE);

		//then
		verify(log).error(LoggerConstants.SIMPLE_MESSAGE);
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
	public void testJCLLoggerFactory() {
		//given
		loggerProvider = JCLLogger.PROVIDER;

		//when
		logger = loggerProvider.createLogger(LoggerConstants.LOGGER);

		//then
		assertEquals(JCLLogger.class, logger.getClass());
	}
}
