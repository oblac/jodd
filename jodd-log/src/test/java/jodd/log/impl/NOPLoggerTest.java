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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;

public class NOPLoggerTest extends LoggerTestBase {

	private String name = "NOPLogger";

	@Before
	public void setUp() throws Exception {
		logger = new NOPLogger(name);
	}

	@Test
	public void testIsEnabled() {
		assertFalse("Source code implemented in such a way that this method call always returns false"
			, logger.isEnabled(Level.DEBUG));
	}

	@Test
	public void testGetName() {
		assertEquals("Name must be equal to NOPLogger", name, logger.getName());
	}

	@Test
	public void testLog() {
		//given
		throwable = mock(Throwable.class);

		//when
		//The below methods are no op methods in actual implementations.
		//so we will not be able to verify anything
		logger.log(Level.DEBUG, name);
		logger.trace(name);
		logger.debug(name);
		logger.info(name);
		logger.warn(name);
		logger.warn(name, throwable);
		logger.error(name);
		logger.error(name, throwable);
	}

	@Test
	public void testIsLevelEnabled() {
		super.testIsLevelEnabled();
	}

	@Test
	public void testJDKLoggerFactory() {
		//given
		loggerFactory = new NOPLoggerFactory();

		//when
		logger = loggerFactory.getLogger(LoggerConstants.LOGGER);

		//then
		assertThat("Logger must be of type NOPLogger", logger.getClass(),
			is(instanceOf(NOPLogger.class.getClass())));
	}
}