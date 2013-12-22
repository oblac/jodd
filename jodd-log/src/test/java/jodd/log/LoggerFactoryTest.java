// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.log;

import jodd.io.StringOutputStream;
import jodd.log.impl.JDKLoggerFactory;
import jodd.log.impl.NOPLoggerFactory;
import jodd.log.impl.SimpleLoggerFactory;
import org.junit.Test;

import java.io.PrintStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LoggerFactoryTest {

	@Test
	public void testNopLogger() {
		LoggerFactory.setLoggerFactory(new NOPLoggerFactory());
		Logger log = LoggerFactory.getLogger("foo");

		assertEquals("*", log.getName());

		PrintStream out = System.out;
		StringOutputStream sos = new StringOutputStream();
		System.setOut(new PrintStream(sos));

		log.debug("nothing");
		log.error("nothing");

		assertEquals("", sos.toString());

		System.setOut(out);
	}

	@Test
	public void testSimpleFactory() {
		LoggerFactory.setLoggerFactory(new SimpleLoggerFactory(Logger.Level.TRACE));
		Logger log = LoggerFactory.getLogger("foo");

		assertEquals("foo", log.getName());

		PrintStream out = System.out;
		StringOutputStream sos = new StringOutputStream();
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