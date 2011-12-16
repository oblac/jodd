// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import junit.framework.TestCase;
import jodd.petite.data.DefaultBizImpl;
import jodd.petite.data.Biz;
import jodd.petite.data.DefaultBiz;
import jodd.petite.tst.Foo;
import jodd.petite.tst.Boo;
import jodd.petite.tst.Zoo;

public class MiscTest extends TestCase {

	public void testOne() {
		PetiteContainer pc = new PetiteContainer();
		pc.registerBean(DefaultBizImpl.class);
		assertEquals(1, pc.getTotalBeans());

		Object bizI = pc.getBean("biz");
		assertTrue(bizI instanceof Biz);
		assertTrue(bizI instanceof DefaultBizImpl);

		pc = new PetiteContainer();
		pc.registerBean(DefaultBizImpl.class);
		pc.registerBean(DefaultBiz.class);			// override!
		assertEquals(1, pc.getTotalBeans());
		pc.registerBean(Foo.class);
		pc.registerPropertyInjectionPoint("biz", "foo");
		pc.registerInitMethods("biz", "init", "init2");
		
		assertEquals(2, pc.getTotalBeans());
		bizI = pc.getBean("biz");
		assertTrue(bizI instanceof Biz);
		assertFalse(bizI instanceof DefaultBizImpl);
		assertTrue(bizI instanceof DefaultBiz);

		assertNotNull(((DefaultBiz) bizI).getFoo());
		assertEquals(2, ((DefaultBiz) bizI).initCount);
	}

	public void testTwo() {
		PetiteContainer pc = new PetiteContainer();
		pc.registerBean(DefaultBizImpl.class);
		assertEquals(1, pc.getTotalBeans());

		Object bizI = pc.getBean("biz");
		assertTrue(bizI instanceof Biz);
		assertFalse(bizI instanceof DefaultBiz);
		assertTrue(bizI instanceof DefaultBizImpl);

		//pc = new PetiteContainer();			// same container!!!
		pc.registerBean(DefaultBiz.class);			// override! instance will be removed from the scope
		assertEquals(1, pc.getTotalBeans());
		bizI = pc.getBean("biz");
		assertTrue(bizI instanceof Biz);
		assertFalse(bizI instanceof DefaultBizImpl);
		assertTrue(bizI instanceof DefaultBiz);
	}


	public void testAdd() {
		PetiteContainer pc = new PetiteContainer();
		Foo foo = new Foo();
		pc.addBean("foo", foo);
		Foo foo2 = (Foo) pc.getBean("foo");
		assertNotNull(foo2);
		assertSame(foo, foo2);
	}

	public void testAdd2WithCircDep() {
		Foo.instanceCounter = 0;
		PetiteContainer pc = new PetiteContainer();
		pc.registerBean(Foo.class);
		pc.registerBean(Zoo.class);

		Foo foo = (Foo) pc.getBean("foo");
		Boo boo = new Boo();
		assertNull(boo.getFoo());

		pc.addBean("boo", boo, null);
		assertNotNull(boo.getFoo());
		assertSame(foo, boo.getFoo());
		assertNotNull(boo.zoo);

		Zoo zoo = (Zoo) pc.getBean("zoo");
		assertNotNull(zoo.boo);
		assertSame(zoo, boo.zoo);		// circular dependecy
		assertSame(boo, zoo.boo);

		Boo boo2 = (Boo) pc.getBean("boo");
		assertNotNull(boo2);
		assertSame(boo, boo2);
		assertFalse(boo.orders.isEmpty());
		assertEquals(6, boo.orders.size());
		assertEquals("[first, second, third, init, beforeLast, last]", boo.orders.toString());
		assertNotNull(boo2.getFoo());
		assertSame(foo, boo2.getFoo());
		assertEquals(1, boo2.getFoo().hello());
		assertEquals(1, boo2.getFoo().getCounter());

		pc.addBean("boo", boo);
		boo2 = (Boo) pc.getBean("boo");
		assertNotNull(boo2);
		assertSame(boo, boo2);
		assertNotNull(boo2.getFoo());
		assertSame(foo, boo2.getFoo());
		assertEquals(1, boo2.getFoo().hello());
		assertEquals(2, boo2.getFoo().getCounter());
		assertEquals(12, boo.orders.size());		// init methods are called again due to re-add
	}

	public void testNoAdd2WithCircDep() {
		Foo.instanceCounter = 0;
		PetiteContainer pc = new PetiteContainer();
		pc.registerBean(Foo.class);
		pc.registerBean(Zoo.class);
		pc.registerBean(Boo.class);

		Boo boo = (Boo) pc.getBean("boo");
		Foo foo = (Foo) pc.getBean("foo");
		Zoo zoo = (Zoo) pc.getBean("zoo");

		assertNotNull(boo.getFoo());
		assertSame(foo, boo.getFoo());

		assertNotNull(zoo.boo);
		assertSame(boo, zoo.boo);
		assertSame(zoo, boo.zoo);

		Boo boo2 = (Boo) pc.getBean("boo");
		assertNotNull(boo2);
		assertSame(boo, boo2);
		assertFalse(boo.orders.isEmpty());
		assertNotNull(boo2.getFoo());
		assertSame(foo, boo2.getFoo());
		assertEquals(1, boo2.getFoo().hello());
		assertEquals(1, boo2.getFoo().getCounter());
		assertEquals("[first, second, third, init, beforeLast, last]", boo.orders.toString());
	}

}
