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

import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;
import jodd.petite.proxetta.ProxettaAwarePetiteContainer;
import jodd.proxetta.ProxyAspect;
import jodd.proxetta.impl.ProxyProxetta;
import jodd.proxetta.pointcuts.AllRealMethodsPointcut;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
