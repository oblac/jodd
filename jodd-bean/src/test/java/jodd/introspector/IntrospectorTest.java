// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector;

import jodd.introspector.tst.Abean;
import jodd.introspector.tst.Ac;
import jodd.introspector.tst.Bbean;
import jodd.introspector.tst.Bc;
import jodd.introspector.tst.Cbean;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Comparator;

import static org.junit.Assert.*;

public class IntrospectorTest {

	@Test
	public void testBasic() {
		ClassDescriptor cd = ClassIntrospector.lookup(Abean.class);
		assertNotNull(cd);
		PropertyDescriptor[] properties = cd.getAllPropertyDescriptors();
		int c = 0;
		for (PropertyDescriptor property : properties) {
			if (property.isFieldOnlyDescriptor()) continue;
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
			if (property.isFieldOnlyDescriptor()) continue;
			if (property.isPublic()) c++;
		}
		assertEquals(2, c);

		c = 0;
		for (PropertyDescriptor property : properties) {
			if (property.isFieldOnlyDescriptor()) continue;
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
		assertFalse(pd.isFieldOnlyDescriptor());

		pd = properties[1];
		assertEquals("fooProp", pd.getName());
		assertNotNull(pd.getReadMethodDescriptor());
		assertNotNull(pd.getWriteMethodDescriptor());
		assertNull(pd.getFieldDescriptor()); 	// null since field is not visible
		assertFalse(pd.isFieldOnlyDescriptor());

		pd = properties[2];
		assertEquals("shared", pd.getName());
		assertNull(pd.getReadMethodDescriptor());
		assertNull(pd.getWriteMethodDescriptor());
		assertNotNull(pd.getFieldDescriptor());
		assertTrue(pd.isFieldOnlyDescriptor());

		pd = properties[3];
		assertEquals("something", pd.getName());
		assertNotNull(pd.getReadMethodDescriptor());
		assertNull(pd.getWriteMethodDescriptor());
		assertNull(pd.getFieldDescriptor());
		assertFalse(pd.isFieldOnlyDescriptor());

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
