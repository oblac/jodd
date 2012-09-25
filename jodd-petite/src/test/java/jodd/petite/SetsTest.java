// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.petite.tst3.*;
import org.junit.Test;

import static org.junit.Assert.*;

public class SetsTest {

	@Test
	public void testEmptySet() {
		final PetiteContainer pc = new PetiteContainer();

		pc.registerBean(GothamCity.class);

		GothamCity gothamCity = pc.getBean(GothamCity.class);

		assertNotNull(gothamCity.superHeros);
		assertTrue(gothamCity.superHeros.isEmpty());
	}

	@Test
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

	@Test
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

	@Test
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
