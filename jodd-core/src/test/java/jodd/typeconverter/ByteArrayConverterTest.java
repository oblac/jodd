// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.ByteArrayConverter;
import org.junit.Test;

import static jodd.typeconverter.TypeConverterTestHelper.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ByteArrayConverterTest {

	ByteArrayConverter byteArrayConverter = (ByteArrayConverter) TypeConverterManager.lookup(byte[].class);

	@Test
	public void testArrayConversion() {
		assertNull(byteArrayConverter.convert(null));

		assertEq(arrb(1, 7, 3), byteArrayConverter.convert(arrb(1, 7, 3)));
		assertEq(arrb(1, 7, 3), byteArrayConverter.convert(arrs(1, 7, 3)));
		assertEq(arrb(1, 0, 1), byteArrayConverter.convert(arrl(true, false, true)));
		assertEq(arrb(1, 7, 3), byteArrayConverter.convert(arri(1, 7, 3)));
		assertEq(arrb(1, 7, 3), byteArrayConverter.convert(arrl(1, 7, 3)));
		assertEq(arrb(1, 7, 3), byteArrayConverter.convert(arrf(1.99f, 7.99f, 3.22f)));
		assertEq(arrb(1, 7, 3), byteArrayConverter.convert(arrd(1.99, 7.99, 3.22)));
		assertEq(arrb(1, 7, 3), byteArrayConverter.convert(arrs("1", "7", "3")));
		assertEq(arrb(1, 7, 3), byteArrayConverter.convert(arrs(" 1 ", " 7 ", " 3 ")));
		assertEq(arrb(1, 7, 3), byteArrayConverter.convert(" 1 ,  7 ,  3 "));
	}

	@Test
	public void testNonArrayConversion() {
		assertEq(arrb(7), byteArrayConverter.convert(Byte.valueOf((byte) 7)));
		assertEq(arrb(7), byteArrayConverter.convert(Integer.valueOf(7)));
		assertEq(arrb(7), byteArrayConverter.convert("7"));
	}

	private void assertEq(byte[] arr1, byte[] arr2) {
		assertEquals(arr1.length, arr2.length);
		for (int i = 0; i < arr1.length; i++) {
			assertEquals(arr1[i], arr2[i]);
		}
	}

}
