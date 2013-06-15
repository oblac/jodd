//  Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class BeanIndexPropertyTest {

	public static class Simple<T extends Number> {
		public T data;
	}
	public static class SimpleLong extends Simple<Long> {}

	public static class Bean1 {
		public Simple[] array1;
		public SimpleLong[] array2;
		public List<Simple> list1;
	}

	public static class Bean2<T extends Number> {
		public T[] array1;
		public List<T> list1;
	}
	public static class Bean2Long extends Bean2<Long> {}


	@Test
	public void testGetSetArray1() {
		Bean1 bean1 = new Bean1();

		assertNull(BeanUtil.getProperty(bean1, "array1"));
		try {
			BeanUtil.getProperty(bean1, "array1[0]");
			fail();	// fails to read index of null property
		} catch (BeanException bex) {
			System.out.println(bex);
		}

		// array1[0]

		try {
			BeanUtil.setProperty(bean1, "array1[0].data", Integer.valueOf(173));
			fail();
		} catch (BeanException ignored) {
		}

		BeanUtil.setPropertyForced(bean1, "array1[0].data", Integer.valueOf(173));
		assertNotNull(bean1.array1);
		assertEquals(Integer.valueOf(173), bean1.array1[0].data);

		// array2[0]

		try {
			BeanUtil.setProperty(bean1, "array2[0].data", Integer.valueOf(173));
			fail();
		} catch (BeanException ignored) {
		}

		BeanUtil.setPropertyForced(bean1, "array2[0].data", Integer.valueOf(173));
		assertNotNull(bean1.array2);
		assertEquals(Long.valueOf(173), bean1.array2[0].data);
	}

	@Test
	public void testGetSetArray2() {
		Bean2 bean2 = new Bean2();
		Bean2Long bean2Long = new Bean2Long();

		// array1[0]

		try {
			BeanUtil.setProperty(bean2, "array1[0]", Integer.valueOf(173));
			fail();
		} catch (BeanException ignore) {
		}

		BeanUtil.setPropertyForced(bean2, "array1[0]", Integer.valueOf(173));
		assertNotNull(bean2.array1);
		assertEquals(Integer.valueOf(173), bean2.array1[0]);

		try {
			BeanUtil.setProperty(bean2Long, "array1[0]", Integer.valueOf(173));
			fail();
		} catch (BeanException ignore) {
		}

		BeanUtil.setPropertyForced(bean2Long, "array1[0]", Integer.valueOf(173));
		assertNotNull(bean2Long.array1);
		assertEquals(Long.valueOf(173), bean2Long.array1[0]);
		assertEquals(Long.class, bean2Long.array1[0].getClass());
	}

	// ---------------------------------------------------------------- list

	@Test
	public void testGetSetList() {
		Bean1 bean1 = new Bean1();

		assertNull(BeanUtil.getProperty(bean1, "list1"));
		try {
			BeanUtil.getProperty(bean1, "list1[0]");
			fail();	// fails to read index of null property
		} catch (BeanException ignored) {
		}

		try {
			BeanUtil.setProperty(bean1, "list1[0].data", Integer.valueOf(173));
			fail();
		} catch (BeanException ignored) {
		}

		BeanUtil.setPropertyForced(bean1, "list1[0].data", Integer.valueOf(173));
		assertNotNull(bean1.list1);
		assertEquals(Integer.valueOf(173), bean1.list1.get(0).data);
	}

	@Test
	public void testGetSetList2() {
		Bean2 bean2 = new Bean2();
		Bean2Long bean2Long = new Bean2Long();

		// array1[0]

		try {
			BeanUtil.setProperty(bean2, "list1[0]", Integer.valueOf(173));
			fail();
		} catch (BeanException ignore) {
		}

		BeanUtil.setPropertyForced(bean2, "list1[0]", Integer.valueOf(173));
		assertNotNull(bean2.list1);
		assertEquals(Integer.valueOf(173), bean2.list1.get(0));

		try {
			BeanUtil.setProperty(bean2Long, "list1[0]", Integer.valueOf(173));
			fail();
		} catch (BeanException ignore) {
		}

		BeanUtil.setPropertyForced(bean2Long, "list1[0]", Integer.valueOf(173));
		assertNotNull(bean2Long.list1);
		assertEquals(Long.valueOf(173), bean2Long.list1.get(0));
		assertEquals(Long.class, bean2Long.list1.get(0).getClass());
	}


}