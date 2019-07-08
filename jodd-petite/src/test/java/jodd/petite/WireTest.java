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

import jodd.petite.fixtures.tst.Boo;
import jodd.petite.fixtures.tst.BooC;
import jodd.petite.fixtures.tst.BooC2;
import jodd.petite.fixtures.tst.Foo;
import jodd.petite.fixtures.tst.Goo;
import jodd.petite.fixtures.tst.Ioo;
import jodd.petite.fixtures.tst.Loo;
import jodd.petite.fixtures.tst.Zoo;
import jodd.petite.fixtures.tst.impl.DefaultIoo;
import jodd.petite.scope.ProtoScope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

class WireTest {

	@BeforeEach
	void setUp() {
		Foo.instanceCounter = 0;
	}

	@Test
	void testContainer() {
		PetiteContainer pc = new PetiteContainer();
		pc.registerPetiteBean(Foo.class);

		assertEquals(1, pc.beansCount());
		assertEquals(1, pc.scopesCount());
		assertEquals(0, Foo.instanceCounter);

		Foo foo = pc.getBean("foo");
		assertNotNull(foo);
		assertEquals(1, foo.hello());
		foo = pc.getBean("foo");
		assertEquals(1, foo.hello());


		// register again the same class, but this time with proto scope
		pc.registerPetiteBean(Foo.class, "foo2", ProtoScope.class, null, false, null);
		assertEquals(2, pc.beansCount());
		assertEquals(2, pc.scopesCount());

		assertEquals(2, ((Foo) pc.getBean("foo2")).hello());
		assertEquals(3, ((Foo) pc.getBean("foo2")).hello());


		// register boo
		pc.registerPetiteBean(Boo.class, null, null, null, false, null);
		assertEquals(3, pc.beansCount());
		assertEquals(2, pc.scopesCount());

		Boo boo;
		try {
			//noinspection UnusedAssignment
			boo = pc.getBean("boo");
			fail("error");
		} catch (PetiteException pex) {
			// zoo class is missing
		}


		// registering missing dependency.
		// however we need to remove existing bean that requires this dependency
		// as it has been already initialized.
		pc.registerPetiteBean(Zoo.class, null, null, null, false, null);
		pc.removeBean(Boo.class);
		pc.registerPetiteBean(Boo.class, null, null, null, false, null);

		assertEquals(4, pc.beansCount());
		assertEquals(2, pc.scopesCount());

		boo = pc.getBean("boo");
		assertNotNull(boo);
		assertNotNull(boo.getFoo());
		assertNotNull(boo.zoo);
		assertSame(boo.zoo.boo, boo);
		assertEquals(3, boo.getFoo().hello());
		assertEquals(2, boo.getFoo().getCounter());        // '2' because the first time we getBean('boo') the wiring occurred before exception was throwed!
	}

	@Test
	void testCreate() {
		PetiteContainer pc = new PetiteContainer();
		pc.registerPetiteBean(Foo.class, null, null, null, false, null);
		pc.registerPetiteBean(Zoo.class, null, null, null, false, null);
		pc.registerPetiteBean(Boo.class, null, null, null, false, null);
		assertEquals(3, pc.beansCount());
		assertEquals(1, pc.scopesCount());
		assertEquals(0, Foo.instanceCounter);

		Boo boo = pc.createBean(Boo.class);
		assertNotNull(boo);
		assertNotNull(boo.getFoo());
		assertNotNull(boo.zoo);
		assertNotSame(boo.zoo.boo, boo);        // not equal instances!!!
		assertEquals(1, boo.getFoo().hello());
		assertEquals(1, boo.getCount());
	}

	@Test
	void testCtor() {
		PetiteContainer pc = new PetiteContainer();
		pc.registerPetiteBean(BooC.class, null, null, null, false, null);
		pc.registerPetiteBean(Foo.class, null, null, null, false, null);
		assertEquals(2, pc.beansCount());
		assertEquals(1, pc.scopesCount());
		assertEquals(0, Foo.instanceCounter);

		BooC boo = pc.getBean("booC");
		assertNotNull(boo);
		assertNotNull(boo.getFoo());
		assertEquals(1, boo.getFoo().hello());

		pc.registerPetiteBean(BooC2.class, "boo", null, null, false, null);
		pc.registerPetiteBean(Zoo.class, null, null, null, false, null);
		assertEquals(4, pc.beansCount());
		assertEquals(1, pc.scopesCount());
		assertEquals(1, Foo.instanceCounter);

		try {
			pc.getBean("boo");
			fail("error");
		} catch (PetiteException pex) {
			// ignore                       // cyclic dependency
		}
	}

	@Test
	void testAutowire() {
		PetiteContainer pc = new PetiteContainer();
		pc.registerPetiteBean(Goo.class, null, ProtoScope.class, null, false, null);
		pc.registerPetiteBean(Loo.class, null, null, null, false, null);

		assertEquals(2, pc.beansCount());

		Goo goo = pc.getBean("goo");
		assertNotNull(goo);
		assertNotNull(goo.looCustom);
		assertNull(goo.foo);

		pc.registerPetiteBean(Foo.class, null, null, null, false, null);
		goo = pc.getBean("goo");
		assertNotNull(goo);
		assertNotNull(goo.looCustom);
		assertNull(goo.foo);

		pc = new PetiteContainer();
		pc.config().setDefaultWiringMode(WiringMode.AUTOWIRE);
		pc.registerPetiteBean(Goo.class, null, ProtoScope.class, null, false, null);
		pc.registerPetiteBean(Loo.class, null, null, null, false, null);
		pc.registerPetiteBean(Foo.class, null, null, null, false, null);

		goo = pc.getBean("goo");
		assertNotNull(goo);
		assertNotNull(goo.looCustom);
		assertNotNull(goo.foo);

		pc.removeBean(Goo.class);
	}

	@Test
	void testInterface() {
		PetiteContainer pc = new PetiteContainer();
		pc.registerPetiteBean(Foo.class, null, null, null, false, null);
		pc.registerPetiteBean(DefaultIoo.class, "ioo", null, null, false, null);

		assertEquals(2, pc.beansCount());
		Ioo ioo = pc.getBean("ioo");
		assertNotNull(ioo);
		assertNotNull(ioo.getFoo());
		assertEquals(DefaultIoo.class, ioo.getClass());
	}

	@Test
	void testSelf() {
		PetiteContainer pc = new PetiteContainer();
		pc.addSelf();

		assertEquals(1, pc.beansCount());

		PetiteContainer pc2 = pc.getBean(PetiteContainer.PETITE_CONTAINER_REF_NAME);
		assertEquals(pc2, pc);

	}

	@Test
	void testInit() {
		PetiteContainer pc = new PetiteContainer();
		pc.registerPetiteBean(Foo.class, null, null, null, false, null);
		pc.registerPetiteBean(Zoo.class, null, null, null, false, null);
		pc.registerPetiteBean(Boo.class, null, null, null, false, null);
		pc.registerPetiteBean(Boo.class, "boo2", null, null, false, null);

		Boo boo = pc.getBean("boo");
		assertNotNull(boo.getFoo());
		assertEquals(1, boo.getCount());

		Boo boo2 = pc.getBean("boo2");
		assertNotSame(boo, boo2);
		assertEquals(1, boo2.getCount());

		assertSame(boo.getFoo(), boo2.getFoo());


		List<String> order = boo.orders;

		assertEquals(6, order.size());
		assertEquals("first", order.get(0));
		assertEquals("second", order.get(1));        // Collections.sort() is stable: equals methods are not reordered.
		assertEquals("third", order.get(2));
		assertEquals("init", order.get(3));
		assertEquals("beforeLast", order.get(4));
		assertEquals("last", order.get(5));
	}

}
