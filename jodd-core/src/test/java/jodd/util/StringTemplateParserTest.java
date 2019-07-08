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

import jodd.template.MapTemplateParser;
import jodd.template.StringTemplateParser;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringTemplateParserTest {

	@Test
	void testMap() {
		MapTemplateParser stp = new MapTemplateParser();

		HashMap<String, String> map = new HashMap<>();
		map.put("key1", "value1");

		assertEquals("---value1---", stp.of(map).parse("---${key1}---"));
	}


	@Test
	void testMissing() {
		MapTemplateParser stp = new MapTemplateParser();

		HashMap<String, String> map = new HashMap<>();
		map.put("key1", "value1");


		assertEquals("------", stp.of(map).parse("---${key2}---"));

		stp.setReplaceMissingKey(false);
		assertEquals("---${key2}---", stp.of(map).parse("---${key2}---"));

		stp.setReplaceMissingKey(true);
		stp.setMissingKeyReplacement("");

		assertEquals("------", stp.of(map).parse("---${key2}---"));

		stp.setMissingKeyReplacement("<>");
		assertEquals("---<>---", stp.of(map).parse("---${key2}---"));
	}

	@Test
	void testInner() {
		MapTemplateParser stp = new MapTemplateParser();

		HashMap<String, String> map = new HashMap<>();
		map.put("key0", "1");
		map.put("key1", "2");
		map.put("key2", "value");

		assertEquals("---value---", stp.of(map).parse("---${key${key1}}---"));

		assertEquals("---value---", stp.of(map).parse("---${key${key${key0}}}---"));
	}

	@Test
	void testInner2() {
		MapTemplateParser stp = new MapTemplateParser();

		HashMap<String, String> map = new HashMap<>();
		map.put("foo", "foo");
		map.put("boo.foo", "*${foo}*");
		map.put("zoo", "${boo.${foo}}");

		assertEquals("-*${foo}*-", stp.of(map).parse("-${boo.${foo}}-"));
		assertEquals("-${boo.${foo}}-", stp.of(map).parse("-${zoo}-"));

		stp.setParseValues(true);
		assertEquals("-*foo*-", stp.of(map).parse("-${boo.${foo}}-"));
		assertEquals("-*foo*-", stp.of(map).parse("-${zoo}-"));

	}

	@Test
	void testResolver() {
		StringTemplateParser stp = new StringTemplateParser();
		assertEquals("xxxSMALLxxx", stp.parse("xxx${small}xxx", String::toUpperCase));
	}

	@Test
	void testReplaceMissingKey() {
		MapTemplateParser stp = new MapTemplateParser();

		HashMap<String, String> map = new HashMap<>();
		map.put("key0", "1");
		map.put("key1", "2");

		assertEquals(".1.", stp.of(map).parse(".${key0}."));

		assertEquals("..", stp.of(map).parse(".${key2}."));

		stp.setMissingKeyReplacement("x");
		assertEquals(".x.", stp.of(map).parse(".${key2}."));

		stp.setReplaceMissingKey(false);
		assertEquals(".${key2}.", stp.of(map).parse(".${key2}."));

		stp.setMissingKeyReplacement(null);
		assertEquals(".${key2}.", stp.of(map).parse(".${key2}."));
	}

	@Test
	void testResolveEscapes() {
		MapTemplateParser stp = new MapTemplateParser();

		HashMap<String, String> map = new HashMap<>();
		map.put("fooProp", "abean_value");

		stp.setResolveEscapes(false);

		assertEquals("...abean_value...", stp.of(map).parse("...${fooProp}..."));

		assertEquals("...\\${fooProp}...", stp.of(map).parse("...\\${fooProp}..."));
		assertEquals("...\\\\abean_value...", stp.of(map).parse("...\\\\${fooProp}..."));
		assertEquals("...\\\\\\${fooProp}...", stp.of(map).parse("...\\\\\\${fooProp}..."));
		assertEquals("...\\\\\\\\abean_value...", stp.of(map).parse("...\\\\\\\\${fooProp}..."));
		assertEquals("...\\\\\\\\\\${fooProp}...", stp.of(map).parse("...\\\\\\\\\\${fooProp}..."));
	}

	@Test
	void testCustomMacrosEnds() {
		MapTemplateParser stp = new MapTemplateParser();

		HashMap<String, String> map = new HashMap<>();
		map.put("foo", "bar");
		map.put("bar", "zap");

		assertEquals("...bar...<%=foo%>...", stp.of(map).parse("...${foo}...<%=foo%>..."));

		stp.setMacroStart("<%=");
		stp.setMacroEnd("%>");
		stp.setMacroPrefix(null);

		assertEquals("...${foo}...bar...", stp.of(map).parse("...${foo}...<%=foo%>..."));

		assertEquals("z<%=foo%>z", stp.of(map).parse("z\\<%=foo%>z"));
		assertEquals("xzapx", stp.of(map).parse("x<%=<%=foo%>%>x"));
	}

	@Test
	void testNonScript() {
		HashMap<String, Object> map = new HashMap<>();
		map.put("foo", "bar");
		map.put("bar", "zap");
		map.put("inner.man", "jo");

		MapTemplateParser stp = new MapTemplateParser();

		assertEquals("...bar...", stp.of(map).parse("...$foo..."));
		assertEquals("xx bar xx", stp.of(map).parse("xx $foo xx"));
		assertEquals("bar", stp.of(map).parse("$foo"));

		assertEquals("jo", stp.of(map).parse("$inner.man"));
		assertEquals("jo.", stp.of(map).parse("$inner.man."));
		assertEquals("jo x", stp.of(map).parse("$inner.man x"));
		assertEquals("jo bar", stp.of(map).parse("$inner.man ${foo}"));

		stp.setStrictFormat();
		assertEquals("$inner.man bar", stp.of(map).parse("$inner.man ${foo}"));
	}

	@Test
	void test601_IndexOutOfBounds() {
		StringTemplateParser stp = new StringTemplateParser();
		stp.setReplaceMissingKey(false);

		assertEquals("$foo", stp.parse("$foo", null));
		assertEquals("$foo bar", stp.parse("$foo bar", null));
		assertEquals("foo $bar", stp.parse("foo $bar", null));
		assertEquals("$foo", stp.parse("$foo", (s) -> {throw new RuntimeException();}));
	}

	@Test
	void test601_DuplicatedChar() {
		StringTemplateParser stp = new StringTemplateParser();
		stp.setReplaceMissingKey(false);

		assertEquals("bar$foo baz", stp.parse("bar$foo baz", null));
		assertEquals("bar$foo baz", stp.parse("bar$foo baz", (s) -> {throw new RuntimeException();}));
	}

}
