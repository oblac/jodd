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

import jodd.petite.fixtures.tst5.Solar;
import jodd.petite.fixtures.tst5.Solar2;
import jodd.petite.fixtures.tst5.Solar3;
import jodd.petite.fixtures.tst5.Sun;
import jodd.petite.fixtures.tst5.Sun2;
import jodd.petite.fixtures.tst5.Planet;
import jodd.util.ClassUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProviderTest {

	@Test
	public void testInstanceMethodProvider() {
		PetiteContainer pc = new PetiteContainer();

		pc.registerPetiteBean(Solar.class, null, null, null, false);
		pc.registerPetiteBean(Sun.class, null, null, null, false);

		Sun sun = pc.getBean(Sun.class);

		assertEquals("Sun{Earth}", sun.toString());
	}


	@Test
	public void testInstanceMethodProviderManualRegistration() {
		PetiteContainer pc = new PetiteContainer();

		pc.registerPetiteBean(Solar2.class, null, null, null, false);
		pc.registerPetiteBean(Sun2.class, null, null, null, false);

		pc.registerPetiteProvider("planet", "solar2", "planetProvider", ClassUtil.EMPTY_CLASS_ARRAY);
		pc.registerPetitePropertyInjectionPoint("sun2", "planet", null);

		Sun2 sun = pc.getBean(Sun2.class);

		assertEquals("Sun{Earth}", sun.toString());
	}


	@Test
	public void testInstanceStaticMethodProvider() {
		PetiteContainer pc = new PetiteContainer();

		pc.registerPetiteBean(Solar3.class, null, null, null, false);	// still needs to be a bean
		pc.registerPetiteBean(Sun.class, null, null, null, false);

		Sun sun = pc.getBean(Sun.class);

		assertEquals("Sun{Earth}", sun.toString());
	}



	@Test
	public void testProviderLookup() {
		PetiteContainer pc = new PetiteContainer();

		pc.registerPetiteBean(Solar3.class, null, null, null, false);

		Planet earth = (Planet) pc.getBean("planet");

		assertEquals("Earth", earth.toString());
	}

}
