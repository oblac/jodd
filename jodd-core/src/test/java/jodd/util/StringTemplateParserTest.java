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

import java.util.HashMap;

import static jodd.util.StringTemplateParser.createMapMacroResolver;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringTemplateParserTest {

	@Test
	public void testMap() {
		StringTemplateParser stp = new StringTemplateParser();

		HashMap<String, String> map = new HashMap<>();
		map.put("key1", "value1");

		assertEquals("---value1---", stp.parse("---${key1}---", createMapMacroResolver(map)));
	}


	@Test
	public void testMissing() {
		StringTemplateParser stp = new StringTemplateParser();

		HashMap<String, String> map = new HashMap<>();
		map.put("key1", "value1");

		StringTemplateParser.MacroResolver macroResolver = createMapMacroResolver(map);

		assertEquals("------", stp.parse("---${key2}---", macroResolver));

		stp.setReplaceMissingKey(false);
		assertEquals("---${key2}---", stp.parse("---${key2}---", macroResolver));

		stp.setReplaceMissingKey(true);
		stp.setMissingKeyReplacement("");

		assertEquals("------", stp.parse("---${key2}---", macroResolver));

		stp.setMissingKeyReplacement("<>");
		assertEquals("---<>---", stp.parse("---${key2}---", macroResolver));
	}

	@Test
	public void testInner() {
		StringTemplateParser stp = new StringTemplateParser();

		HashMap<String, String> map = new HashMap<>();
		map.put("key0", "1");
		map.put("key1", "2");
		map.put("key2", "value");

		StringTemplateParser.MacroResolver macroResolver = createMapMacroResolver(map);

		assertEquals("---value---", stp.parse("---${key${key1}}---", macroResolver));

		assertEquals("---value---", stp.parse("---${key${key${key0}}}---", macroResolver));
	}

	@Test
	public void testInner2() {
		StringTemplateParser stp = new StringTemplateParser();

		HashMap<String, String> map = new HashMap<>();
		map.put("foo", "foo");
		map.put("boo.foo", "*${foo}*");
		map.put("zoo", "${boo.${foo}}");

		StringTemplateParser.MacroResolver macroResolver = createMapMacroResolver(map);

		assertEquals("-*${foo}*-", stp.parse("-${boo.${foo}}-", macroResolver));
		assertEquals("-${boo.${foo}}-", stp.parse("-${zoo}-", macroResolver));

		stp.setParseValues(true);
		assertEquals("-*foo*-", stp.parse("-${boo.${foo}}-", macroResolver));
		assertEquals("-*foo*-", stp.parse("-${zoo}-", macroResolver));

	}

	@Test
	public void testResolver() {
		StringTemplateParser stp = new StringTemplateParser();
		StringTemplateParser.MacroResolver macroResolver = new StringTemplateParser.MacroResolver() {
			public String resolve(String macroName) {
				return macroName.toUpperCase();
			}
		};
		assertEquals("xxxSMALLxxx", stp.parse("xxx${small}xxx", macroResolver));
	}

	@Test
	public void testReplaceMissingKey() {
		StringTemplateParser stp = new StringTemplateParser();

		HashMap<String, String> map = new HashMap<>();
		map.put("key0", "1");
		map.put("key1", "2");
		StringTemplateParser.MacroResolver macroResolver = createMapMacroResolver(map);

		assertEquals(".1.", stp.parse(".${key0}.", macroResolver));

		assertEquals("..", stp.parse(".${key2}.", macroResolver));

		stp.setMissingKeyReplacement("x");
		assertEquals(".x.", stp.parse(".${key2}.", macroResolver));

		stp.setReplaceMissingKey(false);
		assertEquals(".${key2}.", stp.parse(".${key2}.", macroResolver));

		stp.setMissingKeyReplacement(null);
		assertEquals(".${key2}.", stp.parse(".${key2}.", macroResolver));
	}

	@Test
	public void testResolveEscapes() {
		StringTemplateParser stp = new StringTemplateParser();

		HashMap<String, String> map = new HashMap<>();
		map.put("fooProp", "abean_value");
		StringTemplateParser.MacroResolver macroResolver = createMapMacroResolver(map);

		stp.setResolveEscapes(false);

		assertEquals("...abean_value...", stp.parse("...${fooProp}...", macroResolver));

		assertEquals("...\\${fooProp}...", stp.parse("...\\${fooProp}...", macroResolver));
		assertEquals("...\\\\abean_value...", stp.parse("...\\\\${fooProp}...", macroResolver));
		assertEquals("...\\\\\\${fooProp}...", stp.parse("...\\\\\\${fooProp}...", macroResolver));
		assertEquals("...\\\\\\\\abean_value...", stp.parse("...\\\\\\\\${fooProp}...", macroResolver));
		assertEquals("...\\\\\\\\\\${fooProp}...", stp.parse("...\\\\\\\\\\${fooProp}...", macroResolver));
	}

	@Test
	public void testCustomMacrosEnds() {
		StringTemplateParser stp = new StringTemplateParser();

		HashMap<String, String> map = new HashMap<>();
		map.put("foo", "bar");
		map.put("bar", "zap");
		StringTemplateParser.MacroResolver macroResolver = createMapMacroResolver(map);

		assertEquals("...bar...<%=foo%>...", stp.parse("...${foo}...<%=foo%>...", macroResolver));

		stp.setMacroStart("<%=");
		stp.setMacroEnd("%>");
		stp.setMacroPrefix(null);

		assertEquals("...${foo}...bar...", stp.parse("...${foo}...<%=foo%>...", macroResolver));

		assertEquals("z<%=foo%>z", stp.parse("z\\<%=foo%>z", macroResolver));
		assertEquals("xzapx", stp.parse("x<%=<%=foo%>%>x", macroResolver));
	}

	@Test
	public void testNonScript() {
		HashMap<String, Object> map = new HashMap<>();
		map.put("foo", "bar");
		map.put("bar", "zap");
		map.put("inner.man", "jo");

		StringTemplateParser.MacroResolver macroResolver = createMapMacroResolver(map);

		StringTemplateParser stp = new StringTemplateParser();
		assertEquals("...bar...", stp.parse("...$foo...", macroResolver));
		assertEquals("xx bar xx", stp.parse("xx $foo xx", macroResolver));
		assertEquals("bar", stp.parse("$foo", macroResolver));

		assertEquals("jo", stp.parse("$inner.man", macroResolver));
		assertEquals("jo.", stp.parse("$inner.man.", macroResolver));
		assertEquals("jo x", stp.parse("$inner.man x", macroResolver));
		assertEquals("jo bar", stp.parse("$inner.man ${foo}", macroResolver));

		stp.setStrictFormat();
		assertEquals("$inner.man bar", stp.parse("$inner.man ${foo}", macroResolver));

	}

}
