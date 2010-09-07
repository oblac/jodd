// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

public class BooleanArrayConverterTest extends BaseTestCase {

	public void testConversion() {
		assertNull(BooleanArrayConverter.valueOf(null));

		assertEq(arr(true), BooleanArrayConverter.valueOf(Boolean.TRUE));
		assertEq(arr(true), BooleanArrayConverter.valueOf("true"));
		assertEq(arr(true, false, true), BooleanArrayConverter.valueOf(arr(true, false, true)));
		assertEq(arr(true, false, true), BooleanArrayConverter.valueOf(arri(7, 0, 3)));
		assertEq(arr(true, false, true), BooleanArrayConverter.valueOf(arrs("true", "0", "yes")));
	}


	private void assertEq(boolean[] arr1, boolean[] arr2) {
		assertEquals(arr1.length, arr2.length);
		for (int i = 0; i < arr1.length; i++) {
			assertEquals(arr1[i], arr2[i]);
		}
	}

}
