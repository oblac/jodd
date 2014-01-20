// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.petite.scope.ProtoScope;
import jodd.petite.scope.RequestScope;
import jodd.petite.scope.SessionScope;
import jodd.petite.scope.SingletonScope;
import jodd.petite.scope.ThreadLocalScope;
import jodd.petite.tst.Boo;
import jodd.petite.tst.Foo;
import jodd.petite.tst.Zoo;
import org.junit.Test;

import java.util.concurrent.Semaphore;

import static org.junit.Assert.*;

public class ScopeTest {

	@Test
	public void testThreadLocalScope() throws InterruptedException {
		final PetiteContainer pc = new PetiteContainer();

		pc.registerPetiteBean(Foo.class, "foo", null, null, false);
		pc.registerPetiteBean(Zoo.class, null, null, null, false);
		pc.registerPetiteBean(Boo.class, null, ThreadLocalScope.class, null, false);

		assertEquals(3, pc.getTotalBeans());
		assertEquals(2, pc.getTotalScopes());

		final Boo boo = (Boo) pc.getBean("boo");
		final Foo foo = (Foo) pc.getBean("foo");
		assertSame(boo.getFoo(), foo);


		final Semaphore sem = new Semaphore(1);
		sem.acquire();

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
		sem.acquire();
		sem.release();
	}

	@Test
	public void testScopeAccept() {
		final PetiteContainer pc = new PetiteContainer();

		SingletonScope singletonScope = pc.resolveScope(SingletonScope.class);
		ProtoScope protoScope = pc.resolveScope(ProtoScope.class);
		SessionScope sessionScope = pc.resolveScope(SessionScope.class);
		RequestScope requestScope = pc.resolveScope(RequestScope.class);

		assertTrue(singletonScope.accept(singletonScope));
		assertFalse(singletonScope.accept(protoScope));
		assertFalse(singletonScope.accept(sessionScope));
		assertFalse(singletonScope.accept(requestScope));

		assertTrue(protoScope.accept(singletonScope));
		assertTrue(protoScope.accept(protoScope));
		assertTrue(protoScope.accept(sessionScope));
		assertTrue(protoScope.accept(requestScope));

		assertTrue(sessionScope.accept(singletonScope));
		assertFalse(sessionScope.accept(protoScope));
		assertTrue(sessionScope.accept(sessionScope));
		assertFalse(sessionScope.accept(requestScope));

		assertTrue(requestScope.accept(singletonScope));
		assertFalse(requestScope.accept(protoScope));
		assertTrue(requestScope.accept(sessionScope));
		assertTrue(requestScope.accept(requestScope));
	}

}