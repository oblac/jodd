// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IntrospectorPropertyGenericsTest {

	static class BaseAction<A, B> {
		A input;
		B output;

		public A getLina() {
			return null;
		}
	}

	static class GenAction extends BaseAction<String, Integer> {
	}

	static class Normal {
		String input;
		Integer output;
	}

	@Test
	public void testGenAction() {
		ClassDescriptor cd = ClassIntrospector.lookup(GenAction.class);

		FieldDescriptor fd = cd.getFieldDescriptor("input", true);

		assertEquals(Object.class, fd.getField().getType());
		assertEquals(String.class, fd.getRawType());

		fd = cd.getFieldDescriptor("output", true);

		assertEquals(Object.class, fd.getField().getType());
		assertEquals(Integer.class, fd.getRawType());

		PropertyDescriptor pd = cd.getPropertyDescriptor("input", true);
		assertEquals(String.class, pd.getType());

		pd = cd.getPropertyDescriptor("output", true);
		assertEquals(Integer.class, pd.getType());

		pd = cd.getPropertyDescriptor("lina", true);
		assertEquals(String.class, pd.getType());
	}

	@Test
	public void testNormal() {
		ClassDescriptor cd = ClassIntrospector.lookup(Normal.class);

		FieldDescriptor fd = cd.getFieldDescriptor("input", true);

		assertEquals(String.class, fd.getField().getType());
		assertEquals(String.class, fd.getRawType());

		fd = cd.getFieldDescriptor("output", true);

		assertEquals(Integer.class, fd.getField().getType());
		assertEquals(Integer.class, fd.getRawType());

		PropertyDescriptor pd = cd.getPropertyDescriptor("input", true);
		assertEquals(String.class, pd.getType());

		pd = cd.getPropertyDescriptor("output", true);
		assertEquals(Integer.class, pd.getType());
	}

}