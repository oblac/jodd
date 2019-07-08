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

package jodd.bean;

import jodd.bean.fixtures.LifeBean;
import jodd.introspector.CachingIntrospector;
import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.introspector.PropertyDescriptor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BeanPrefixTest {

	@Test
	void testFieldPrefix1() {
		LifeBean lifeBean = new LifeBean();

		String foo = BeanUtil.pojo.getProperty(lifeBean, "foo").toString();

		assertEquals("foo", foo);

		ClassIntrospector.Implementation.set(new CachingIntrospector(true, true, true, new String[] {"_"}));

		foo = BeanUtil.pojo.getProperty(lifeBean, "foo").toString();

		assertEquals("foo", foo);

		ClassDescriptor cd = ClassIntrospector.get().lookup(LifeBean.class);

		PropertyDescriptor[] pds = cd.getAllPropertyDescriptors();
		assertEquals(3, pds.length);

		assertEquals("bar", pds[0].getName());
		assertEquals("_bar", pds[0].getFieldDescriptor().getName());

		assertEquals("www", pds[2].getName());
		assertEquals(null, pds[2].getFieldDescriptor());

		ClassIntrospector.Implementation.set(new CachingIntrospector());
	}

	@Test
	void testFieldPrefix1withEmpty() {
		LifeBean lifeBean = new LifeBean();

		String foo = BeanUtil.pojo.getProperty(lifeBean, "foo").toString();

		assertEquals("foo", foo);

		ClassIntrospector.Implementation.set(new CachingIntrospector(true, true, true, new String[] {"_", ""}));

		foo = BeanUtil.pojo.getProperty(lifeBean, "foo").toString();

		assertEquals("foo", foo);


		ClassDescriptor cd = ClassIntrospector.get().lookup(LifeBean.class);

		PropertyDescriptor[] pds = cd.getAllPropertyDescriptors();
		assertEquals(3, pds.length);

		assertEquals("bar", pds[0].getName());
		assertEquals("_bar", pds[0].getFieldDescriptor().getName());

		assertEquals("www", pds[2].getName());
		assertEquals("www", pds[2].getFieldDescriptor().getName());

		ClassIntrospector.Implementation.set(new CachingIntrospector());
	}

	@Test
	void testFieldPrefix2() {
		BeanUtilBean beanUtilBean = new BeanUtilBean();

		LifeBean lifeBean = new LifeBean();

		String bar = beanUtilBean.getProperty(lifeBean, "bar").toString();

		assertEquals("bar", bar);

		beanUtilBean.setIntrospector(new CachingIntrospector(true, true, true, new String[] {"_"}));

		bar = beanUtilBean.getProperty(lifeBean, "bar").toString();

		assertEquals("_bar", bar);
	}
}
