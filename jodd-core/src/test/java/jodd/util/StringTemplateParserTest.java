// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import org.junit.Test;

import java.util.HashMap;

import static jodd.util.StringTemplateParser.createMapMacroResolver;
import static org.junit.Assert.assertEquals;

public class StringTemplateParserTest {

	@Test
	public void testMap() {
		StringTemplateParser stp = new StringTemplateParser();

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("key1", "value1");

		assertEquals("---value1---", stp.parse("---${key1}---", createMapMacroResolver(map)));
	}


	@Test
	public void testMissing() {
		StringTemplateParser stp = new StringTemplateParser();

		HashMap<String, String> map = new HashMap<String, String>();
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

		HashMap<String, String> map = new HashMap<String, String>();
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

		HashMap<String, String> map = new HashMap<String, String>();
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

		HashMap<String, String> map = new HashMap<String, String>();
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

		HashMap<String, String> map = new HashMap<String, String>();
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

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("foo", "bar");
		map.put("bar", "zap");
		StringTemplateParser.MacroResolver macroResolver = createMapMacroResolver(map);

		assertEquals("...bar...<%=foo%>...", stp.parse("...${foo}...<%=foo%>...", macroResolver));

		stp.setMacroStart("<%=");
		stp.setMacroEnd("%>");

		assertEquals("...${foo}...bar...", stp.parse("...${foo}...<%=foo%>...", macroResolver));

		assertEquals("z<%=foo%>z", stp.parse("z\\<%=foo%>z", macroResolver));
		assertEquals("xzapx", stp.parse("x<%=<%=foo%>%>x", macroResolver));
	}

}