// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.petite.tst5.Solar;
import jodd.petite.tst5.Solar2;
import jodd.petite.tst5.Solar3;
import jodd.petite.tst5.Sun;
import jodd.petite.tst5.Sun2;
import jodd.petite.tst5.Planet;
import jodd.util.ReflectUtil;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class ProviderTest {

	@Test
	public void testInstanceMethodProvider() {
		PetiteContainer pc = new PetiteContainer();

		pc.registerBean(Solar.class);
		pc.registerBean(Sun.class);

		Sun sun = pc.getBean(Sun.class);

		assertEquals("Sun{Earth}", sun.toString());
	}

	@Test
	public void testInstanceMethodProviderManualRegistration() {
		PetiteContainer pc = new PetiteContainer();

		pc.registerBean(Solar2.class);
		pc.registerBean(Sun2.class);

		pc.registerPetiteProvider("planet", "solar2", "planetProvider", ReflectUtil.NO_PARAMETERS);
		pc.registerPropertyInjectionPoint("sun2", "planet");

		Sun2 sun = pc.getBean(Sun2.class);

		assertEquals("Sun{Earth}", sun.toString());
	}

	@Test
	public void testInstanceStaticMethodProvider() {
		PetiteContainer pc = new PetiteContainer();

		pc.registerBean(Solar3.class);	// still needs to be a bean
		pc.registerBean(Sun.class);

		Sun sun = pc.getBean(Sun.class);

		assertEquals("Sun{Earth}", sun.toString());
	}

	@Test
	public void testProviderLookup() {
		PetiteContainer pc = new PetiteContainer();

		pc.registerBean(Solar3.class);

		Planet earth = (Planet) pc.getBean("planet");

		assertEquals("Earth", earth.toString());
	}

}