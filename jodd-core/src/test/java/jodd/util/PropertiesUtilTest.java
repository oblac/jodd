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

package jodd.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Properties;

import static jodd.util.PropertiesUtil.resolveProperty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PropertiesUtilTest {

	@Test
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

	@Test
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

	@Test
	public void testNull() {

		Properties properties = new Properties();
		properties.setProperty("foo", "123");
		properties.setProperty("xyz", "q${foo}z");
		properties.setProperty("abc", "q${bar}z");

		assertEquals("q123z", resolveProperty(properties, "xyz"));
		assertEquals("qz", resolveProperty(properties, "abc"));
	}

}
