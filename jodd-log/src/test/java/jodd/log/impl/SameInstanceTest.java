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
