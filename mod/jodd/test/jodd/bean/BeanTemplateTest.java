// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean;

import junit.framework.TestCase;
import jodd.bean.data.Abean;

import java.util.HashMap;

public class BeanTemplateTest extends TestCase {

	public void testTemplate() {
		Abean a = new Abean();

		assertEquals("xxxx", BeanTemplate.parse("xxxx", a));
		assertEquals("", BeanTemplate.parse("", a));
		assertEquals("...abean_value...", BeanTemplate.parse("...${fooProp}...", a));
		assertEquals("abean_value", BeanTemplate.parse("${fooProp}", a));

		assertEquals("...${fooProp}...", BeanTemplate.parse("...\\${fooProp}...", a));
		assertEquals("...\\abean_value...", BeanTemplate.parse("...\\\\${fooProp}...", a));
		assertEquals("...\\${fooProp}...", BeanTemplate.parse("...\\\\\\${fooProp}...", a));
		assertEquals("...\\\\abean_value...", BeanTemplate.parse("...\\\\\\\\${fooProp}...", a));
		assertEquals("...\\\\${fooProp}...", BeanTemplate.parse("...\\\\\\\\\\${fooProp}...", a));

		assertEquals("${fooProp}", BeanTemplate.parse("\\${fooProp}", a));
		assertEquals("\\abean_value", BeanTemplate.parse("\\\\${fooProp}", a));
		assertEquals("\\${fooProp}", BeanTemplate.parse("\\\\\\${fooProp}", a));
		assertEquals("\\\\abean_value", BeanTemplate.parse("\\\\\\\\${fooProp}", a));
		assertEquals("\\\\${fooProp}", BeanTemplate.parse("\\\\\\\\\\${fooProp}", a));

		assertEquals("abean_valueabean_value", BeanTemplate.parse("${fooProp}${fooProp}", a));
		assertEquals("${fooProp}abean_value", BeanTemplate.parse("\\${fooProp}${fooProp}", a));
	}

	public void testMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("key1", "value1");

		assertEquals("---value1---", BeanTemplate.parse("---${key1}---", map));
	}

	public void testMissing() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("key1", "value1");

		try {
			BeanTemplate.parse("---${key2}---", map);
			fail();
		} catch (BeanException bex) {
			// ignore
		}

		assertEquals("------", BeanTemplate.parse("---${key2}---", map, ""));
		assertEquals("---<>---", BeanTemplate.parse("---${key2}---", map, "<>"));
	}

	public void testInner() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("key0", "1");
		map.put("key1", "2");
		map.put("key2", "value");

		assertEquals("---value---", BeanTemplate.parse("---${key${key1}}---", map));

		assertEquals("---value---", BeanTemplate.parse("---${key${key${key0}}}---", map));
	}

	public void testResolver() {
		BeanTemplateResolver btr = new BeanTemplateResolver() {
			public Object resolve(String name) {
				return name.toUpperCase();
			}
		};

		assertEquals("xxxSMALLxxx", BeanTemplate.parse("xxx${small}xxx", btr));
	}
}
