// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.ByteArrayConverter;

public class ByteArrayConverterTest extends BaseTestCase {

	public void testArrayConversion() {
		assertNull(ByteArrayConverter.valueOf(null));

		assertEq(arrb((byte)1, (byte)7, (byte)3), ByteArrayConverter.valueOf(arrb((byte)1, (byte)7, (byte)3)));
		assertEq(arrb((byte)1, (byte)7, (byte)3), ByteArrayConverter.valueOf(arrs((short)1, (short)7, (short)3)));
		assertEq(arrb((byte)1, (byte)0, (byte)1), ByteArrayConverter.valueOf(arrl(true, false, true)));
		assertEq(arrb((byte)1, (byte)7, (byte)3), ByteArrayConverter.valueOf(arri(1, 7, 3)));
		assertEq(arrb((byte)1, (byte)7, (byte)3), ByteArrayConverter.valueOf(arrl(1, 7, 3)));
		assertEq(arrb((byte)1, (byte)7, (byte)3), ByteArrayConverter.valueOf(arrf(1.99f, 7.99f, 3.22f)));
		assertEq(arrb((byte)1, (byte)7, (byte)3), ByteArrayConverter.valueOf(arrd(1.99, 7.99, 3.22)));
		assertEq(arrb((byte)1, (byte)7, (byte)3), ByteArrayConverter.valueOf(arrs("1", "7", "3")));
		assertEq(arrb((byte)1, (byte)7, (byte)3), ByteArrayConverter.valueOf(arrs(" 1 ", " 7 ", " 3 ")));
		assertEq(arrb((byte)1, (byte)7, (byte)3), ByteArrayConverter.valueOf(" 1 ,  7 ,  3 "));
	}

	public void testNonArrayConversion() {
		assertEq(arrb((byte)7), ByteArrayConverter.valueOf(Byte.valueOf((byte) 7)));
		assertEq(arrb((byte)7), ByteArrayConverter.valueOf(Integer.valueOf(7)));
		assertEq(arrb((byte)7), ByteArrayConverter.valueOf("7"));
	}

	private void assertEq(byte[] arr1, byte[] arr2) {
		assertEquals(arr1.length, arr2.length);
		for (int i = 0; i < arr1.length; i++) {
			assertEquals(arr1[i], arr2[i]);
		}
	}

}
