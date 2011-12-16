// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import junit.framework.TestCase;
import jodd.petite.config.AutomagicPetiteConfigurator;
import jodd.petite.tst.Boo;
import jodd.petite.tst.BooC;
import jodd.petite.tst.BooC2;
import jodd.petite.tst.Foo;
import jodd.petite.tst.Zoo;
import jodd.petite.tst.Goo;
import jodd.petite.tst.Loo;
import jodd.petite.tst.Ioo;
import jodd.petite.tst.impl.DefaultIoo;
import jodd.petite.scope.ProtoScope;

import java.util.List;

public class WireTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Foo.instanceCounter = 0;
	}

	public void testContainer() {
		PetiteContainer pc = new PetiteContainer();
		AutomagicPetiteConfigurator configurator = new AutomagicPetiteConfigurator();
		configurator.setIncludedEntries("jodd.petite.*");
		configurator.setExcludedEntries("jodd.petite.data.*", "jodd.petite.test3.*");
		configurator.configure(pc);

		assertEquals(1, pc.getTotalBeans());
		assertEquals(1, pc.getTotalScopes());
		assertEquals(0, Foo.instanceCounter);

		Foo foo = (Foo) pc.getBean("foo");
		assertNotNull(foo);
		assertEquals(1, foo.hello());
		foo = (Foo) pc.getBean("foo");
		assertEquals(1, foo.hello());


		// register again the same class, but this time with proto scope
		pc.registerBean("foo2", Foo.class, ProtoScope.class);
		assertEquals(2, pc.getTotalBeans());
		assertEquals(2, pc.getTotalScopes());

		assertEquals(2, ((Foo) pc.getBean("foo2")).hello());
		assertEquals(3, ((Foo) pc.getBean("foo2")).hello());


		// register boo
		pc.registerBean(Boo.class);
		assertEquals(3, pc.getTotalBeans());
		assertEquals(2, pc.getTotalScopes());

		Boo boo;
		try {
			//noinspection UnusedAssignment
			boo = (Boo) pc.getBean("boo");
			fail();
		} catch (PetiteException pex) {
			// zoo class is missing
		}

		pc.registerBean(Zoo.class);
		assertEquals(4, pc.getTotalBeans());
		assertEquals(2, pc.getTotalScopes());

		boo = (Boo) pc.getBean("boo");
		assertNotNull(boo);
		assertNotNull(boo.getFoo());
		assertNotNull(boo.zoo);
		assertSame(boo.zoo.boo, boo);
		assertEquals(3, boo.getFoo().hello());
		assertEquals(1, boo.getFoo().getCounter());
	}

	public void testCreate() {
		PetiteContainer pc = new PetiteContainer();
		pc.registerBean(Foo.class);
		pc.registerBean(Zoo.class);
		pc.registerBean(Boo.class);
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

	public void testCtor() {
		PetiteContainer pc = new PetiteContainer();
		pc.registerBean(BooC.class);
		pc.registerBean(Foo.class);
		assertEquals(2, pc.getTotalBeans());
		assertEquals(1, pc.getTotalScopes());
		assertEquals(0, Foo.instanceCounter);

		BooC boo = (BooC) pc.getBean("booC");
		assertNotNull(boo);
		assertNotNull(boo.getFoo());
		assertEquals(1, boo.getFoo().hello());

		pc.registerBean("boo", BooC2.class);
		pc.registerBean(Zoo.class);
		assertEquals(4, pc.getTotalBeans());
		assertEquals(1, pc.getTotalScopes());
		assertEquals(1, Foo.instanceCounter);

		try {
			pc.getBean("boo");
			fail();
		} catch (PetiteException pex) {
			// ignore                       // cyclic dependency
		}
	}

	public void testAutowire() {
		PetiteContainer pc = new PetiteContainer();
		pc.registerBean(Goo.class, ProtoScope.class);
		pc.registerBean(Loo.class);

		assertEquals(2, pc.getTotalBeans());

		Goo goo = (Goo) pc.getBean("goo");
		assertNotNull(goo);
		assertNotNull(goo.looCustom);
		assertNull(goo.foo);

		pc.registerBean(Foo.class);
		goo = (Goo) pc.getBean("goo");
		assertNotNull(goo);
		assertNotNull(goo.looCustom);
		assertNull(goo.foo);

		pc = new PetiteContainer();
		pc.getConfig().setDefaultWiringMode(WiringMode.AUTOWIRE);
		pc.registerBean(Goo.class, ProtoScope.class);
		pc.registerBean(Loo.class);
		pc.registerBean(Foo.class);

		goo = (Goo) pc.getBean("goo");
		assertNotNull(goo);
		assertNotNull(goo.looCustom);
		assertNotNull(goo.foo);

		pc.removeBean(Goo.class);
	}

	public void testInterface() {
		PetiteContainer pc = new PetiteContainer();
		pc.registerBean(Foo.class);
		pc.registerBean("ioo", DefaultIoo.class);

		assertEquals(2, pc.getTotalBeans());
		Ioo ioo = (Ioo) pc.getBean("ioo");
		assertNotNull(ioo);
		assertNotNull(ioo.getFoo());
		assertEquals(DefaultIoo.class, ioo.getClass());
	}

	public void testSelf() {
		PetiteContainer pc = new PetiteContainer();
		pc.addSelf();

		assertEquals(1, pc.getTotalBeans());

		PetiteContainer pc2 = (PetiteContainer) pc.getBean(PetiteContainer.PETITE_CONTAINER_REF_NAME);
		assertEquals(pc2, pc);
		
	}

	public void testInit() {
		PetiteContainer pc = new PetiteContainer();
		pc.registerBean(Foo.class);
		pc.registerBean(Zoo.class);
		pc.registerBean(Boo.class);
		pc.registerBean("boo2", Boo.class);

		Boo boo = (Boo) pc.getBean("boo");
		assertNotNull(boo.getFoo());
		assertEquals(1, boo.getCount());

		Boo boo2 = (Boo) pc.getBean("boo2");
		assertNotSame(boo, boo2);
		assertEquals(1, boo2.getCount());

		assertSame(boo.getFoo(), boo2.getFoo());


		List<String> order = boo.orders;

		assertEquals(6, order.size());
		assertEquals("first", order.get(0));
		assertEquals("second", order.get(1));		// Collections.sort() is stable: equals methods are not reordered.
		assertEquals("third", order.get(2));
		assertEquals("init", order.get(3));
		assertEquals("beforeLast", order.get(4));
		assertEquals("last", order.get(5));
	}
}
