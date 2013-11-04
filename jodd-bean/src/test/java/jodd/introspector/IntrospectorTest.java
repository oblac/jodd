// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

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

		pd = properties[1];
		assertEquals("something", pd.getName());
		assertNotNull(pd.getReadMethodDescriptor());
		assertNull(pd.getWriteMethodDescriptor());

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
			if (property.isPublic()) c++;
		}
		assertEquals(2, c);

		properties = cd.getAllPropertyDescriptors();
		assertEquals(3, properties.length);

		Arrays.sort(properties, new Comparator<PropertyDescriptor>() {
			public int compare(PropertyDescriptor o1, PropertyDescriptor o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});

		PropertyDescriptor pd = properties[0];
		assertEquals("boo", pd.getName());
		assertNotNull(pd.getReadMethodDescriptor());
		assertNotNull(pd.getWriteMethodDescriptor());

		pd = properties[1];
		assertEquals("fooProp", pd.getName());
		assertNotNull(pd.getReadMethodDescriptor());
		assertNotNull(pd.getWriteMethodDescriptor());

		pd = properties[2];
		assertEquals("something", pd.getName());
		assertNotNull(pd.getReadMethodDescriptor());
		assertNull(pd.getWriteMethodDescriptor());

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

		assertNotNull(cd.getPropertyGetterDescriptor("s1", false));
		assertNull(cd.getPropertySetterDescriptor("s1", false));


		pd = cd.getPropertyDescriptor("s2", false);
		assertNull(pd);

		pd = cd.getPropertyDescriptor("s2", true);
		assertFalse(pd.isPublic());
		assertFalse(pd.getReadMethodDescriptor().isPublic());
		assertTrue(pd.getWriteMethodDescriptor().isPublic());

		assertNull(cd.getPropertyGetterDescriptor("s2", false));
		assertNotNull(cd.getPropertySetterDescriptor("s2", false));


		pd = cd.getPropertyDescriptor("s3", false);
		assertNotNull(pd);

		pd = cd.getPropertyDescriptor("s3", true);
		assertTrue(pd.isPublic());
		assertTrue(pd.getReadMethodDescriptor().isPublic());
		assertTrue(pd.getWriteMethodDescriptor().isPublic());

		assertNotNull(cd.getPropertyGetterDescriptor("s3", false));
		assertNotNull(cd.getPropertySetterDescriptor("s3", false));
	}
}
