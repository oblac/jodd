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

import jodd.petite.fixtures.tst4.Bar;
import jodd.petite.fixtures.tst4.Foo;
import jodd.petite.fixtures.tst4.Foo2;
import org.junit.jupiter.api.Test;

import static jodd.petite.meta.InitMethodInvocationStrategy.POST_CONSTRUCT;
import static jodd.petite.meta.InitMethodInvocationStrategy.POST_DEFINE;
import static jodd.petite.meta.InitMethodInvocationStrategy.POST_INITIALIZE;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InitMethodTest {

	private void defineBean(PetiteContainer petiteContainer, String beanName, Class type) {
		petiteContainer.registerPetiteBean(type, beanName, null, null, true);
	}

	@Test
	public void testPostConstructor() {
		PetiteContainer petiteContainer = new PetiteContainer();

		// define two beans
		defineBean(petiteContainer, "bar", Bar.class);
		defineBean(petiteContainer, "foo", Foo.class);

		// wiring
		petiteContainer.registerPetitePropertyInjectionPoint("foo", "bar", "bar");

		// init method
		petiteContainer.registerPetiteInitMethods("foo", POST_CONSTRUCT, "init");

		// param
		petiteContainer.defineParameter("foo.data", "data");

		// get bean
		Foo foo = (Foo) petiteContainer.getBean("foo");

		assertEquals("ctor null null", foo.result);
		assertEquals("bar", foo.bar.toString());
		assertEquals("data", foo.data);
	}

	@Test
	public void testPostDefine() {
		PetiteContainer petiteContainer = new PetiteContainer();

		// define two beans
		defineBean(petiteContainer, "bar", Bar.class);
		defineBean(petiteContainer, "foo", Foo.class);

		// wiring
		petiteContainer.registerPetitePropertyInjectionPoint("foo", "bar", "bar");

		// init method
		petiteContainer.registerPetiteInitMethods("foo", POST_DEFINE, "init");

		// param
		petiteContainer.defineParameter("foo.data", "data");

		// get bean
		Foo foo = (Foo) petiteContainer.getBean("foo");

		assertEquals("ctor bar null", foo.result);
		assertEquals("bar", foo.bar.toString());
		assertEquals("data", foo.data);
	}

	@Test
	public void testPostInitialize() {
		PetiteContainer petiteContainer = new PetiteContainer();

		// define two beans
		defineBean(petiteContainer, "bar", Bar.class);
		defineBean(petiteContainer, "foo", Foo.class);

		// wiring
		petiteContainer.registerPetitePropertyInjectionPoint("foo", "bar", "bar");

		// init method
		petiteContainer.registerPetiteInitMethods("foo", POST_INITIALIZE, "init");

		// param
		petiteContainer.defineParameter("foo.data", "data");

		// get bean
		Foo foo = (Foo) petiteContainer.getBean("foo");

		assertEquals("ctor bar data", foo.result);
		assertEquals("bar", foo.bar.toString());
		assertEquals("data", foo.data);
	}

	@Test
	public void testPostAll() {
		PetiteContainer petiteContainer = new PetiteContainer();

		// define two beans
		defineBean(petiteContainer, "bar", Bar.class);
		defineBean(petiteContainer, "foo", Foo2.class);

		// wiring
		petiteContainer.registerPetitePropertyInjectionPoint("foo", "bar", "bar");

		// init method
		petiteContainer.registerPetiteInitMethods("foo", POST_CONSTRUCT, "init1");
		petiteContainer.registerPetiteInitMethods("foo", POST_DEFINE, "init2");
		petiteContainer.registerPetiteInitMethods("foo", POST_INITIALIZE, "init3");

		// param
		petiteContainer.defineParameter("foo.data", "data");

		// get bean
		Foo2 foo = (Foo2) petiteContainer.getBean("foo");

		assertEquals("1 null null 2 bar null 3 bar data", foo.result);
		assertEquals("bar", foo.bar.toString());
		assertEquals("data", foo.data);
	}

}
