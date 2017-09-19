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

import jodd.petite.config.AutomagicPetiteConfigurator;
import jodd.petite.scope.ProtoScope;
import jodd.petite.fixtures.tst.*;
import jodd.petite.fixtures.tst.impl.DefaultIoo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WireTest {

	@BeforeEach
	public void setUp() throws Exception {
		Foo.instanceCounter = 0;
	}

	@Test
	public void testContainer() {
		PetiteContainer pc = new PetiteContainer();
		AutomagicPetiteConfigurator configurator = new AutomagicPetiteConfigurator();
		configurator.setExcludeAllEntries(true);
		configurator.setIncludedEntries("jodd.petite.fixtures.*");
		configurator.setExcludedEntries("jodd.petite.fixtures.data.*", "jodd.petite.fixtures.tst3.*", "jodd.petite.fixtures.tst.Ses");
		configurator.setExcludedEntries(
				"jodd.petite.fixtures.data.*", "jodd.petite.fixtures.tst3.*", "jodd.petite.fixtures.tst.Ses", "*Public*", "*Secret*", "*$*",
			"jodd.petite.proxy.*");
		configurator.configure(pc);

		assertEquals(1, pc.getTotalBeans());
		assertEquals(1, pc.getTotalScopes());
		assertEquals(0, Foo.instanceCounter);

		Foo foo = pc.getBean("foo");
		assertNotNull(foo);
		assertEquals(1, foo.hello());
		foo = pc.getBean("foo");
		assertEquals(1, foo.hello());


		// register again the same class, but this time with proto scope
		pc.registerPetiteBean(Foo.class, "foo2", ProtoScope.class, null, false);
		assertEquals(2, pc.getTotalBeans());
		assertEquals(2, pc.getTotalScopes());

		assertEquals(2, ((Foo) pc.getBean("foo2")).hello());
		assertEquals(3, ((Foo) pc.getBean("foo2")).hello());


		// register boo
		pc.registerPetiteBean(Boo.class, null, null, null, false);
		assertEquals(3, pc.getTotalBeans());
		assertEquals(2, pc.getTotalScopes());

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
		pc.registerPetiteBean(Zoo.class, null, null, null, false);
		pc.removeBean(Boo.class);
		pc.registerPetiteBean(Boo.class, null, null, null, false);

		assertEquals(4, pc.getTotalBeans());
		assertEquals(2, pc.getTotalScopes());

		boo = pc.getBean("boo");
		assertNotNull(boo);
		assertNotNull(boo.getFoo());
		assertNotNull(boo.zoo);
		assertSame(boo.zoo.boo, boo);
		assertEquals(3, boo.getFoo().hello());
		assertEquals(2, boo.getFoo().getCounter());        // '2' because the first time we getBean('boo') the wiring occurred before exception was throwed!
	}

	@Test
	public void testCreate() {
		PetiteContainer pc = new PetiteContainer();
		pc.registerPetiteBean(Foo.class, null, null, null, false);
		pc.registerPetiteBean(Zoo.class, null, null, null, false);
		pc.registerPetiteBean(Boo.class, null, null, null, false);
		assertEquals(3, pc.getTotalBeans());
		assertEquals(1, pc.getTotalScopes());
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
	public void testCtor() {
		PetiteContainer pc = new PetiteContainer();
		pc.registerPetiteBean(BooC.class, null, null, null, false);
		pc.registerPetiteBean(Foo.class, null, null, null, false);
		assertEquals(2, pc.getTotalBeans());
		assertEquals(1, pc.getTotalScopes());
		assertEquals(0, Foo.instanceCounter);

		BooC boo = pc.getBean("booC");
		assertNotNull(boo);
		assertNotNull(boo.getFoo());
		assertEquals(1, boo.getFoo().hello());

		pc.registerPetiteBean(BooC2.class, "boo", null, null, false);
		pc.registerPetiteBean(Zoo.class, null, null, null, false);
		assertEquals(4, pc.getTotalBeans());
		assertEquals(1, pc.getTotalScopes());
		assertEquals(1, Foo.instanceCounter);

		try {
			pc.getBean("boo");
			fail("error");
		} catch (PetiteException pex) {
			// ignore                       // cyclic dependency
		}
	}

	@Test
	public void testAutowire() {
		PetiteContainer pc = new PetiteContainer();
		pc.registerPetiteBean(Goo.class, null, ProtoScope.class, null, false);
		pc.registerPetiteBean(Loo.class, null, null, null, false);

		assertEquals(2, pc.getTotalBeans());

		Goo goo = pc.getBean("goo");
		assertNotNull(goo);
		assertNotNull(goo.looCustom);
		assertNull(goo.foo);

		pc.registerPetiteBean(Foo.class, null, null, null, false);
		goo = pc.getBean("goo");
		assertNotNull(goo);
		assertNotNull(goo.looCustom);
		assertNull(goo.foo);

		pc = new PetiteContainer();
		pc.getConfig().setDefaultWiringMode(WiringMode.AUTOWIRE);
		pc.registerPetiteBean(Goo.class, null, ProtoScope.class, null, false);
		pc.registerPetiteBean(Loo.class, null, null, null, false);
		pc.registerPetiteBean(Foo.class, null, null, null, false);

		goo = pc.getBean("goo");
		assertNotNull(goo);
		assertNotNull(goo.looCustom);
		assertNotNull(goo.foo);

		pc.removeBean(Goo.class);
	}

	@Test
	public void testInterface() {
		PetiteContainer pc = new PetiteContainer();
		pc.registerPetiteBean(Foo.class, null, null, null, false);
		pc.registerPetiteBean(DefaultIoo.class, "ioo", null, null, false);

		assertEquals(2, pc.getTotalBeans());
		Ioo ioo = pc.getBean("ioo");
		assertNotNull(ioo);
		assertNotNull(ioo.getFoo());
		assertEquals(DefaultIoo.class, ioo.getClass());
	}

	@Test
	public void testSelf() {
		PetiteContainer pc = new PetiteContainer();
		pc.addSelf();

		assertEquals(1, pc.getTotalBeans());

		PetiteContainer pc2 = pc.getBean(PetiteContainer.PETITE_CONTAINER_REF_NAME);
		assertEquals(pc2, pc);

	}

	@Test
	public void testInit() {
		PetiteContainer pc = new PetiteContainer();
		pc.registerPetiteBean(Foo.class, null, null, null, false);
		pc.registerPetiteBean(Zoo.class, null, null, null, false);
		pc.registerPetiteBean(Boo.class, null, null, null, false);
		pc.registerPetiteBean(Boo.class, "boo2", null, null, false);

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
