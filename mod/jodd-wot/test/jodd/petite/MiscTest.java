// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import junit.framework.TestCase;
import jodd.petite.data.DefaultBizImpl;
import jodd.petite.data.Biz;
import jodd.petite.data.DefaultBiz;
import jodd.petite.test.Foo;
import jodd.petite.test.Boo;
import jodd.petite.test.Zoo;

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

	public void testAdd2() {
		Foo.instanceCounter = 0;
		PetiteContainer pc = new PetiteContainer();
		pc.registerBean(Foo.class);
		pc.registerBean(Zoo.class);

		Foo foo = (Foo) pc.getBean("foo");
		Boo boo = new Boo();
		assertNull(boo.getFoo());
		pc.addBean("boo", boo, null, false);
		assertNotNull(boo.getFoo());
		assertSame(foo, boo.getFoo());

		Boo boo2 = (Boo) pc.getBean("boo");
		assertNotNull(boo2);
		assertSame(boo, boo2);
		assertTrue(boo.orders.isEmpty());
		assertNotNull(boo2.getFoo());
		assertSame(foo, boo2.getFoo());
		assertEquals(1, boo2.getFoo().hello());
		assertEquals(1, boo2.getFoo().getCounter());

		pc.addBean("boo", boo);
		boo2 = (Boo) pc.getBean("boo");
		assertNotNull(boo2);
		assertSame(boo, boo2);
		assertEquals("[first, second, third, init, beforeLast, last]", boo.orders.toString());
		assertNotNull(boo2.getFoo());
		assertSame(foo, boo2.getFoo());
		assertEquals(1, boo2.getFoo().hello());
		assertEquals(2, boo2.getFoo().getCounter());
	}

}
