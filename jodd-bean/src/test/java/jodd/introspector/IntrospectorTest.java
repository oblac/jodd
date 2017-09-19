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

package jodd.introspector;

import jodd.bean.BeanUtil;
import jodd.introspector.fixtures.Abean;
import jodd.introspector.fixtures.Ac;
import jodd.introspector.fixtures.Bbean;
import jodd.introspector.fixtures.Bc;
import jodd.introspector.fixtures.Cbean;
import jodd.introspector.fixtures.Mojo;
import jodd.introspector.fixtures.One;
import jodd.introspector.fixtures.OneSub;
import jodd.introspector.fixtures.Overload;
import jodd.introspector.fixtures.TwoSub;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

public class IntrospectorTest {

	@Test
	public void testBasic() {
		ClassDescriptor cd = ClassIntrospector.lookup(Abean.class);
		assertNotNull(cd);
		PropertyDescriptor[] properties = cd.getAllPropertyDescriptors();
		int c = 0;
		for (PropertyDescriptor property : properties) {
			if (property.isFieldOnly()) continue;
			if (property.isPublic()) c++;
		}
		assertEquals(2, c);

		Arrays.sort(properties, new Comparator<PropertyDescriptor>() {
			public int compare(PropertyDescriptor o1, PropertyDescriptor o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});

		PropertyDescriptor pd = properties[0];
		assertEquals("fooProp", pd.getName());
		assertNotNull(pd.getReadMethodDescriptor());
		assertNotNull(pd.getWriteMethodDescriptor());
		assertNotNull(pd.getFieldDescriptor());

		pd = properties[1];
		assertEquals("shared", pd.getName());
		assertNull(pd.getReadMethodDescriptor());
		assertNull(pd.getWriteMethodDescriptor());
		assertNotNull(pd.getFieldDescriptor());

		pd = properties[2];
		assertEquals("something", pd.getName());
		assertNotNull(pd.getReadMethodDescriptor());
		assertNull(pd.getWriteMethodDescriptor());
		assertNull(pd.getFieldDescriptor());

		assertNotNull(cd.getPropertyDescriptor("fooProp", false));
		assertNotNull(cd.getPropertyDescriptor("something", false));
		assertNull(cd.getPropertyDescriptor("FooProp", false));
		assertNull(cd.getPropertyDescriptor("Something", false));
		assertNull(cd.getPropertyDescriptor("notExisting", false));
	}

	@Test
	public void testExtends() {
		ClassDescriptor cd = ClassIntrospector.lookup(Bbean.class);
		assertNotNull(cd);

		PropertyDescriptor[] properties = cd.getAllPropertyDescriptors();
		int c = 0;
		for (PropertyDescriptor property : properties) {
			if (property.isFieldOnly()) continue;
			if (property.isPublic()) c++;
		}
		assertEquals(2, c);

		c = 0;
		for (PropertyDescriptor property : properties) {
			if (property.isFieldOnly()) continue;
			c++;
		}
		assertEquals(3, c);
		assertEquals(4, properties.length);

		Arrays.sort(properties, new Comparator<PropertyDescriptor>() {
			public int compare(PropertyDescriptor o1, PropertyDescriptor o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});

		PropertyDescriptor pd = properties[0];
		assertEquals("boo", pd.getName());
		assertNotNull(pd.getReadMethodDescriptor());
		assertNotNull(pd.getWriteMethodDescriptor());
		assertNotNull(pd.getFieldDescriptor());
		assertFalse(pd.isFieldOnly());

		pd = properties[1];
		assertEquals("fooProp", pd.getName());
		assertNotNull(pd.getReadMethodDescriptor());
		assertNotNull(pd.getWriteMethodDescriptor());
		assertNotNull(pd.getFieldDescriptor());
		assertFalse(pd.isFieldOnly());

		pd = properties[2];
		assertEquals("shared", pd.getName());
		assertNull(pd.getReadMethodDescriptor());
		assertNull(pd.getWriteMethodDescriptor());
		assertNotNull(pd.getFieldDescriptor());
		assertTrue(pd.isFieldOnly());

		pd = properties[3];
		assertEquals("something", pd.getName());
		assertNotNull(pd.getReadMethodDescriptor());
		assertNull(pd.getWriteMethodDescriptor());
		assertNull(pd.getFieldDescriptor());
		assertFalse(pd.isFieldOnly());

		assertNotNull(cd.getPropertyDescriptor("fooProp", false));
		assertNotNull(cd.getPropertyDescriptor("something", false));
		assertNull(cd.getPropertyDescriptor("FooProp", false));
		assertNull(cd.getPropertyDescriptor("Something", false));
		assertNull(cd.getPropertyDescriptor("notExisting", false));

		assertNotNull(cd.getPropertyDescriptor("boo", true));
		assertNull(cd.getPropertyDescriptor("boo", false));
	}

	@Test
	public void testCtors() {
		ClassDescriptor cd = ClassIntrospector.lookup(Ac.class);
		CtorDescriptor[] ctors = cd.getAllCtorDescriptors();
		int c = 0;
		for (CtorDescriptor ctor : ctors) {
			if (ctor.isPublic()) c++;
		}
		assertEquals(1, c);
		ctors = cd.getAllCtorDescriptors();
		assertEquals(2, ctors.length);
		assertNotNull(cd.getDefaultCtorDescriptor(true));
		assertNull(cd.getDefaultCtorDescriptor(false));

		Constructor ctor = cd.getCtorDescriptor(new Class[] {Integer.class}, true).getConstructor();
		assertNotNull(ctor);

		cd = ClassIntrospector.lookup(Bc.class);
		ctors = cd.getAllCtorDescriptors();
		c = 0;
		for (CtorDescriptor ccc : ctors) {
			if (ccc.isPublic()) c++;
		}
		assertEquals(1, c);

		ctors = cd.getAllCtorDescriptors();
		assertEquals(1, ctors.length);
		assertNull(cd.getDefaultCtorDescriptor(false));
		assertNull(cd.getDefaultCtorDescriptor(true));

		CtorDescriptor ctorDescriptor = cd.getCtorDescriptor(new Class[] {Integer.class}, true);
		assertNull(ctorDescriptor);
		ctor = cd.getCtorDescriptor(new Class[] {String.class}, true).getConstructor();
		assertNotNull(ctor);
	}

	@Test
	public void testSameFieldDifferentClass() {
		ClassDescriptor cd = ClassIntrospector.lookup(Abean.class);

		FieldDescriptor fd = cd.getFieldDescriptor("shared", false);
		assertNull(fd);

		fd = cd.getFieldDescriptor("shared", true);
		assertNotNull(fd);

		ClassDescriptor cd2 = ClassIntrospector.lookup(Bbean.class);
		FieldDescriptor fd2 = cd2.getFieldDescriptor("shared", true);

		assertNotEquals(fd, fd2);
		assertEquals(fd.getField(), fd2.getField());
	}

	@Test
	public void testPropertyMatches() {
		ClassDescriptor cd = ClassIntrospector.lookup(Cbean.class);

		PropertyDescriptor pd;

		pd = cd.getPropertyDescriptor("s1", false);
		assertNull(pd);

		pd = cd.getPropertyDescriptor("s1", true);
		assertFalse(pd.isPublic());
		assertTrue(pd.getReadMethodDescriptor().isPublic());
		assertFalse(pd.getWriteMethodDescriptor().isPublic());

		assertNotNull(getPropertyGetterDescriptor(cd, "s1", false));
		assertNull(getPropertySetterDescriptor(cd, "s1", false));


		pd = cd.getPropertyDescriptor("s2", false);
		assertNull(pd);

		pd = cd.getPropertyDescriptor("s2", true);
		assertFalse(pd.isPublic());
		assertFalse(pd.getReadMethodDescriptor().isPublic());
		assertTrue(pd.getWriteMethodDescriptor().isPublic());

		assertNull(getPropertyGetterDescriptor(cd, "s2", false));
		assertNotNull(getPropertySetterDescriptor(cd, "s2", false));


		pd = cd.getPropertyDescriptor("s3", false);
		assertNotNull(pd);

		pd = cd.getPropertyDescriptor("s3", true);
		assertTrue(pd.isPublic());
		assertTrue(pd.getReadMethodDescriptor().isPublic());
		assertTrue(pd.getWriteMethodDescriptor().isPublic());

		assertNotNull(getPropertyGetterDescriptor(cd, "s3", false));
		assertNotNull(getPropertySetterDescriptor(cd, "s3", false));
	}

	@Test
	public void testOverload() {
		ClassDescriptor cd = ClassIntrospector.lookup(Overload.class);

		PropertyDescriptor[] pds = cd.getAllPropertyDescriptors();

		assertEquals(1, pds.length);

		PropertyDescriptor pd = pds[0];

		assertNotNull(pd.getFieldDescriptor());
		assertNotNull(pd.getReadMethodDescriptor());
		assertNull(pd.getWriteMethodDescriptor());
	}

	@Test
	public void testSerialUid() {
		ClassDescriptor cd = ClassIntrospector.lookup(Bbean.class);

		assertNull(cd.getFieldDescriptor("serialVersionUID", true));
	}

	@Test
	public void testStaticFieldsForProperties() {
		ClassDescriptor cd = ClassIntrospector.lookup(Mojo.class);

		FieldDescriptor[] fieldDescriptors = cd.getAllFieldDescriptors();
		assertEquals(3, fieldDescriptors.length);

		MethodDescriptor[] methodDescriptors = cd.getAllMethodDescriptors();
		assertEquals(2, methodDescriptors.length);

		PropertyDescriptor[] propertyDescriptor = cd.getAllPropertyDescriptors();
		assertEquals(3, propertyDescriptor.length);

		int count = 0;
		for (PropertyDescriptor pd : propertyDescriptor) {
			if (pd.isFieldOnly()) {
				continue;
			}
			count++;
		}
		assertEquals(1, count);
	}

	@Test
	public void testPropertiesOneClass() throws InvocationTargetException, IllegalAccessException {
		ClassDescriptor cd = ClassIntrospector.lookup(One.class);

		PropertyDescriptor[] propertyDescriptors = cd.getAllPropertyDescriptors();

		assertEquals(3, propertyDescriptors.length);

		assertEquals("fone", propertyDescriptors[0].getName());
		assertEquals("ftwo", propertyDescriptors[1].getName());
		assertEquals("not", propertyDescriptors[2].getName());

		for (int i = 0; i < 3; i++) {
			assertNull(propertyDescriptors[i].getWriteMethodDescriptor());
			if (i != 2) {
				assertNotNull(propertyDescriptors[i].getReadMethodDescriptor());
			} else {
				assertNull(propertyDescriptors[i].getReadMethodDescriptor());
			}
			assertNotNull(propertyDescriptors[i].getFieldDescriptor());
		}

		// change value

		One one = new One();

		Setter setter = propertyDescriptors[0].getSetter(true);

		setter.invokeSetter(one, "one!");

		assertEquals("one!", one.getFone());

		// fields

		FieldDescriptor[] fieldDescriptors = cd.getAllFieldDescriptors();
		assertEquals(3, fieldDescriptors.length);

		// beanutil

		BeanUtil.declared.setProperty(one, "fone", "!!!");
		assertEquals("!!!", one.getFone());

		// change value 2

		setter = propertyDescriptors[2].getSetter(true);
		setter.invokeSetter(one, Long.valueOf("99"));
		assertEquals(99, one.whynot());
	}

	@Test
	public void testPropertiesOneSubClass() throws InvocationTargetException, IllegalAccessException {
		ClassDescriptor cd = ClassIntrospector.lookup(OneSub.class);

		PropertyDescriptor[] propertyDescriptors = cd.getAllPropertyDescriptors();

		assertEquals(2, propertyDescriptors.length);

		assertEquals("fone", propertyDescriptors[0].getName());
		assertEquals("ftwo", propertyDescriptors[1].getName());

		for (int i = 0; i < 2; i++) {
			assertNull(propertyDescriptors[i].getWriteMethodDescriptor());
			assertNotNull(propertyDescriptors[i].getReadMethodDescriptor());
			assertNotNull(propertyDescriptors[i].getFieldDescriptor());
		}

		// change value

		OneSub one = new OneSub();

		Setter setter = propertyDescriptors[0].getSetter(true);

		setter.invokeSetter(one, "one!");

		assertEquals("one!", one.getFone());

		// fields

		FieldDescriptor[] fieldDescriptors = cd.getAllFieldDescriptors();
		assertEquals(1, fieldDescriptors.length);

		assertEquals("ftwo", fieldDescriptors[0].getName());

		// beanutil

		BeanUtil.declared.setProperty(one, "fone", "!!!");
		assertEquals("!!!", one.getFone());
	}

	@Test
	public void testPropertiesTwoSubClass() throws InvocationTargetException, IllegalAccessException {
		ClassDescriptor cd = ClassIntrospector.lookup(TwoSub.class);

		PropertyDescriptor[] propertyDescriptors = cd.getAllPropertyDescriptors();

		assertEquals(2, propertyDescriptors.length);

		assertEquals("fone", propertyDescriptors[0].getName());
		assertEquals("ftwo", propertyDescriptors[1].getName());

		for (int i = 0; i < 2; i++) {
			assertNull(propertyDescriptors[i].getWriteMethodDescriptor());
			assertNotNull(propertyDescriptors[i].getReadMethodDescriptor());
			assertNotNull(propertyDescriptors[i].getFieldDescriptor());
		}

		// change value

		TwoSub one = new TwoSub();

		Setter setter = propertyDescriptors[0].getSetter(true);

		setter.invokeSetter(one, "one!");

		assertEquals("one!", one.getFone());

		// fields

		FieldDescriptor[] fieldDescriptors = cd.getAllFieldDescriptors();
		assertEquals(1, fieldDescriptors.length);

		assertEquals("ftwo", fieldDescriptors[0].getName());

		// beanutil

		BeanUtil.declared.setProperty(one, "fone", "!!!");
		assertEquals("!!!", one.getFone());
	}


	MethodDescriptor getPropertySetterDescriptor(ClassDescriptor cd, String name, boolean declared) {
		PropertyDescriptor propertyDescriptor = cd.getPropertyDescriptor(name, true);

		if (propertyDescriptor != null) {
			MethodDescriptor setter = propertyDescriptor.getWriteMethodDescriptor();

			if ((setter != null) && setter.matchDeclared(declared)) {
				return setter;
			}
		}
		return null;
	}

	MethodDescriptor getPropertyGetterDescriptor(ClassDescriptor cd, String name, boolean declared) {
		PropertyDescriptor propertyDescriptor = cd.getPropertyDescriptor(name, true);

		if (propertyDescriptor != null) {
			MethodDescriptor getter = propertyDescriptor.getReadMethodDescriptor();

			if ((getter != null) && getter.matchDeclared(declared)) {
				return getter;
			}
		}
		return null;
	}

}
