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

import jodd.petite.fixtures.data.PojoAnnBean;
import jodd.petite.fixtures.data.PojoBean;
import jodd.petite.fixtures.data.SomeService;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static jodd.petite.meta.InitMethodInvocationStrategy.POST_INITIALIZE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ManualTest {

	@Test
	void testManualRegistration() {
		PetiteContainer pc = new PetiteContainer();
		pc.registerPetiteBean(SomeService.class, null, null, null, false, null);
		pc.registerPetiteBean(PojoBean.class, "pojo", null, null, false, null);
		assertEquals(2, pc.beansCount());

		Set<String> names = pc.beanNames();
		assertEquals(2, names.size());

		assertTrue(names.contains("pojo"));
		assertTrue(names.contains("someService"));

		pc.registerPetiteCtorInjectionPoint("pojo", null, null);
		pc.registerPetitePropertyInjectionPoint("pojo", "service", "someService");
		pc.registerPetiteMethodInjectionPoint("pojo", "injectService", null, new String[]{"someService"});
		pc.registerPetiteInitMethods("pojo", POST_INITIALIZE, "init");

		PojoBean pojoBean = pc.getBean("pojo");
		SomeService ss = pc.getBean("someService");

		assertNotNull(pojoBean);
		assertNotNull(ss);
		assertSame(ss, pojoBean.fservice);
		assertSame(ss, pojoBean.service);
		assertSame(ss, pojoBean.service2);
		assertEquals(1, pojoBean.count);
	}

	@Test
	void testManualRegistration2() {
		PetiteContainer pc = new PetiteContainer();

		PetiteRegistry.of(pc).bean(SomeService.class).register();
		PetiteRegistry.of(pc).bean(PojoBean.class).name("pojo").register();

		assertEquals(2, pc.beansCount());

		PetiteRegistry.of(pc).wire("pojo").ctor().bind();
		PetiteRegistry.of(pc).wire("pojo").property("service").ref("someService").bind();
		PetiteRegistry.of(pc).wire("pojo").method("injectService").ref("someService").bind();
		PetiteRegistry.of(pc).init("pojo").invoke(POST_INITIALIZE).methods("init").register();

		PojoBean pojoBean = pc.getBean("pojo");
		SomeService ss = pc.getBean("someService");

		assertNotNull(pojoBean);
		assertNotNull(ss);
		assertSame(ss, pojoBean.fservice);
		assertSame(ss, pojoBean.service);
		assertSame(ss, pojoBean.service2);
		assertEquals(1, pojoBean.count);
	}

	@Test
	void testManualRegistrationUsingAnnotations() {
		PetiteContainer pc = new PetiteContainer();
		pc.registerPetiteBean(SomeService.class, null, null, null, false, null);
		pc.registerPetiteBean(PojoAnnBean.class, "pojo", null, null, false, null);
		assertEquals(2, pc.beansCount());

		PojoAnnBean pojoBean = pc.getBean("pojo");
		SomeService ss = pc.getBean("someService");

		assertNotNull(pojoBean);
		assertNotNull(ss);
		assertSame(ss, pojoBean.fservice);
		assertSame(ss, pojoBean.service);
		assertSame(ss, pojoBean.service2);
		assertEquals(1, pojoBean.count);
	}

	@Test
	void testManualRegistrationUsingAnnotations2() {
		PetiteContainer pc = new PetiteContainer();

		PetiteRegistry.of(pc).bean(SomeService.class).register();
		PetiteRegistry.of(pc).bean(PojoAnnBean.class).name("pojo").register();

		assertEquals(2, pc.beansCount());

		PojoAnnBean pojoBean = pc.getBean("pojo");
		SomeService ss = pc.getBean("someService");

		assertNotNull(pojoBean);
		assertNotNull(ss);
		assertSame(ss, pojoBean.fservice);
		assertSame(ss, pojoBean.service);
		assertSame(ss, pojoBean.service2);
		assertEquals(1, pojoBean.count);
	}

	@Test
	void testManualDefinitionUsingAnnotations() {

		PetiteContainer pc = new PetiteContainer();
		pc.registerPetiteBean(SomeService.class, null, null, null, false, null);
		pc.registerPetiteBean(PojoAnnBean.class, "pojo", null, null, true, null);
		assertEquals(2, pc.beansCount());

		PojoAnnBean pojoBean = pc.getBean("pojo");
		SomeService ss = pc.getBean("someService");

		assertNotNull(pojoBean);
		assertNotNull(ss);
		assertSame(ss, pojoBean.fservice);
		assertNull(pojoBean.service);
		assertNull(pojoBean.service2);
		assertEquals(0, pojoBean.count);
	}

	@Test
	void testManualDefinitionUsingAnnotations2() {

		PetiteContainer pc = new PetiteContainer();

		PetiteRegistry.of(pc).bean(SomeService.class).register();
		PetiteRegistry.of(pc).bean(PojoAnnBean.class).name("pojo").define().register();

		assertEquals(2, pc.beansCount());

		PojoAnnBean pojoBean = pc.getBean("pojo");
		SomeService ss = pc.getBean("someService");

		assertNotNull(pojoBean);
		assertNotNull(ss);
		assertSame(ss, pojoBean.fservice);
		assertNull(pojoBean.service);
		assertNull(pojoBean.service2);
		assertEquals(0, pojoBean.count);
	}

	@Test
	void testManualDefinition() {

		PetiteContainer pc = new PetiteContainer();
		pc.registerPetiteBean(SomeService.class, null, null, null, false, null);
		pc.registerPetiteBean(PojoBean.class, "pojo", null, null, true, null);
		assertEquals(2, pc.beansCount());

		pc.registerPetiteCtorInjectionPoint("pojo", null, null);
		pc.registerPetitePropertyInjectionPoint("pojo", "service", "someService");
		pc.registerPetiteMethodInjectionPoint("pojo", "injectService", null, new String[] {"someService"});
		pc.registerPetiteInitMethods("pojo", POST_INITIALIZE, "init");

		PojoBean pojoBean = pc.getBean("pojo");
		SomeService ss = pc.getBean("someService");

		assertNotNull(pojoBean);
		assertNotNull(ss);
		assertSame(ss, pojoBean.fservice);
		assertSame(ss, pojoBean.service);
		assertSame(ss, pojoBean.service2);
		assertEquals(1, pojoBean.count);
	}

	@Test
	void testManualDefinition2() {
		PetiteRegistry petiteRegistry = PetiteRegistry.of(new PetiteContainer());

		petiteRegistry.bean(SomeService.class).register();
		petiteRegistry.bean(PojoBean.class).name("pojo").define().register();

		assertEquals(2, petiteRegistry.petiteContainer().beansCount());


		petiteRegistry.wire("pojo").ctor().bind();
		petiteRegistry.wire("pojo").property("service").ref("someService").bind();
		petiteRegistry.wire("pojo").method("injectService").ref("someService").bind();
		petiteRegistry.init("pojo").invoke(POST_INITIALIZE).methods("init").register();

		PojoBean pojoBean = petiteRegistry.petiteContainer().getBean("pojo");
		SomeService ss = petiteRegistry.petiteContainer().getBean("someService");

		assertNotNull(pojoBean);
		assertNotNull(ss);
		assertSame(ss, pojoBean.fservice);
		assertSame(ss, pojoBean.service);
		assertSame(ss, pojoBean.service2);
		assertEquals(1, pojoBean.count);
	}

}
