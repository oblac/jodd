// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.BooleanArrayConverter;

public class BooleanArrayConverterTest extends BaseTestCase {

	public void testConversion() {
		assertNull(BooleanArrayConverter.valueOf(null));

		assertEq(arrl(true), BooleanArrayConverter.valueOf(Boolean.TRUE));
		assertEq(arrl(true), BooleanArrayConverter.valueOf("true"));
		assertEq(arrl(true, false, true), BooleanArrayConverter.valueOf(arrl(true, false, true)));
		assertEq(arrl(true, false, true), BooleanArrayConverter.valueOf(arri(-7, 0, 3)));
		assertEq(arrl(true, false, true), BooleanArrayConverter.valueOf(arrf(-7.0f, 0.0f, 3.0f)));
		assertEq(arrl(true, false, true), BooleanArrayConverter.valueOf(arrs("true", "0", "yes")));
		assertEq(arrl(true, false, true), BooleanArrayConverter.valueOf(arrs(" true ", "0", " yes ")));
	}


	private void assertEq(boolean[] arr1, boolean[] arr2) {
		assertEquals(arr1.length, arr2.length);
		for (int i = 0; i < arr1.length; i++) {
			assertEquals(arr1[i], arr2[i]);
		}
	}

}
