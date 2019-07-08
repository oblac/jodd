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

import jodd.petite.fixtures.tst2.Joo;
import jodd.petite.fixtures.tst2.Koo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class FullTypeTest {

	private void registerBean(PetiteContainer petiteContainer, Class beanType) {
		petiteContainer.registerPetiteBean(beanType, null, null, null, false, null);
	}

	@Test
	void testFullTypeProperty() {
		PetiteContainer pc = new PetiteContainer();
		pc.config().setUseFullTypeNames(true);

		registerBean(pc, Koo.class);
		registerBean(pc, Joo.class);

		assertEquals(2, pc.beansCount());

		assertNull(pc.getBean("koo"));
		assertNull(pc.getBean("joo"));

		Koo koo = pc.getBean(Koo.class);
		assertNotNull(koo);
		Joo joo = pc.getBean(Joo.class);
		assertNotNull(joo);

		koo = pc.getBean(Koo.class.getName());
		assertNotNull(koo);
		joo = pc.getBean(Joo.class.getName());
		assertNotNull(joo);

		assertNotNull(koo.joo);
		assertNotNull(koo.someNoJooName);
		assertEquals(joo, koo.joo);
		assertEquals(joo, koo.someNoJooName);
	}

	@Test
	void testFullTypeMethodCtor() {
		PetiteContainer pc = new PetiteContainer();
		pc.config().setUseFullTypeNames(true);

		registerBean(pc, Koo.class);
		registerBean(pc, Joo.class);

		Koo koo = pc.getBean(Koo.class.getName());
		assertNotNull(koo);
		Joo joo = pc.getBean(Joo.class.getName());
		assertNotNull(joo);

		assertNotNull(koo.joo);
		assertNotNull(koo.someNoJooName);
		assertNotNull(koo.mjoo);
		assertNotNull(koo.mjoo2);
		assertNotNull(koo.joojoo);
	}

	@Test
	void testOptionalAndNotAllReferences() {
		PetiteContainer pc = new PetiteContainer();
		pc.config().setDefaultWiringMode(WiringMode.OPTIONAL);
		pc.config().setUseFullTypeNames(false);
		pc.config().setLookupReferences(PetiteReferenceType.NAME);

		registerBean(pc, Koo.class);
		registerBean(pc, Joo.class);

		assertEquals(2, pc.beansCount());

		Koo koo = pc.getBean(Koo.class);
		assertNotNull(koo);
		Joo joo = pc.getBean(Joo.class);
		assertNotNull(joo);

		assertNull(koo.someNoJooName);
		assertNotNull(koo.joo);

		koo = pc.getBean(Koo.class.getName());
		assertNull(koo);
		joo = pc.getBean(Joo.class.getName());
		assertNull(joo);

	}
}
