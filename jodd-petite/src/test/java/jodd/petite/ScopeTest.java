// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.bean.BeanUtil;
import jodd.petite.scope.ProtoScope;
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
	public void testSessionScopeAccepted() {
		PetiteContainer pc = new PetiteContainer();

		ThreadLocalScope threadLocalScope = pc.resolveScope(ThreadLocalScope.class);
		SingletonScope singletonScope = pc.resolveScope(SingletonScope.class);
		ProtoScope protoScope = pc.resolveScope(ProtoScope.class);

		assertTrue(threadLocalScope.accept(singletonScope));
		assertTrue(threadLocalScope.accept(threadLocalScope));
		assertFalse(threadLocalScope.accept(protoScope));

		Class[] acceptedClasses = (Class[]) BeanUtil.getDeclaredProperty(threadLocalScope, "acceptedScopes");
		assertEquals(2, acceptedClasses.length);

		SessionScope sessionScope = pc.resolveScope(SessionScope.class);

		acceptedClasses = (Class[]) BeanUtil.getDeclaredProperty(threadLocalScope, "acceptedScopes");
		assertEquals(3, acceptedClasses.length);
		assertTrue(threadLocalScope.accept(sessionScope));
	}

}