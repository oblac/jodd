// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.ByteArrayConverter;

public class ByteArrayConverterTest extends BaseTestCase {

	ByteArrayConverter byteArrayConverter = (ByteArrayConverter) TypeConverterManager.lookup(byte[].class);

	public void testArrayConversion() {
		assertNull(byteArrayConverter.convert(null));

		assertEq(arrb((byte)1, (byte)7, (byte)3), byteArrayConverter.convert(arrb((byte)1, (byte)7, (byte)3)));
		assertEq(arrb((byte)1, (byte)7, (byte)3), byteArrayConverter.convert(arrs((short)1, (short)7, (short)3)));
		assertEq(arrb((byte)1, (byte)0, (byte)1), byteArrayConverter.convert(arrl(true, false, true)));
		assertEq(arrb((byte)1, (byte)7, (byte)3), byteArrayConverter.convert(arri(1, 7, 3)));
		assertEq(arrb((byte)1, (byte)7, (byte)3), byteArrayConverter.convert(arrl(1, 7, 3)));
		assertEq(arrb((byte)1, (byte)7, (byte)3), byteArrayConverter.convert(arrf(1.99f, 7.99f, 3.22f)));
		assertEq(arrb((byte)1, (byte)7, (byte)3), byteArrayConverter.convert(arrd(1.99, 7.99, 3.22)));
		assertEq(arrb((byte)1, (byte)7, (byte)3), byteArrayConverter.convert(arrs("1", "7", "3")));
		assertEq(arrb((byte)1, (byte)7, (byte)3), byteArrayConverter.convert(arrs(" 1 ", " 7 ", " 3 ")));
		assertEq(arrb((byte)1, (byte)7, (byte)3), byteArrayConverter.convert(" 1 ,  7 ,  3 "));
	}

	public void testNonArrayConversion() {
		assertEq(arrb((byte)7), byteArrayConverter.convert(Byte.valueOf((byte) 7)));
		assertEq(arrb((byte)7), byteArrayConverter.convert(Integer.valueOf(7)));
		assertEq(arrb((byte)7), byteArrayConverter.convert("7"));
	}

	private void assertEq(byte[] arr1, byte[] arr2) {
		assertEquals(arr1.length, arr2.length);
		for (int i = 0; i < arr1.length; i++) {
			assertEquals(arr1[i], arr2[i]);
		}
	}

}
