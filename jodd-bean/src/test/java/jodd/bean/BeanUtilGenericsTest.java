// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean;

import jodd.bean.data.Woof;
import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BeanUtilGenericsTest {

	@Test
	public void testOne() {
		Woof woof = new Woof();
		Class type = woof.getClass();
		ClassDescriptor cd = ClassIntrospector.lookup(type);
		Method[] allSetters = cd.getAllBeanSetters(true);
		assertNotNull(allSetters);
		assertEquals(7, allSetters.length);
	}
}
