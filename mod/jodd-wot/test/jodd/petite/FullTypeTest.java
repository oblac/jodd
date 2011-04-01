// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.petite.test2.Joo;
import jodd.petite.test2.Koo;
import junit.framework.TestCase;

public class FullTypeTest extends TestCase {

	public void testFullType() {
		PetiteContainer pc = new PetiteContainer();
		pc.getConfig().setUseFullTypeNames(true);

		pc.registerBean(Koo.class);
		pc.registerBean(Joo.class);

		assertEquals(2, pc.getTotalBeans());

		assertNull(pc.getBean("koo"));
		assertNull(pc.getBean("joo"));

		Koo koo = pc.getBean(Koo.class);
		assertNotNull(koo);
		Joo joo = pc.getBean(Joo.class);
		assertNotNull(joo);

		koo = (Koo) pc.getBean(Koo.class.getName());
		assertNotNull(koo);
		joo = (Joo) pc.getBean(Joo.class.getName());
		assertNotNull(joo);

		assertNotNull(koo.joo);
		assertEquals(joo, koo.joo);
	}
}
