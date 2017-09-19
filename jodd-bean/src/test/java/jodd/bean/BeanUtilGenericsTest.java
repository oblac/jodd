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

import jodd.bean.fixtures.Woof;
import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.introspector.PropertyDescriptor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BeanUtilGenericsTest {

	@Test
	public void testAllBeanSetters() {
		Woof woof = new Woof();
		Class type = woof.getClass();
		ClassDescriptor cd = ClassIntrospector.lookup(type);
		PropertyDescriptor[] properties = cd.getAllPropertyDescriptors();
		assertNotNull(properties);
		assertEquals(7, properties.length);
	}

	// ---------------------------------------------------------------- fields

	public static class BaseClass<A, B> {
		public A f1;		// String
		public B f2;		// Integer


		public void setFoo1(A a) {
			f1 = a;
		}
		public A getFoo1() {return f1;}
		public void setFoo2(B b) {
			f2 = b;
		}
		public B getFoo2() {return f2;}
	}

	public static class ConcreteClass extends BaseClass<String, Integer> {
	}

	public static class BaseClass2<X> extends BaseClass<X, Integer> {
	}

	public static class ConcreteClass2 extends BaseClass2<String> {
	}

	@Test
	public void testSetField() {
		BaseClass base = new BaseClass();

		BeanUtil.pojo.setProperty(base, "f1", Integer.valueOf(173));
		assertEquals(Integer.valueOf(173), base.f1);
		assertEquals(Integer.valueOf(173), BeanUtil.pojo.getProperty(base, "f1"));
		assertEquals(Integer.class, base.f1.getClass());

		BeanUtil.pojo.setProperty(base, "f2", "123");
		assertEquals("123", base.f2);
		assertEquals("123", BeanUtil.pojo.getProperty(base, "f2"));
		assertEquals(String.class, base.f2.getClass());

		// concrete implementation #1

		ConcreteClass impl1 = new ConcreteClass();

		BeanUtil.pojo.setProperty(impl1, "f1", Integer.valueOf(173));
		assertEquals("173", impl1.f1);
		assertEquals("173", BeanUtil.pojo.getProperty(impl1, "f1"));
		assertEquals(String.class, impl1.f1.getClass());

		BeanUtil.pojo.setProperty(impl1, "f2", "123");
		assertEquals(Integer.valueOf(123), impl1.f2);
		assertEquals(Integer.valueOf(123), BeanUtil.pojo.getProperty(impl1, "f2"));
		assertEquals(Integer.class, impl1.f2.getClass());

		// concrete implementation #2

		ConcreteClass2 impl2 = new ConcreteClass2();

		BeanUtil.pojo.setProperty(impl2, "f1", Integer.valueOf(173));
		assertEquals("173", impl2.f1);
		assertEquals("173", BeanUtil.pojo.getProperty(impl2, "f1"));
		assertEquals(String.class, impl2.f1.getClass());

		BeanUtil.pojo.setProperty(impl2, "f2", "123");
		assertEquals(Integer.valueOf(123), impl2.f2);
		assertEquals(Integer.valueOf(123), BeanUtil.pojo.getProperty(impl2, "f2"));
		assertEquals(Integer.class, impl2.f2.getClass());
	}

	@Test
	public void testSetProperty() {
		BaseClass base = new BaseClass();

		BeanUtil.pojo.setProperty(base, "foo1", Integer.valueOf(173));
		assertEquals(Integer.valueOf(173), base.getFoo1());
		assertEquals(Integer.valueOf(173), BeanUtil.pojo.getProperty(base, "foo1"));
		assertEquals(Integer.class, base.getFoo1().getClass());

		BeanUtil.pojo.setProperty(base, "foo2", "123");
		assertEquals("123", base.getFoo2());
		assertEquals("123", BeanUtil.pojo.getProperty(base, "foo2"));
		assertEquals(String.class, base.getFoo2().getClass());

		// concrete implementation #1

		ConcreteClass impl1 = new ConcreteClass();

		BeanUtil.pojo.setProperty(impl1, "foo1", Integer.valueOf(173));
		assertEquals("173", impl1.getFoo1());
		assertEquals("173", BeanUtil.pojo.getProperty(impl1, "foo1"));
		assertEquals(String.class, impl1.getFoo1().getClass());

		BeanUtil.pojo.setProperty(impl1, "foo2", "123");
		assertEquals(Integer.valueOf(123), impl1.getFoo2());
		assertEquals(Integer.valueOf(123), BeanUtil.pojo.getProperty(impl1, "foo2"));
		assertEquals(Integer.class, impl1.getFoo2().getClass());

		// concrete implementation #2

		ConcreteClass2 impl2 = new ConcreteClass2();

		BeanUtil.pojo.setProperty(impl2, "foo1", Integer.valueOf(173));
		assertEquals("173", impl2.getFoo1());
		assertEquals("173", BeanUtil.pojo.getProperty(impl2, "foo1"));
		assertEquals(String.class, impl2.getFoo1().getClass());

		BeanUtil.pojo.setProperty(impl2, "f2", "123");
		assertEquals(Integer.valueOf(123), impl2.getFoo2());
		assertEquals(Integer.valueOf(123), BeanUtil.pojo.getProperty(impl2, "foo2"));
		assertEquals(Integer.class, impl2.getFoo2().getClass());
	}

}
