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

import jodd.petite.fixtures.rainbow.Blue;
import jodd.petite.fixtures.rainbow.Green;
import jodd.petite.fixtures.rainbow.Yellow;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WiringModeTest {

	@Test
	void testWireMode_none() {
		PetiteContainer pc = new PetiteContainer();

		final WiringMode wiringMode = WiringMode.NONE;
		pc.registerPetiteBean(Green.class, null, null, wiringMode, false, null);
		pc.registerPetiteBean(Blue.class, null, null, wiringMode, false, null);
		pc.registerPetiteBean(Yellow.class, null, null, wiringMode, false, null);

		Green green = pc.getBean("green");

		assertNotNull(green);
		assertNull(green.blue);
		assertNull(green.yellow);
	}

	@Test
	void testWireMode_strict() {
		PetiteContainer pc = new PetiteContainer();

		final WiringMode wiringMode = WiringMode.STRICT;
		pc.registerPetiteBean(Green.class, null, null, wiringMode, false, null);
		pc.registerPetiteBean(Blue.class, null, null, wiringMode, false, null);
		pc.registerPetiteBean(Yellow.class, null, null, wiringMode, false, null);

		assertThrows(PetiteException.class, () -> pc.getBean("green"));
	}

	@Test
	void testWireMode_optional() {
		PetiteContainer pc = new PetiteContainer();

		final WiringMode wiringMode = WiringMode.OPTIONAL;
		pc.registerPetiteBean(Green.class, null, null, wiringMode, false, null);
		pc.registerPetiteBean(Blue.class, null, null, wiringMode, false, null);
		pc.registerPetiteBean(Yellow.class, null, null, wiringMode, false, null);

		Green green = pc.getBean("green");

		assertNotNull(green);
		assertNotNull(green.blue);
		assertNull(green.yellow);
		assertNull(green.red);
	}

	@Test
	void testWireMode_auto() {
		PetiteContainer pc = new PetiteContainer();

		final WiringMode wiringMode = WiringMode.AUTOWIRE;
		pc.registerPetiteBean(Green.class, null, null, wiringMode, false, null);
		pc.registerPetiteBean(Blue.class, null, null, wiringMode, false, null);
		pc.registerPetiteBean(Yellow.class, null, null, wiringMode, false, null);

		Green green = pc.getBean("green");

		assertNotNull(green);
		assertNotNull(green.blue);
		assertNotNull(green.yellow);
		assertNull(green.red);
	}

}
