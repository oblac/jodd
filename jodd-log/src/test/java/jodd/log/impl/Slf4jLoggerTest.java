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
import jodd.log.impl.util.LoggerConstants;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class Slf4jLoggerTest extends LoggerTestBase {

	private org.slf4j.Logger log;

	@Before
	public void setUp() throws Exception {
		log = mock(org.slf4j.Logger.class);
		logger = new Slf4jLogger(log);
	}

	@Test
	public void testIsLevelEnabled() {
		super.testIsLevelEnabled();
	}

	@Test
	public void testGetName() {
		//given
		when(log.getName()).thenReturn(LoggerConstants.LOG);

		//then
		assertEquals("Name must be equal log as we set log to the logger", LoggerConstants.LOG, logger.getName());
	}

	@Test
	public void testIsEnabled() {
		super.testIsEnabled();
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
		loggerFactory = new Slf4jLoggerFactory();

		//when
		logger = (Slf4jLogger) loggerFactory.getLogger(LoggerConstants.LOGGER);

		//then
		assertThat("Logger must be of type JCLLogger", logger.getClass(),
			is(instanceOf(Slf4jLogger.class.getClass())));
	}
}