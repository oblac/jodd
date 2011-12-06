// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean;

import junit.framework.TestCase;
import jodd.bean.data.Abean;

import java.util.HashMap;

public class BeanTemplateParserTest extends TestCase {

	BeanTemplateParser beanTemplateParser = new BeanTemplateParser();

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

	public void testMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("key1", "value1");

		assertEquals("---value1---", beanTemplateParser.parse("---${key1}---", map));
	}

	public void testMissing() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("key1", "value1");

		try {
			beanTemplateParser.parse("---${key2}---", map);
			fail();
		} catch (BeanException bex) {
			// ignore
		}

		BeanTemplateParser beanTemplateParser2 = new BeanTemplateParser();
		beanTemplateParser2.setMissingKeyReplacement("");

		assertEquals("------", beanTemplateParser2.parse("---${key2}---", map));

		beanTemplateParser2.setMissingKeyReplacement("<>");
		assertEquals("---<>---", beanTemplateParser2.parse("---${key2}---", map));
	}

	public void testInner() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("key0", "1");
		map.put("key1", "2");
		map.put("key2", "value");

		assertEquals("---value---", beanTemplateParser.parse("---${key${key1}}---", map));

		assertEquals("---value---", beanTemplateParser.parse("---${key${key${key0}}}---", map));
	}

	public void testResolver() {
		BeanTemplateMacroResolver btr = new BeanTemplateMacroResolver() {
			public Object resolve(String name) {
				return name.toUpperCase();
			}
		};

		assertEquals("xxxSMALLxxx", beanTemplateParser.parse("xxx${small}xxx", btr));
	}

	public void testReplaceMissingKey() {
		BeanTemplateParser btp = new BeanTemplateParser();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("key0", "1");
		map.put("key1", "2");

		assertEquals(".1.", btp.parse(".${key0}.", map));
		try {
			assertEquals(".1.", btp.parse(".${key2}.", map));
			fail();
		} catch (BeanException be) {
		}

		btp.setMissingKeyReplacement("x");
		assertEquals(".x.", btp.parse(".${key2}.", map));

		btp.setReplaceMissingKey(false);
		assertEquals(".${key2}.", btp.parse(".${key2}.", map));

		btp.setMissingKeyReplacement(null);
		assertEquals(".${key2}.", btp.parse(".${key2}.", map));
	}

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
