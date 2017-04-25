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

import jodd.log.Logger;
import jodd.log.Logger.Level;
import jodd.log.impl.fixtures.LoggerConstants;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;

public class SimpleLoggerTest extends LoggerTestBase {

	private SimpleLoggerProvider slf;

	private ByteArrayOutputStream outputStream;

	private String output;

	@Before
	public void setUp() throws Exception {
		initializeLogFactoryAndLogger(Logger.Level.DEBUG);
	}

	@Test
	public void testBasicOperations() throws Exception {
		assertEquals("Logger Level must be debug", Logger.Level.DEBUG, slf.getLevel());
		assertTrue("Elapsed Time Should be greater than or equal to zero", slf.getElapsedTime() >= 0);
		assertEquals("Logger name must be simple logger", LoggerConstants.SIMPLE_LOGGER, logger.getName());
	}

	@Test
	public void testIsLevelEnabled() {
		//when
		initializeLogFactoryAndLogger(Logger.Level.DEBUG);

		//then 
		assertTrue("Debug must be enabled", logger.isDebugEnabled());

		//when
		initializeLogFactoryAndLogger(Logger.Level.ERROR);

		//then 
		assertTrue("Error must be enabled", logger.isErrorEnabled());

		//when
		initializeLogFactoryAndLogger(Logger.Level.INFO);

		//then 
		assertTrue("Info must be enabled", logger.isInfoEnabled());

		//when
		initializeLogFactoryAndLogger(Logger.Level.TRACE);

		//then 
		assertTrue("Trace must be enabled", logger.isTraceEnabled());

		//when
		initializeLogFactoryAndLogger(Logger.Level.WARN);

		//then 
		assertTrue("Warn must be enabled", logger.isWarnEnabled());
	}

	@Test
	public void testLogLevel() {
		//given
		setUpOutputStream();

		//when
		logger.debug(LoggerConstants.DEBUG_MESSAGE);

		//then
		output = outputStream.toString();
		assertTrue("Output must contain debug", output.contains(LoggerConstants.DEBUG));

		//when
		logger.error(LoggerConstants.ERROR_MESSAGE);

		//then
		output = outputStream.toString();
		assertTrue("Output must contain error", output.contains(LoggerConstants.ERROR));

		//when
		logger.info(LoggerConstants.INFO_MESSAGE);

		//then
		output = outputStream.toString();
		assertTrue("Output must contain info", output.contains(LoggerConstants.INFO));

		//when
		logger.warn(LoggerConstants.WARN_MESSAGE);

		//then
		output = outputStream.toString();
		assertTrue("Output must contain warn", output.contains(LoggerConstants.WARN_MESSAGE));

		//when
		initializeLogFactoryAndLogger(Logger.Level.TRACE);
		logger.trace(LoggerConstants.TRACE_MESSAGE);

		//then
		output = outputStream.toString();
		assertTrue("Output must contain trace", output.contains(LoggerConstants.TRACE));
	}

	@Test
	public void testLog() {
		//given
		setUpOutputStream();

		//when
		logger.log(Level.DEBUG, LoggerConstants.SIMPLE_MESSAGE);

		//then
		output = outputStream.toString();
		assertTrue("Output must contain debug", output.contains(LoggerConstants.SIMPLE_MESSAGE));
	}

	@Test
	public void testThrowable() {
		//given
		throwable = mock(Throwable.class);
		setUpOutputStream();

		//when
		logger.warn(LoggerConstants.WARN_MESSAGE, throwable);

		//then
		output = outputStream.toString();
		assertTrue("Output must contain warn", output.contains(LoggerConstants.WARN_MESSAGE));
		verify(throwable).printStackTrace(System.out);

		//setup
		throwable = mock(Throwable.class);

		//when
		logger.error(LoggerConstants.ERROR_MESSAGE, throwable);

		//then
		output = outputStream.toString();
		assertTrue("Output must contain error", output.contains(LoggerConstants.ERROR));
		verify(throwable).printStackTrace(System.out);
	}

	private void initializeLogFactoryAndLogger(Logger.Level level) {
		slf = new SimpleLoggerProvider(level);
		logger = slf.apply(LoggerConstants.SIMPLE_LOGGER);
	}

	private void setUpOutputStream() {
		outputStream = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outputStream));
	}
}