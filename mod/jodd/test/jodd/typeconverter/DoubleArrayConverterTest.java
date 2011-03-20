// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.DoubleArrayConverter;

public class DoubleArrayConverterTest extends BaseTestCase {

	public void testConversion() {
		assertNull(DoubleArrayConverter.valueOf(null));

		assertEq(arrd(1.73), DoubleArrayConverter.valueOf(Double.valueOf(1.73)));
		assertEq(arrd(1.73, 10.22), DoubleArrayConverter.valueOf(arrd(1.73, 10.22)));
		assertEq(arrd(1.0, 7.0, 3.0), DoubleArrayConverter.valueOf(arri(1, 7, 3)));
		assertEq(arrd(1.0, 7.0, 3.0), DoubleArrayConverter.valueOf(arrl(1, 7, 3)));
		assertEq(arrd(1.0, 7.0, 3.0), DoubleArrayConverter.valueOf(arrf(1, 7, 3)));
		assertEq(arrd(1.0, 0.0, 1.0), DoubleArrayConverter.valueOf(arrl(true, false, true)));
		assertEq(arrd(1.0, 7.0, 3.0), DoubleArrayConverter.valueOf(arrb((byte)1, (byte)7, (byte)3)));
		assertEq(arrd(1.0, 7.0, 3.0), DoubleArrayConverter.valueOf(arrs((short)1, (short)7, (short)3)));
		assertEq(arrd(1.73, 10.22), DoubleArrayConverter.valueOf(arrs("1.73", "10.22")));
		assertEq(arrd(1.73, 10.22), DoubleArrayConverter.valueOf(arrs(" 1.73 ", " 10.22 ")));
		assertEq(arrd(1.73, 10), DoubleArrayConverter.valueOf(arro("1.73", Integer.valueOf(10))));
	}

	private void assertEq(double[] arr1, double[] arr2) {
		assertEquals(arr1.length, arr2.length);
		for (int i = 0; i < arr1.length; i++) {
			assertEquals(arr1[i], arr2[i], 0.0001);
		}
	}
}

