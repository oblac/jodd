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

package jodd.bean;

import jodd.bean.fixtures.Abean;
import jodd.util.StringTemplateParser;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class BeanTemplateParserTest {

	BeanTemplateParser beanTemplateParser = new BeanTemplateParser();

	@Test
	public void testTemplate() {
		Abean a = new Abean();

		assertEquals("xxxx", beanTemplateParser.parse("xxxx", a));
		assertEquals("", beanTemplateParser.parse("", a));
		assertEquals("...abean_value...", beanTemplateParser.parse("...${fooProp}...", a));
		assertEquals("abean_value", beanTemplateParser.parse("${fooProp}", a));

		assertEquals("...${fooProp}...", beanTemplateParser.parse("...\\${fooProp}...", a));
		assertEquals("...\\abean_value...", beanTemplateParser.parse("...\\\\${fooProp}...", a));
		assertEquals("...\\${fooProp}...", beanTemplateParser.parse("...\\\\\\${fooProp}...", a));
		assertEquals("...\\\\abean_value...", beanTemplateParser.parse("...\\\\\\\\${fooProp}...", a));
		assertEquals("...\\\\${fooProp}...", beanTemplateParser.parse("...\\\\\\\\\\${fooProp}...", a));

		assertEquals("${fooProp}", beanTemplateParser.parse("\\${fooProp}", a));
		assertEquals("\\abean_value", beanTemplateParser.parse("\\\\${fooProp}", a));
		assertEquals("\\${fooProp}", beanTemplateParser.parse("\\\\\\${fooProp}", a));
		assertEquals("\\\\abean_value", beanTemplateParser.parse("\\\\\\\\${fooProp}", a));
		assertEquals("\\\\${fooProp}", beanTemplateParser.parse("\\\\\\\\\\${fooProp}", a));

		assertEquals("abean_valueabean_value", beanTemplateParser.parse("${fooProp}${fooProp}", a));
		assertEquals("${fooProp}abean_value", beanTemplateParser.parse("\\${fooProp}${fooProp}", a));
	}

	@Test
	public void testNoParenthes() {
		BeanTemplateParser beanTemplateParser = new BeanTemplateParser();

		Map<String, Object> ctx = new HashMap<>();
		ctx.put("string", 173);

		assertEquals("173", beanTemplateParser.parse("$string", ctx));
		assertEquals("", beanTemplateParser.parse("$string2", ctx));
	}

	@Test
	public void testMap() {
		HashMap<String, String> map = new HashMap<>();
		map.put("key1", "value1");

		assertEquals("---value1---", beanTemplateParser.parse("---${key1}---", map));
	}

	@Test
	public void testMissing() {
		HashMap<String, String> map = new HashMap<>();
		map.put("key1", "value1");

		assertEquals("------", beanTemplateParser.parse("---${key2}---", map));

		BeanTemplateParser beanTemplateParser2 = new BeanTemplateParser();
		beanTemplateParser2.setMissingKeyReplacement("");

		assertEquals("------", beanTemplateParser2.parse("---${key2}---", map));

		beanTemplateParser2.setMissingKeyReplacement("<>");
		assertEquals("---<>---", beanTemplateParser2.parse("---${key2}---", map));
	}

	@Test
	public void testInner() {
		HashMap<String, String> map = new HashMap<>();
		map.put("key0", "1");
		map.put("key1", "2");
		map.put("key2", "value");

		assertEquals("---value---", beanTemplateParser.parse("---${key${key1}}---", map));

		assertEquals("---value---", beanTemplateParser.parse("---${key${key${key0}}}---", map));
	}

	@Test
	public void testReplaceMissingKey() {
		StringTemplateParser stp = new StringTemplateParser();

		BeanTemplateParser btp = new BeanTemplateParser();
		HashMap<String, String> map = new HashMap<>();
		map.put("key0", "1");
		map.put("key1", "2");

		assertEquals(".1.", btp.parse(".${key0}.", map));
		assertEquals("..", btp.parse(".${key2}.", map));

		assertEquals(".1.", stp.parse(".${key0}.", StringTemplateParser.createMapMacroResolver(map)));
		assertEquals("..", stp.parse(".${key2}.", StringTemplateParser.createMapMacroResolver(map)));

		btp.setMissingKeyReplacement("x");
		assertEquals(".x.", btp.parse(".${key2}.", map));

		btp.setReplaceMissingKey(false);
		assertEquals(".${key2}.", btp.parse(".${key2}.", map));

		btp.setMissingKeyReplacement(null);
		assertEquals(".${key2}.", btp.parse(".${key2}.", map));
	}

	@Test
	public void testResolveEscapes() {
		Abean a = new Abean();
		BeanTemplateParser btp = new BeanTemplateParser();
		btp.setResolveEscapes(false);

		assertEquals("...abean_value...", btp.parse("...${fooProp}...", a));

		assertEquals("...\\${fooProp}...", btp.parse("...\\${fooProp}...", a));
		assertEquals("...\\\\abean_value...", btp.parse("...\\\\${fooProp}...", a));
		assertEquals("...\\\\\\${fooProp}...", btp.parse("...\\\\\\${fooProp}...", a));
		assertEquals("...\\\\\\\\abean_value...", btp.parse("...\\\\\\\\${fooProp}...", a));
		assertEquals("...\\\\\\\\\\${fooProp}...", btp.parse("...\\\\\\\\\\${fooProp}...", a));
	}
}
