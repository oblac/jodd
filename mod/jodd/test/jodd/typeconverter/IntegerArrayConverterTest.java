// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.IntegerArrayConverter;

public class IntegerArrayConverterTest extends BaseTestCase {

	public void testConversion() {
		assertNull(IntegerArrayConverter.valueOf(null));

		assertEq(arri(173, 234), IntegerArrayConverter.valueOf("173, 234"));
		assertEq(arri(173), IntegerArrayConverter.valueOf(Double.valueOf(173)));
		assertEq(arri(1, 7, 3), IntegerArrayConverter.valueOf(arri(1, 7, 3)));
		assertEq(arri(1, 7, 3), IntegerArrayConverter.valueOf(arrl(1, 7, 3)));
		assertEq(arri(1, 7, 3), IntegerArrayConverter.valueOf(arrf(1, 7, 3)));
		assertEq(arri(1, 7, 3), IntegerArrayConverter.valueOf(arrd(1.1, 7.99, 3)));
		assertEq(arri(173, 1022), IntegerArrayConverter.valueOf(arrs("173", "1022")));
		assertEq(arri(173, 10), IntegerArrayConverter.valueOf(arro("173", Integer.valueOf(10))));

		assertEq(arri(111, 777, 333), IntegerArrayConverter.valueOf(arrs("111", "   777     ", "333")));
		assertEq(arri(111, 777, 333), IntegerArrayConverter.valueOf("111,  777,  333"));

	}

	private void assertEq(int[] arr1, int[] arr2) {
		assertEquals(arr1.length, arr2.length);
		for (int i = 0; i < arr1.length; i++) {
			assertEquals(arr1[i], arr2[i]);
		}
	}

}

