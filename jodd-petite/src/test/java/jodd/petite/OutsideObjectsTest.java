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

import jodd.cache.TypeCache;
import jodd.petite.meta.PetiteInject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class OutsideObjectsTest {

	public static class InBean {
	}

	public static class BeBean {
		@PetiteInject public InBean inBean;
	}

	@Test
	void testWire() {
		PetiteContainer pc = new PetiteContainer();
		pc.registerPetiteBean(InBean.class);

		BeBean beBean = new BeBean();

		assertNull(beBean.inBean);

		pc.wire(beBean);

		assertNotNull(beBean.inBean);
		assertNotNull(pc.lookupBeanDefinition("inBean"));
		assertNull(pc.lookupBeanDefinition("beBean"));
		assertEquals(1, pc.beansCount());
		assertEquals(0, pc.externalsCache.size());
	}

	@Test
	void testWire_withCache() {
		PetiteContainer pc = new PetiteContainer();
		pc.setExternalsCache(TypeCache.createDefault());
		pc.registerPetiteBean(InBean.class);

		BeBean beBean = new BeBean();

		assertNull(beBean.inBean);

		pc.wire(beBean);

		assertNotNull(beBean.inBean);
		assertNotNull(pc.lookupBeanDefinition("inBean"));
		assertNull(pc.lookupBeanDefinition("beBean"));
		assertEquals(1, pc.beansCount());

		// repeating
		beBean = new BeBean();
		pc.wire(beBean);
		assertNotNull(beBean.inBean);
		assertEquals(1, pc.beansCount());
		assertEquals(1, pc.externalsCache.size());
	}
}
