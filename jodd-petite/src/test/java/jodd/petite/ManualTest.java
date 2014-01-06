// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.petite.data.PojoAnnBean;
import jodd.petite.data.PojoBean;
import jodd.petite.data.SomeService;
import org.junit.Test;

import java.util.Set;

import static jodd.petite.PetiteRegistry.petite;
import static jodd.petite.meta.InitMethodInvocationStrategy.POST_INITIALIZE;
import static org.junit.Assert.*;

public class ManualTest {

	@Test
	public void testManualRegistration() {
		PetiteContainer pc = new PetiteContainer();
		pc.registerPetiteBean(SomeService.class, null, null, null, false);
		pc.registerPetiteBean(PojoBean.class, "pojo", null, null, false);
		assertEquals(2, pc.getTotalBeans());

		Set<String> names = pc.getBeanNames();
		assertEquals(2, names.size());

		assertTrue(names.contains("pojo"));
		assertTrue(names.contains("someService"));

		pc.registerPetiteCtorInjectionPoint("pojo", null, null);
		pc.registerPetitePropertyInjectionPoint("pojo", "service", "someService");
		pc.registerPetiteMethodInjectionPoint("pojo", "injectService", null, new String[]{"someService"});
		pc.registerPetiteInitMethods("pojo", POST_INITIALIZE, "init");

		PojoBean pojoBean = (PojoBean) pc.getBean("pojo");
		SomeService ss = (SomeService) pc.getBean("someService");

		assertNotNull(pojoBean);
		assertNotNull(ss);
		assertSame(ss, pojoBean.fservice);
		assertSame(ss, pojoBean.service);
		assertSame(ss, pojoBean.service2);
		assertEquals(1, pojoBean.count);
	}

	@Test
	public void testManualRegistration2() {
		PetiteContainer pc = new PetiteContainer();

		petite(pc).bean(SomeService.class).register();
		petite(pc).bean(PojoBean.class).name("pojo").register();

		assertEquals(2, pc.getTotalBeans());

		petite(pc).wire("pojo").ctor().bind();
		petite(pc).wire("pojo").property("service").ref("someService").bind();
		petite(pc).wire("pojo").method("injectService").ref("someService").bind();
		petite(pc).init("pojo").invoke(POST_INITIALIZE).methods("init").register();

		PojoBean pojoBean = (PojoBean) pc.getBean("pojo");
		SomeService ss = (SomeService) pc.getBean("someService");

		assertNotNull(pojoBean);
		assertNotNull(ss);
		assertSame(ss, pojoBean.fservice);
		assertSame(ss, pojoBean.service);
		assertSame(ss, pojoBean.service2);
		assertEquals(1, pojoBean.count);
	}

	@Test
	public void testManualRegistrationUsingAnnotations() {
		PetiteContainer pc = new PetiteContainer();
		pc.registerPetiteBean(SomeService.class, null, null, null, false);
		pc.registerPetiteBean(PojoAnnBean.class, "pojo", null, null, false);
		assertEquals(2, pc.getTotalBeans());

		PojoAnnBean pojoBean = (PojoAnnBean) pc.getBean("pojo");
		SomeService ss = (SomeService) pc.getBean("someService");

		assertNotNull(pojoBean);
		assertNotNull(ss);
		assertSame(ss, pojoBean.fservice);
		assertSame(ss, pojoBean.service);
		assertSame(ss, pojoBean.service2);
		assertEquals(1, pojoBean.count);
	}

	@Test
	public void testManualRegistrationUsingAnnotations2() {
		PetiteContainer pc = new PetiteContainer();

		petite(pc).bean(SomeService.class).register();
		petite(pc).bean(PojoAnnBean.class).name("pojo").register();

		assertEquals(2, pc.getTotalBeans());

		PojoAnnBean pojoBean = (PojoAnnBean) pc.getBean("pojo");
		SomeService ss = (SomeService) pc.getBean("someService");

		assertNotNull(pojoBean);
		assertNotNull(ss);
		assertSame(ss, pojoBean.fservice);
		assertSame(ss, pojoBean.service);
		assertSame(ss, pojoBean.service2);
		assertEquals(1, pojoBean.count);
	}

	@Test
	public void testManualDefinitionUsingAnnotations() {

		PetiteContainer pc = new PetiteContainer();
		pc.registerPetiteBean(SomeService.class, null, null, null, false);
		pc.registerPetiteBean(PojoAnnBean.class, "pojo", null, null, true);
		assertEquals(2, pc.getTotalBeans());

		PojoAnnBean pojoBean = (PojoAnnBean) pc.getBean("pojo");
		SomeService ss = (SomeService) pc.getBean("someService");

		assertNotNull(pojoBean);
		assertNotNull(ss);
		assertSame(ss, pojoBean.fservice);
		assertNull(pojoBean.service);
		assertNull(pojoBean.service2);
		assertEquals(0, pojoBean.count);
	}

	@Test
	public void testManualDefinitionUsingAnnotations2() {

		PetiteContainer pc = new PetiteContainer();

		petite(pc).bean(SomeService.class).register();
		petite(pc).bean(PojoAnnBean.class).name("pojo").define().register();

		assertEquals(2, pc.getTotalBeans());

		PojoAnnBean pojoBean = (PojoAnnBean) pc.getBean("pojo");
		SomeService ss = (SomeService) pc.getBean("someService");

		assertNotNull(pojoBean);
		assertNotNull(ss);
		assertSame(ss, pojoBean.fservice);
		assertNull(pojoBean.service);
		assertNull(pojoBean.service2);
		assertEquals(0, pojoBean.count);
	}

	@Test
	public void testManualDefinition() {

		PetiteContainer pc = new PetiteContainer();
		pc.registerPetiteBean(SomeService.class, null, null, null, false);
		pc.registerPetiteBean(PojoBean.class, "pojo", null, null, true);
		assertEquals(2, pc.getTotalBeans());

		pc.registerPetiteCtorInjectionPoint("pojo", null, null);
		pc.registerPetitePropertyInjectionPoint("pojo", "service", "someService");
		pc.registerPetiteMethodInjectionPoint("pojo", "injectService", null, new String[] {"someService"});
		pc.registerPetiteInitMethods("pojo", POST_INITIALIZE, "init");

		PojoBean pojoBean = (PojoBean) pc.getBean("pojo");
		SomeService ss = (SomeService) pc.getBean("someService");

		assertNotNull(pojoBean);
		assertNotNull(ss);
		assertSame(ss, pojoBean.fservice);
		assertSame(ss, pojoBean.service);
		assertSame(ss, pojoBean.service2);
		assertEquals(1, pojoBean.count);
	}

	@Test
	public void testManualDefinition2() {

		PetiteContainer pc = new PetiteContainer();

		petite(pc).bean(SomeService.class).register();
		petite(pc).bean(PojoBean.class).name("pojo").define().register();

		assertEquals(2, pc.getTotalBeans());


		petite(pc).wire("pojo").ctor().bind();
		petite(pc).wire("pojo").property("service").ref("someService").bind();
		petite(pc).wire("pojo").method("injectService").ref("someService").bind();
		petite(pc).init("pojo").invoke(POST_INITIALIZE).methods("init").register();

		PojoBean pojoBean = (PojoBean) pc.getBean("pojo");
		SomeService ss = (SomeService) pc.getBean("someService");

		assertNotNull(pojoBean);
		assertNotNull(ss);
		assertSame(ss, pojoBean.fservice);
		assertSame(ss, pojoBean.service);
		assertSame(ss, pojoBean.service2);
		assertEquals(1, pojoBean.count);
	}

}