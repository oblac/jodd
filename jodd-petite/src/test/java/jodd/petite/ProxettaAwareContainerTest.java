// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;
import jodd.petite.proxetta.ProxettaAwarePetiteContainer;
import jodd.proxetta.ProxyAspect;
import jodd.proxetta.impl.ProxyProxetta;
import jodd.proxetta.pointcuts.AllRealMethodsPointcut;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class ProxettaAwareContainerTest {

	@PetiteBean
	public static class SecretService {
	}

	// non-private field
	@PetiteBean
	public static class PublicService {

		@PetiteInject
		SecretService secretService;

		public String hello() {
			return "Hello World!";
		}
	}

	// private field with setter
	@PetiteBean
	public static class PublicService2 {

		@PetiteInject
		private SecretService secretService;

		public void setSecretService(SecretService secretService) {
			this.secretService = secretService;
		}

		public String hello() {
			return "Hello World!";
		}
	}

	// private field with annotated setter
	@PetiteBean
	public static class PublicService3 {

		private SecretService secretService;

		@PetiteInject
		public void setSecretService(SecretService secretService) {
			this.secretService = secretService;
		}

		public String hello() {
			return "Hello World!";
		}
	}

	@Test
	public void testProxyProxetta() {
		ProxyProxetta proxetta = ProxyProxetta.withAspects(
			new ProxyAspect(AddStringAdvice.class, new AllRealMethodsPointcut()));

		PetiteContainer papc = new ProxettaAwarePetiteContainer(proxetta);

		papc.registerPetiteBean(SecretService.class, null, null, null, false);
		BeanDefinition beanDefinition = papc.registerPetiteBean(PublicService.class, null, null, null, false);
		papc.registerPetiteBean(PublicService2.class, null, null, null, false);
		papc.registerPetiteBean(PublicService3.class, null, null, null, false);

		assertNotEquals(PublicService.class, beanDefinition.getType());

		PublicService publicService = (PublicService) papc.getBean(beanDefinition.getName());
		assertNotNull(publicService.secretService);
		assertEquals("Hello World! And Universe, too!", publicService.hello());

		PublicService2 publicService2 = papc.getBean(PublicService2.class);
		assertNotNull(publicService2.secretService);
		assertEquals("Hello World! And Universe, too!", publicService2.hello());

		PublicService3 publicService3 = papc.getBean(PublicService3.class);
		assertNotNull(publicService3.secretService);
		assertEquals("Hello World! And Universe, too!", publicService3.hello());
	}
}