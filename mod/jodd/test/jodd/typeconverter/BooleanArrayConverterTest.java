// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.BooleanArrayConverter;

public class BooleanArrayConverterTest extends BaseTestCase {

	public void testConversion() {
		BooleanArrayConverter booleanArrayConverter = (BooleanArrayConverter) TypeConverterManager.lookup(boolean[].class);

		assertNull(booleanArrayConverter.convert(null));
		
		boolean[] primitiveArray = new boolean[] {false, true, false};
		Object convertedArray = booleanArrayConverter.convert(primitiveArray);
		assertEquals(boolean[].class, convertedArray.getClass());

		Boolean[] booleanArray = new Boolean[] {Boolean.FALSE, Boolean.TRUE, Boolean.FALSE};
		convertedArray = booleanArrayConverter.convert(booleanArray);
		assertEquals(boolean[].class, convertedArray.getClass());	// boolean[]!

		assertEq(arrl(true), booleanArrayConverter.convert(Boolean.TRUE));
		assertEq(arrl(true), booleanArrayConverter.convert("true"));
		assertEq(arrl(true, false, true), booleanArrayConverter.convert(arrl(true, false, true)));
		assertEq(arrl(true, false, true), booleanArrayConverter.convert(arri(-7, 0, 3)));
		assertEq(arrl(true, false, true), booleanArrayConverter.convert(arrf(-7.0f, 0.0f, 3.0f)));
		assertEq(arrl(true, false, true), booleanArrayConverter.convert(arrs("true", "0", "yes")));
		assertEq(arrl(true, false, true), booleanArrayConverter.convert(arrs(" true ", "0", " yes ")));
	}

	public void testArrayConversion() {
		Object[] booleanArray = new Object[] {Boolean.FALSE, "TRUE", Integer.valueOf(0)};
		
		boolean[] arr1 = TypeConverterManager.convertType(booleanArray, boolean[].class);
		assertEquals(3, arr1.length);
		assertEq(arrl(false, true, false), arr1);

		Boolean[] arr2 = TypeConverterManager.convertType(booleanArray, Boolean[].class);
		assertEquals(3, arr2.length);
		assertEq(arrl(false, true, false), arr2);
	}


	private void assertEq(boolean[] arr1, boolean[] arr2) {
		assertEquals(arr1.length, arr2.length);
		for (int i = 0; i < arr1.length; i++) {
			assertEquals(arr1[i], arr2[i]);
		}
	}

	private void assertEq(boolean[] arr1, Boolean[] arr2) {
		assertEquals(arr1.length, arr2.length);
		for (int i = 0; i < arr1.length; i++) {
			assertEquals(arr1[i], arr2[i].booleanValue());
		}
	}

}
