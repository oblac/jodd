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

import jodd.log.impl.JCLLogger;
import jodd.log.impl.Log4j2Logger;
import jodd.log.impl.NOPLogger;
import jodd.log.impl.SimpleLogger;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoggerFactoryTest {

	@Test
	public void testNopLogger() {
		LoggerFactory.setLoggerProvider(NOPLogger.PROVIDER);
		Logger log = LoggerFactory.getLogger("foo");

		assertEquals("*", log.getName());

		PrintStream out = System.out;
		ByteArrayOutputStream sos = new ByteArrayOutputStream();
		System.setOut(new PrintStream(sos));

		log.debug("nothing");
		log.error("nothing");

		assertEquals("", sos.toString());

		System.setOut(out);
	}

	@Test
	public void testSimpleFactory() {
		LoggerFactory.setLoggerProvider(SimpleLogger.PROVIDER);
		Logger log = LoggerFactory.getLogger("foo");
		log.setLevel(Logger.Level.TRACE);

		assertEquals("foo", log.getName());

		PrintStream out = System.out;
		ByteArrayOutputStream sos = new ByteArrayOutputStream();
		System.setOut(new PrintStream(sos));

		log.debug("debug");
		log.error("error");

		System.setOut(out);

		String str = sos.toString();

		assertTrue(str.contains("[DEBUG]"));
		assertTrue(str.contains("[ERROR]"));
		assertFalse(str.contains("[TRACE]"));
	}

}
