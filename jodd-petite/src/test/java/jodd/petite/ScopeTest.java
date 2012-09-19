// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.petite.scope.ThreadLocalScope;
import jodd.petite.tst.Boo;
import jodd.petite.tst.Foo;
import jodd.petite.tst.Zoo;
import jodd.util.ConcurrentUtil;
import junit.framework.TestCase;

import java.util.concurrent.Semaphore;

public class ScopeTest extends TestCase {

	public void testThreadLocalScope() {
		final PetiteContainer pc = new PetiteContainer();

		pc.registerBean("foo", Foo.class);
		pc.registerBean(Zoo.class);
		pc.registerBean(Boo.class, ThreadLocalScope.class);

		assertEquals(3, pc.getTotalBeans());
		assertEquals(2, pc.getTotalScopes());

		final Boo boo = (Boo) pc.getBean("boo");
		final Foo foo = (Foo) pc.getBean("foo");
		assertSame(boo.getFoo(), foo);


		final Semaphore sem = new Semaphore(1);
		ConcurrentUtil.acquire(sem);

		Thread thread = new Thread() {
			@Override
			public void run() {
				Boo boo2 = (Boo) pc.getBean("boo");
				Foo foo2 = (Foo) pc.getBean("foo");
				assertSame(foo2, foo);
				assertNotSame(boo2, boo);
				assertSame(foo2, boo2.getFoo());
				sem.release();
			}
		};
		thread.start();
		ConcurrentUtil.waitForRelease(sem);
	}

}
