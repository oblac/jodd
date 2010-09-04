// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean;

import junit.framework.TestCase;
import jodd.bean.data.Abean;

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
}
