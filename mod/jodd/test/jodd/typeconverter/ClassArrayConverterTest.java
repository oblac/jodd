// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

public class ClassArrayConverterTest extends BaseTestCase {

	@SuppressWarnings({"unchecked"})
	public void testConversion() {
		assertNull(ClassArrayConverter.valueOf(null));

		assertEq(arr(String.class), ClassArrayConverter.valueOf(String.class));
		assertEq(arr(String.class, Integer.class), ClassArrayConverter.valueOf(arr(String.class, Integer.class)));
		assertEq(arr(Integer.class), ClassArrayConverter.valueOf("java.lang.Integer"));
		assertEq(arr(Integer.class, String.class), ClassArrayConverter.valueOf("java.lang.Integer,    java.lang.String"));

		try {
			ClassArrayConverter.valueOf("foo.Klass");
			fail();
		} catch (TypeConversionException ignore) {
		}
	}

	private void assertEq(Class<String>[] arr1, Class[] arr2) {
		assertEquals(arr1.length, arr2.length);
		for (int i = 0; i < arr1.length; i++) {
			assertEquals(arr1[i], arr2[i]);
		}
	}
}

