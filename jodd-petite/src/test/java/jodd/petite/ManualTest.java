// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.petite.data.PojoAnnBean;
import jodd.petite.data.PojoBean;
import jodd.petite.data.SomeService;
import org.junit.Test;

import static jodd.petite.meta.InitMethodInvocationStrategy.POST_INITIALIZE;
import static org.junit.Assert.*;

public class ManualTest {

	@Test
	public void testManualRegistration() {
		PetiteContainer pc = new PetiteContainer();
		pc.registerPetiteBean(null, SomeService.class, null, null, false);
		pc.registerPetiteBean("pojo", PojoBean.class, null, null, false);
		assertEquals(2, pc.getTotalBeans());

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
	public void testManualRegistrationUsingAnnotations() {
		PetiteContainer pc = new PetiteContainer();
		pc.registerPetiteBean(null, SomeService.class, null, null, false);
		pc.registerPetiteBean("pojo", PojoAnnBean.class, null, null, false);
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
		pc.registerPetiteBean(null, SomeService.class, null, null, false);
		pc.registerPetiteBean("pojo", PojoAnnBean.class, null, null, true);
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
		pc.registerPetiteBean(null, SomeService.class, null, null, false);
		pc.registerPetiteBean("pojo", PojoBean.class, null, null, true);
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

}