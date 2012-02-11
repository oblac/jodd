// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.FloatArrayConverter;

public class FloatArrayConverterTest extends BaseTestCase {
	
	public void testConversion() {
		FloatArrayConverter floatArrayConverter = new FloatArrayConverter();
		
		assertNull(floatArrayConverter.convert(null));

		assertEq(arrf((float)1.73), floatArrayConverter.convert(Float.valueOf((float) 1.73)));
		assertEq(arrf((float)1.73, (float)10.22), floatArrayConverter.convert(arrf((float)1.73, (float)10.22)));
		assertEq(arrf((float)1.73, (float)10.22), floatArrayConverter.convert(arrd(1.73, 10.22)));
		assertEq(arrf((float)1.73, (float)10.22), floatArrayConverter.convert(arrf(1.73f, 10.22f)));
		assertEq(arrf((float)1.0, (float)7.0, (float)3.0), floatArrayConverter.convert(arri(1, 7, 3)));
		assertEq(arrf((float)1.0, (float)7.0, (float)3.0), floatArrayConverter.convert(arrl(1, 7, 3)));
		assertEq(arrf((float)1.0, (float)7.0, (float)3.0), floatArrayConverter.convert(arrb((byte)1, (byte)7, (byte)3)));
		assertEq(arrf((float)1.0, (float)7.0, (float)3.0), floatArrayConverter.convert(arrs((short)1, (short)7, (short)3)));
		assertEq(arrf((float)1.73, (float)10.22), floatArrayConverter.convert(arrs("1.73", "10.22")));
		assertEq(arrf((float)1.73, (float)10.22), floatArrayConverter.convert(arrs(" 1.73 ", " 10.22 ")));
		assertEq(arrf((float)1.73, 10), floatArrayConverter.convert(arro("1.73", Integer.valueOf(10))));
	}

	private void assertEq(float[] arr1, float[] arr2) {
		assertEquals(arr1.length, arr2.length);
		for (int i = 0; i < arr1.length; i++) {
			assertEquals(arr1[i], arr2[i], 0.0001);
		}
	}

}


