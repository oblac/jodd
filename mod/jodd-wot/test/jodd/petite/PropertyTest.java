// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import junit.framework.TestCase;
import jodd.petite.data.PojoBean2;

public class PropertyTest extends TestCase {

	public void testSet() {
		PetiteContainer pc = new PetiteContainer();
		pc.registerBean(PojoBean2.class);

		pc.setBeanProperty("pojoBean2.val1", "value");
		pc.setBeanProperty("pojoBean2.val2", "173");

		PojoBean2 pojo2 = (PojoBean2) pc.getBean("pojoBean2");
		assertEquals("value", pojo2.getVal1());
		assertEquals(173, pojo2.getVal2().intValue());

	}

	public void testGet() {
		PetiteContainer pc = new PetiteContainer();
		pc.registerBean(PojoBean2.class);

		PojoBean2 pojo2 = (PojoBean2) pc.getBean("pojoBean2");
		pojo2.setVal1("value");
		pojo2.setVal2(Integer.valueOf(173));

		pc.setBeanProperty("pojoBean2.val1", "value");
		pc.setBeanProperty("pojoBean2.val2", "173");


		assertEquals("value", pc.getBeanProperty("pojoBean2.val1"));
		assertEquals(Integer.valueOf(173), pc.getBeanProperty("pojoBean2.val2"));

	}
}
