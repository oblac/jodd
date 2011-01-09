// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean;

import junit.framework.TestCase;
import jodd.bean.data.Woof;
import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;

import java.lang.reflect.Method;

public class BeanUtilGenericsTest extends TestCase {

	public void testOne() {
		Woof woof = new Woof();
		Class type = woof.getClass();
		ClassDescriptor cd = ClassIntrospector.lookup(type);
		Method[] allSetters = cd.getAllBeanSetters(true);
		assertNotNull(allSetters);
		assertEquals(7, allSetters.length);
	}
}
