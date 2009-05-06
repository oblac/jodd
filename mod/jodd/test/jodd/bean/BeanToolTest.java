// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean;

import junit.framework.TestCase;
import jodd.bean.data.Abean;

public class BeanToolTest extends TestCase {

	public void testTemplate() {
		Abean a = new Abean();

		assertEquals("xxxx", BeanTool.parseTemplate("xxxx", a));
		assertEquals("", BeanTool.parseTemplate("", a));
		assertEquals("...abean_value...", BeanTool.parseTemplate("...${fooProp}...", a));
		assertEquals("abean_value", BeanTool.parseTemplate("${fooProp}", a));

		assertEquals("...${fooProp}...", BeanTool.parseTemplate("...\\${fooProp}...", a));
		assertEquals("...\\abean_value...", BeanTool.parseTemplate("...\\\\${fooProp}...", a));
		assertEquals("...\\${fooProp}...", BeanTool.parseTemplate("...\\\\\\${fooProp}...", a));
		assertEquals("...\\\\abean_value...", BeanTool.parseTemplate("...\\\\\\\\${fooProp}...", a));
		assertEquals("...\\\\${fooProp}...", BeanTool.parseTemplate("...\\\\\\\\\\${fooProp}...", a));

		assertEquals("${fooProp}", BeanTool.parseTemplate("\\${fooProp}", a));
		assertEquals("\\abean_value", BeanTool.parseTemplate("\\\\${fooProp}", a));
		assertEquals("\\${fooProp}", BeanTool.parseTemplate("\\\\\\${fooProp}", a));
		assertEquals("\\\\abean_value", BeanTool.parseTemplate("\\\\\\\\${fooProp}", a));
		assertEquals("\\\\${fooProp}", BeanTool.parseTemplate("\\\\\\\\\\${fooProp}", a));

		assertEquals("abean_valueabean_value", BeanTool.parseTemplate("${fooProp}${fooProp}", a));
		assertEquals("${fooProp}abean_value", BeanTool.parseTemplate("\\${fooProp}${fooProp}", a));
	}
}
