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

import jodd.petite.fixtures.data.Biz;
import jodd.petite.fixtures.data.MyBiz;
import jodd.petite.fixtures.data.MyBiz2;
import jodd.petite.fixtures.data.MyBiz3;
import jodd.petite.fixtures.data.WeBiz;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

public class TestOneInterfaceLookup {

	@Test
	public void testLookupBiz() {
		PetiteContainer pc = new PetiteContainer();

		pc.registerPetiteBean(MyBiz.class);

		BeanDefinition beanDefinition = pc.lookupBeanDefinition("myBiz");
		assertNotNull(beanDefinition);
		assertEquals("myBiz", beanDefinition.getName());

		Biz myBiz = pc.getBean("myBiz");
		assertNotNull(myBiz);

		BeanDefinition altBeanDefinition = pc.lookupBeanDefinition("biz");
		assertNotNull(altBeanDefinition);
		assertEquals("myBiz", altBeanDefinition.getName());
		assertSame(beanDefinition, altBeanDefinition);

		Biz myBiz2 = pc.getBean("biz");
		assertNotNull(myBiz2);
		assertSame(myBiz, myBiz2);

		WeBiz weBiz = new WeBiz();
		pc.wire(weBiz);
		assertNotNull(weBiz.biz);
		assertSame(myBiz, weBiz.biz);


		// register second one

		pc.registerPetiteBean(MyBiz2.class);

		beanDefinition = pc.lookupBeanDefinition("myBiz");
		assertNotNull(beanDefinition);
		assertEquals("myBiz", beanDefinition.getName());

		altBeanDefinition = pc.lookupBeanDefinition("biz");
		assertNull(altBeanDefinition);

		myBiz2 = pc.getBean("biz");
		assertNull(myBiz2);

		weBiz = new WeBiz();
		try {
			pc.wire(weBiz);
			fail("error");
		}
		catch (PetiteException ignore) {}

		// register third one

		pc.registerPetiteBean(MyBiz3.class);

		beanDefinition = pc.lookupBeanDefinition("myBiz");
		assertNotNull(beanDefinition);
		assertEquals("myBiz", beanDefinition.getName());

		altBeanDefinition = pc.lookupBeanDefinition("biz");
		assertNull(altBeanDefinition);

		myBiz2 = pc.getBean("biz");
		assertNull(myBiz2);

		weBiz = new WeBiz();
		try {
			pc.wire(weBiz);
			fail("error");
		}
		catch (PetiteException ignore) {}
		assertNull(weBiz.biz);

	}
}
