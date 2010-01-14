// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import junit.framework.TestCase;

import java.util.Properties;
import java.io.IOException;

import static jodd.util.PropertiesUtil.resolveProperty;

public class PropertiesUtilTest extends TestCase {

	public void testResolve() throws IOException {
		Properties p = PropertiesUtil.createFromString(
				"foo=foo\n" +
				"boo.foo=*${foo}*\n" +
				"zoo=${boo.${foo}}");
		assertEquals(3, p.size());

		assertNull(p.getProperty("xxx"));
		assertEquals("foo", p.getProperty("foo"));
		assertEquals("*${foo}*", p.getProperty("boo.foo"));

		assertNull(resolveProperty(p, "xxx"));
		assertEquals("foo", resolveProperty(p, "foo"));
		assertEquals("*foo*", resolveProperty(p, "boo.foo"));
		assertEquals("*foo*", resolveProperty(p, "zoo"));

		PropertiesUtil.resolveAllVariables(p);
		assertEquals(3, p.size());
		assertEquals("foo", p.getProperty("foo"));
		assertEquals("*foo*", p.getProperty("boo.foo"));
		assertEquals("*foo*", p.getProperty("zoo"));
	}

	public void testEscape() throws IOException {
		Properties p = PropertiesUtil.createFromString(
				"foo=foo\n" +
				"boo.foo=*\\\\${foo}*\n" +
				"zoo=\\\\${boo.\\\\${foo}}\n" +
				"doo=\\\\\\\\${foo}");
		assertEquals(4, p.size());

		assertNull(p.getProperty("xxx"));
		assertEquals("foo", p.getProperty("foo"));
		assertEquals("*\\${foo}*", p.getProperty("boo.foo"));
		assertEquals("\\${boo.\\${foo}}", p.getProperty("zoo"));
		assertEquals("\\\\${foo}", p.getProperty("doo"));

		assertNull(resolveProperty(p, "xxx"));
		assertEquals("foo", resolveProperty(p, "foo"));
		assertEquals("*${foo}*", resolveProperty(p, "boo.foo"));
		assertEquals("${boo.${foo}}", resolveProperty(p, "zoo"));
		assertEquals("\\foo", resolveProperty(p, "doo"));
	}

}
