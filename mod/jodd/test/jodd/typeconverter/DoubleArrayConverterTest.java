// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

public class DoubleArrayConverterTest extends BaseTestCase {

	public void testConversion() {
		assertNull(DoubleArrayConverter.valueOf(null));

		assertEq(arrd(1.73), DoubleArrayConverter.valueOf(Double.valueOf(1.73)));
		assertEq(arrd(1.73, 10.22), DoubleArrayConverter.valueOf(arrd(1.73, 10.22)));
		assertEq(arrd(1.0, 7.0, 3.0), DoubleArrayConverter.valueOf(arri(1, 7, 3)));
		assertEq(arrd(1.73, 10.22), DoubleArrayConverter.valueOf(arrs("1.73", "10.22")));
		assertEq(arrd(1.73, 10), DoubleArrayConverter.valueOf(arr("1.73", Integer.valueOf(10))));
	}

	private void assertEq(double[] arr1, double[] arr2) {
		assertEquals(arr1.length, arr2.length);
		for (int i = 0; i < arr1.length; i++) {
			assertEquals(arr1[i], arr2[i], 0.0001);
		}
	}

}

