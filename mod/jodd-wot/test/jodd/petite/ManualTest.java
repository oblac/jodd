// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.petite.data.SomeService;
import jodd.petite.data.PojoBean;
import jodd.petite.data.PojoAnnBean;
import junit.framework.TestCase;

public class ManualTest extends TestCase {

	public void testManualRegistration() {
		PetiteContainer pc = new PetiteContainer();
		pc.registerBean(SomeService.class);
		pc.registerBean("pojo", PojoBean.class);
		assertEquals(2, pc.getTotalBeans());

		pc.registerCtorInjectionPoint("pojo");
		pc.registerPropertyInjectionPoint("pojo", "service", "someService");
		pc.registerMethodInjectionPoint("pojo", "injectService", "someService");
		pc.registerInitMethods("pojo", "init");

		PojoBean pojoBean = (PojoBean) pc.getBean("pojo");
		SomeService ss = (SomeService) pc.getBean("someService");

		assertNotNull(pojoBean);
		assertNotNull(ss);
		assertSame(ss, pojoBean.fservice);
		assertSame(ss, pojoBean.service);
		assertSame(ss, pojoBean.service2);
		assertEquals(1, pojoBean.count);
	}

	public void testManualRegistrationUsingAnnotations() {
		PetiteContainer pc = new PetiteContainer();
		pc.registerBean(SomeService.class);
		pc.registerBean("pojo", PojoAnnBean.class);
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

	public void testManualDefinitionUsingAnnotations() {

		PetiteContainer pc = new PetiteContainer();
		pc.registerBean(SomeService.class);
		pc.defineBean("pojo", PojoAnnBean.class);
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

	public void testManualDefinition() {

		PetiteContainer pc = new PetiteContainer();
		pc.registerBean(SomeService.class);
		pc.defineBean("pojo", PojoBean.class);
		assertEquals(2, pc.getTotalBeans());

		pc.registerCtorInjectionPoint("pojo");
		pc.registerPropertyInjectionPoint("pojo", "service", "someService");
		pc.registerMethodInjectionPoint("pojo", "injectService", "someService");
		pc.registerInitMethods("pojo", "init");

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
