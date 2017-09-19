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

import jodd.petite.scope.ProtoScope;
import jodd.petite.scope.RequestScope;
import jodd.petite.scope.SessionScope;
import jodd.petite.scope.SingletonScope;
import jodd.petite.scope.ThreadLocalScope;
import jodd.petite.fixtures.tst.Boo;
import jodd.petite.fixtures.tst.Foo;
import jodd.petite.fixtures.tst.Zoo;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Semaphore;

import static org.junit.jupiter.api.Assertions.*;

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
		assertTrue(singletonScope.accept(protoScope));
		assertFalse(singletonScope.accept(sessionScope));
		assertFalse(singletonScope.accept(requestScope));

		assertTrue(protoScope.accept(singletonScope));
		assertTrue(protoScope.accept(protoScope));
		assertTrue(protoScope.accept(sessionScope));
		assertTrue(protoScope.accept(requestScope));

		assertTrue(sessionScope.accept(singletonScope));
		assertTrue(sessionScope.accept(protoScope));
		assertTrue(sessionScope.accept(sessionScope));
		assertFalse(sessionScope.accept(requestScope));

		assertTrue(requestScope.accept(singletonScope));
		assertTrue(requestScope.accept(protoScope));
		assertTrue(requestScope.accept(sessionScope));
		assertTrue(requestScope.accept(requestScope));
	}

}
