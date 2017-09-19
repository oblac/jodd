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

import jodd.petite.fixtures.mix.Big;
import jodd.petite.fixtures.mix.Big2;
import jodd.petite.fixtures.mix.Small;
import jodd.petite.scope.ProtoScope;
import jodd.petite.scope.SingletonScope;
import jodd.petite.scope.ThreadLocalScope;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MixScopeTest {

	@Test
	public void testPrototypeInSingleton() {
		Small.instanceCounter = 0;

		PetiteContainer pc = new PetiteContainer();
		pc.getConfig().setWireScopedProxy(true);
		pc.getConfig().setDetectMixedScopes(true);

		pc.registerPetiteBean(Big.class, "big", SingletonScope.class, null, false);
		pc.registerPetiteBean(Big.class, "big2", SingletonScope.class, null, false);
		pc.registerPetiteBean(Small.class, "small", ProtoScope.class, null, false);

		Big big = pc.getBean("big");

		Small small1 = big.getSmall();
		Small small2 = big.getSmall();

		assertSame(small1, small2);				// factory !!!

		assertEquals(1, Small.instanceCounter);

		assertTrue(small1.toString().equals(small2.toString()));

		assertEquals("small 1", small1.name());
		assertEquals("small 1", small2.name());

		assertEquals(1, Small.instanceCounter);

		Big big2 = pc.getBean("big2");
		Small small3 = big2.getSmall();

		assertEquals("small 2", small3.name());

		assertEquals(2, Small.instanceCounter);
	}

	@Test
	public void testPrototypeInSingleton2() {
		Small.instanceCounter = 0;

		PetiteContainer pc = new PetiteContainer();
		pc.getConfig().setWireScopedProxy(true);
		pc.getConfig().setDetectMixedScopes(true);

		pc.registerPetiteBean(Big2.class, "big", SingletonScope.class, null, false);
		pc.registerPetiteBean(Small.class, "small", ProtoScope.class, null, false);

		Big2 big = pc.getBean("big");

		Small small1 = big.getSmall();
		Small small2 = big.getSmall();

		assertSame(small1, small2);				// factory !!!

		assertEquals(1, Small.instanceCounter);

		assertEquals("small 1", small1.name());
		assertEquals("small 1", small2.name());

		assertEquals(1, Small.instanceCounter);

		assertTrue(small1.toString().equals(small2.toString()));

		assertEquals(1, Small.instanceCounter);
	}

	@Test
	public void testSingleFactoryInstance() {

		Small.instanceCounter = 0;

		PetiteContainer pc = new PetiteContainer();
		pc.getConfig().setWireScopedProxy(true);
		pc.getConfig().setDetectMixedScopes(true);

		pc.registerPetiteBean(Big.class, "big", SingletonScope.class, null, false);
		pc.registerPetiteBean(Big.class, "big2", SingletonScope.class, null, false);
		pc.registerPetiteBean(Small.class, "small", ProtoScope.class, null, false);

		Big big = pc.getBean("big");

		Small small1 = big.getSmall();
		Small small2 = big.getSmall();

		assertSame(small1, small2);				// factory !!!

		assertEquals(1, Small.instanceCounter);

		Big big2 = pc.getBean("big2");

		Small small3 = big2.getSmall();
		Small small4 = big2.getSmall();

		assertSame(small3, small4);				// factory !!!

		assertNotSame(small1, small4);
	}

	@Test
	public void testThreadLocalScopeInSingleton() {

		Small.instanceCounter = 0;

		PetiteContainer pc = new PetiteContainer();
		pc.getConfig().setWireScopedProxy(true);
		pc.getConfig().setDetectMixedScopes(true);

		pc.registerPetiteBean(Big.class, "big", SingletonScope.class, null, false);
		pc.registerPetiteBean(Small.class, "small", ThreadLocalScope.class, null, false);

		final Big big = pc.getBean("big");

		Small small1 = big.getSmall();
		Small small2 = big.getSmall();

		assertSame(small1, small2);

		// one 'small' instance is created for the factory wrapper.
		assertEquals(1, Small.instanceCounter);

		// on next small method call, new 'small' instance will be created,
		// from the	ThreadLocal scope factory
		assertEquals("small 2", small1.name());

		// no 'small' instance will be created, the same instance from above will be used
		assertEquals("small 2", small2.name());

		// create new thread
		Thread thread = new Thread() {
			@Override
			public void run() {
				Small small3 = big.getSmall();

				assertEquals(2, Small.instanceCounter);

				assertEquals("small 3", small3.name());
				assertEquals("small 3", small3.name());

				assertEquals(3, Small.instanceCounter);

			}
		};

		thread.start();

		try {
			thread.join();
		} catch (InterruptedException ignore) {
		}

		assertEquals(3, Small.instanceCounter);

	}
}
