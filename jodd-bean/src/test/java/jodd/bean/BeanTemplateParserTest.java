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
import jodd.template.ContextTemplateParser;
import jodd.template.MapTemplateParser;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BeanTemplateParserTest {

	@Test
	void testTemplate() {
		BeanTemplateParser beanTemplateParser = new BeanTemplateParser();
		Abean a = new Abean();
		ContextTemplateParser ctp = beanTemplateParser.of(a);

		assertEquals("xxxx", ctp.parse("xxxx"));
		assertEquals("", ctp.parse(""));
		assertEquals("...abean_value...", ctp.parse("...${fooProp}..."));
		assertEquals("abean_value", ctp.parse("${fooProp}"));

		assertEquals("...${fooProp}...", ctp.parse("...\\${fooProp}..."));
		assertEquals("...\\abean_value...", ctp.parse("...\\\\${fooProp}..."));
		assertEquals("...\\${fooProp}...", ctp.parse("...\\\\\\${fooProp}..."));
		assertEquals("...\\\\abean_value...", ctp.parse("...\\\\\\\\${fooProp}..."));
		assertEquals("...\\\\${fooProp}...", ctp.parse("...\\\\\\\\\\${fooProp}..."));

		assertEquals("${fooProp}", ctp.parse("\\${fooProp}"));
		assertEquals("\\abean_value", ctp.parse("\\\\${fooProp}"));
		assertEquals("\\${fooProp}", ctp.parse("\\\\\\${fooProp}"));
		assertEquals("\\\\abean_value", ctp.parse("\\\\\\\\${fooProp}"));
		assertEquals("\\\\${fooProp}", ctp.parse("\\\\\\\\\\${fooProp}"));

		assertEquals("abean_valueabean_value", ctp.parse("${fooProp}${fooProp}"));
		assertEquals("${fooProp}abean_value", ctp.parse("\\${fooProp}${fooProp}"));
	}

	@Test
	void testNoParenthes() {
		BeanTemplateParser beanTemplateParser = new BeanTemplateParser();

		Map<String, Object> ctx = new HashMap<>();
		ctx.put("string", 173);

		assertEquals("173", beanTemplateParser.of(ctx).parse("$string"));
		assertEquals("", beanTemplateParser.of(ctx).parse("$string2"));
	}

	@Test
	void testMap() {
		BeanTemplateParser beanTemplateParser = new BeanTemplateParser();
		HashMap<String, String> map = new HashMap<>();
		map.put("key1", "value1");

		assertEquals("---value1---", beanTemplateParser.of(map).parse("---${key1}---"));
	}

	@Test
	void testMissing() {
		BeanTemplateParser beanTemplateParser = new BeanTemplateParser();
		HashMap<String, String> map = new HashMap<>();
		map.put("key1", "value1");

		assertEquals("------", beanTemplateParser.of(map).parse("---${key2}---"));

		BeanTemplateParser beanTemplateParser2 = new BeanTemplateParser();
		beanTemplateParser2.setMissingKeyReplacement("");

		assertEquals("------", beanTemplateParser2.of(map).parse("---${key2}---"));

		beanTemplateParser2.setMissingKeyReplacement("<>");
		assertEquals("---<>---", beanTemplateParser2.of(map).parse("---${key2}---"));
	}

	@Test
	void testInner() {
		BeanTemplateParser beanTemplateParser = new BeanTemplateParser();
		HashMap<String, String> map = new HashMap<>();
		map.put("key0", "1");
		map.put("key1", "2");
		map.put("key2", "value");

		assertEquals("---value---", beanTemplateParser.of(map).parse("---${key${key1}}---"));

		assertEquals("---value---", beanTemplateParser.of(map).parse("---${key${key${key0}}}---"));
	}

	@Test
	void testReplaceMissingKey() {
		MapTemplateParser stp = new MapTemplateParser();

		BeanTemplateParser btp = new BeanTemplateParser();

		HashMap<String, String> map = new HashMap<>();
		map.put("key0", "1");
		map.put("key1", "2");

		assertEquals(".1.", btp.of(map).parse(".${key0}."));
		assertEquals("..", btp.of(map).parse(".${key2}."));

		assertEquals(".1.", stp.of(map).parse(".${key0}."));
		assertEquals("..", stp.of(map).parse(".${key2}."));

		btp.setMissingKeyReplacement("x");
		assertEquals(".x.", btp.of(map).parse(".${key2}."));

		btp.setReplaceMissingKey(false);
		assertEquals(".${key2}.", btp.of(map).parse(".${key2}."));

		btp.setMissingKeyReplacement(null);
		assertEquals(".${key2}.", btp.of(map).parse(".${key2}."));
	}

	@Test
	void testResolveEscapes() {
		Abean a = new Abean();
		BeanTemplateParser btp = new BeanTemplateParser();
		btp.setResolveEscapes(false);
		ContextTemplateParser ctp = btp.of(a);

		assertEquals("...abean_value...", ctp.parse("...${fooProp}..."));

		assertEquals("...\\${fooProp}...", ctp.parse("...\\${fooProp}..."));
		assertEquals("...\\\\abean_value...", ctp.parse("...\\\\${fooProp}..."));
		assertEquals("...\\\\\\${fooProp}...", ctp.parse("...\\\\\\${fooProp}..."));
		assertEquals("...\\\\\\\\abean_value...", ctp.parse("...\\\\\\\\${fooProp}..."));
		assertEquals("...\\\\\\\\\\${fooProp}...", ctp.parse("...\\\\\\\\\\${fooProp}..."));
	}
}
