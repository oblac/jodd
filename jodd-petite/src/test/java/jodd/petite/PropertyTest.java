// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.petite.data.PojoBean2;
import jodd.petite.tst2.Joo;
import jodd.petite.tst2.Moo;
import jodd.props.Props;
import org.junit.Test;

import static org.junit.Assert.*;

public class PropertyTest {

	@Test
	public void testSet() {
		PetiteContainer pc = new PetiteContainer();
		pc.registerBean(PojoBean2.class);

		pc.setBeanProperty("pojoBean2.val1", "value");
		pc.setBeanProperty("pojoBean2.val2", "173");

		PojoBean2 pojo2 = (PojoBean2) pc.getBean("pojoBean2");
		assertEquals("value", pojo2.getVal1());
		assertEquals(173, pojo2.getVal2().intValue());
	}

	@Test
	public void testSetWithMultipleDots() {
		PetiteContainer pc = new PetiteContainer();

		pc.registerBean("pojo", PojoBean2.class);

		try {
			pc.setBeanProperty("poco", null);
			fail();
		} catch (PetiteException ignore) {
		}
		pc.setBeanProperty("pojo.val1", "value");
		pc.setBeanProperty("pojo.bean.name", "foo");

		PojoBean2 pojo2 = (PojoBean2) pc.getBean("pojo");
		assertEquals("value", pojo2.getVal1());
		assertEquals("foo", pojo2.getBean().getName());

		pc.registerBean("pojo.bean", PojoBean2.class);
		pc.setBeanProperty("pojo.bean.val1", "value");
		pc.setBeanProperty("pojo.bean.val2", "173");

		pojo2 = (PojoBean2) pc.getBean("pojo.bean");
		assertEquals("value", pojo2.getVal1());
		assertEquals(173, pojo2.getVal2().intValue());
	}

	@Test
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

	@Test
	public void testCount() {
		PetiteContainer pc = new PetiteContainer();
		pc.registerBean(Moo.class);
		pc.registerBean(Joo.class);
		Moo moo = pc.getBean(Moo.class);
		assertNotNull(moo.joo);
		assertNull(moo.jooNo);

		BeanDefinition bd = pc.lookupBeanDefinition("moo");
		assertEquals(1, bd.properties.length);


		pc = new PetiteContainer();
		pc.getConfig().setDefaultWiringMode(WiringMode.AUTOWIRE);
		pc.registerBean(Moo.class);
		pc.registerBean(Joo.class);

		moo = pc.getBean(Moo.class);
		assertNotNull(moo.joo);
		assertNotNull(moo.jooNo);
		assertEquals(moo.joo, moo.jooNo);

		bd = pc.lookupBeanDefinition("moo");
		assertEquals(2, bd.properties.length);
	}

	@Test
	public void testProps() {
		Props props = new Props();
		props.load("pojoBean2.val2=123");
		props.load("pojoBean2.val1=\\\\${pojo}");

		assertEquals("123", props.getValue("pojoBean2.val2"));
		assertEquals("\\${pojo}", props.getValue("pojoBean2.val1"));

		PetiteContainer pc = new PetiteContainer();
		pc.registerBean(PojoBean2.class);
		pc.defineParameters(props);

		PojoBean2 pojoBean2 = pc.getBean(PojoBean2.class);

		assertEquals(123, pojoBean2.getVal2().intValue());
		assertEquals("${pojo}", pojoBean2.getVal1());
	}
}
