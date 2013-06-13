//  Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector;

import org.junit.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class IntrospectorGenericsTest {

	public static class MethodParameterType<A> {
		<T extends List<T>> void m(A a, String p1, T p2, List<?> p3, List<T> p4) { }
	}

	public static class Foo extends MethodParameterType<Integer> {}

	@Test
	public void testMethods() throws NoSuchMethodException {
		ClassDescriptor cd = ClassIntrospector.lookup(MethodParameterType.class);

		assertEquals(MethodParameterType.class, cd.getType());
		assertEquals(0, cd.getMethodsCount(false));
		assertEquals(1, cd.getMethodsCount(true));

		Class[] params = new Class[] {Object.class, String.class, List.class, List.class, List.class};

		Method m = MethodParameterType.class.getDeclaredMethod("m", params);
		assertNotNull(m);

		Method m2 = cd.getMethod("m", params, true);
		assertNotNull(m2);
		assertEquals(m, m2);

		MethodDescriptor md2 = cd.getMethodDescriptor("m", params, true);
		assertNotNull(md2);
		assertEquals(m, md2.getMethod());

		assertArrayEquals(params, md2.getRawParameterTypes());

		// impl

		Class[] params2 = new Class[] {Integer.class, String.class, List.class, List.class, List.class};

		ClassDescriptor cd1 = ClassIntrospector.lookup(Foo.class);

		assertEquals(0, Foo.class.getDeclaredMethods().length);

		Method[] allm = cd1.getAllMethods(true);

		assertEquals(1, allm.length);

		MethodDescriptor md3 = cd1.getMethodDescriptor("m", params, true);
		assertNotNull(md3);

		assertArrayEquals(params2, md3.getRawParameterTypes());

	}
}