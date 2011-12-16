// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.petite.tst3.Batgirl;
import jodd.petite.tst3.Batman;
import jodd.petite.tst3.GothamCity;
import jodd.petite.tst3.Metropolis;
import jodd.petite.tst3.Superman;
import junit.framework.TestCase;

public class SetsTest extends TestCase {

	public void testEmptySet() {
		final PetiteContainer pc = new PetiteContainer();

		pc.registerBean(GothamCity.class);

		GothamCity gothamCity = pc.getBean(GothamCity.class);

		assertNotNull(gothamCity.superHeros);
		assertTrue(gothamCity.superHeros.isEmpty());
	}

	public void testOneHero() {
		final PetiteContainer pc = new PetiteContainer();

		pc.registerBean(Batman.class);
		pc.registerBean(GothamCity.class);

		GothamCity gothamCity = pc.getBean(GothamCity.class);

		assertNotNull(gothamCity.superHeros);
		assertFalse(gothamCity.superHeros.isEmpty());
		assertEquals(1, gothamCity.superHeros.size());

		String str = gothamCity.whoIsThere();
		assertEquals("Batman", str);
	}

	public void testTwoHeros() {
		final PetiteContainer pc = new PetiteContainer();

		pc.registerBean(Batman.class);
		pc.registerBean(GothamCity.class);
		pc.registerBean(Batgirl.class);

		GothamCity gothamCity = pc.getBean(GothamCity.class);

		assertNotNull(gothamCity.superHeros);
		assertFalse(gothamCity.superHeros.isEmpty());
		assertEquals(2, gothamCity.superHeros.size());

		String str = gothamCity.whoIsThere();
		assertTrue(str.contains("Batman"));
		assertTrue(str.contains("Batgirl"));
	}

	public void testCollection() {
		final PetiteContainer pc = new PetiteContainer();

		pc.registerBean(Superman.class);
		pc.registerBean(Metropolis.class);

		Metropolis metropolis = pc.getBean(Metropolis.class);

		assertNotNull(metropolis.superHeros);
		assertFalse(metropolis.superHeros.isEmpty());
		assertEquals(1, metropolis.superHeros.size());

		String str = metropolis.whoIsThere();
		assertTrue(str.contains("Superman"));
	}
}
