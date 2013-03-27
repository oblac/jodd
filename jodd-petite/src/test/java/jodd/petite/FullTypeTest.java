// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.petite.tst2.Joo;
import jodd.petite.tst2.Koo;
import org.junit.Test;

import static org.junit.Assert.*;

public class FullTypeTest {

	private void registerBean(PetiteContainer petiteContainer, Class beanType) {
		petiteContainer.registerPetiteBean(null, beanType, null, null, false);
	}

	@Test
	public void testFullTypeProperty() {
		PetiteContainer pc = new PetiteContainer();
		pc.getConfig().setUseFullTypeNames(true);

		registerBean(pc, Koo.class);
		registerBean(pc, Joo.class);

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
		assertNotNull(koo.someNoJooName);
		assertEquals(joo, koo.joo);
		assertEquals(joo, koo.someNoJooName);
	}

	@Test
	public void testFullTypeMethodCtor() {
		PetiteContainer pc = new PetiteContainer();
		pc.getConfig().setUseFullTypeNames(true);

		registerBean(pc, Koo.class);
		registerBean(pc, Joo.class);

		Koo koo = (Koo) pc.getBean(Koo.class.getName());
		assertNotNull(koo);
		Joo joo = (Joo) pc.getBean(Joo.class.getName());
		assertNotNull(joo);

		assertNotNull(koo.joo);
		assertNotNull(koo.someNoJooName);
		assertNotNull(koo.mjoo);
		assertNotNull(koo.mjoo2);
		assertNotNull(koo.joojoo);
	}

	@Test
	public void testOptionalAndNotAllReferences() {
		PetiteContainer pc = new PetiteContainer();
		pc.getConfig().setDefaultWiringMode(WiringMode.OPTIONAL);
		pc.getConfig().setUseFullTypeNames(false);
		pc.getConfig().setLookupReferences(PetiteReference.NAME);

		registerBean(pc, Koo.class);
		registerBean(pc, Joo.class);

		assertEquals(2, pc.getTotalBeans());

		Koo koo = pc.getBean(Koo.class);
		assertNotNull(koo);
		Joo joo = pc.getBean(Joo.class);
		assertNotNull(joo);

		assertNull(koo.someNoJooName);
		assertNotNull(koo.joo);

		koo = (Koo) pc.getBean(Koo.class.getName());
		assertNull(koo);
		joo = (Joo) pc.getBean(Joo.class.getName());
		assertNull(joo);

	}
}