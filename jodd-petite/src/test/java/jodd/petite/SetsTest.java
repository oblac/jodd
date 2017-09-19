// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.petite;

import jodd.petite.fixtures.tst3.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SetsTest {

	@Test
	public void testEmptySet() {
		final PetiteContainer pc = new PetiteContainer();

		pc.registerPetiteBean(GothamCity.class, null, null, null, false);

		GothamCity gothamCity = pc.getBean(GothamCity.class);

		assertNotNull(gothamCity.superHeros);
		assertTrue(gothamCity.superHeros.isEmpty());
	}

	@Test
	public void testOneHero() {
		final PetiteContainer pc = new PetiteContainer();

		pc.registerPetiteBean(Batman.class, null, null, null, false);
		pc.registerPetiteBean(GothamCity.class, null, null, null, false);

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

		pc.registerPetiteBean(Batman.class, null, null, null, false);
		pc.registerPetiteBean(GothamCity.class, null, null, null, false);
		pc.registerPetiteBean(Batgirl.class, null, null, null, false);

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

		pc.registerPetiteBean(Superman.class, null, null, null, false);
		pc.registerPetiteBean(Metropolis.class, null, null, null, false);

		Metropolis metropolis = pc.getBean(Metropolis.class);

		assertNotNull(metropolis.superHeros);
		assertFalse(metropolis.superHeros.isEmpty());
		assertEquals(1, metropolis.superHeros.size());

		String str = metropolis.whoIsThere();
		assertTrue(str.contains("Superman"));
	}

}
