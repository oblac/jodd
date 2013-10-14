// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.ShortArrayConverter;
import org.junit.Test;

import static jodd.typeconverter.TypeConverterTestHelper.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ShortArrayConverterTest {

	@Test
	public void testConversion() {
		ShortArrayConverter shortArrayConverter = (ShortArrayConverter) TypeConverterManager.lookup(short[].class);

		assertNull(shortArrayConverter.convert(null));

		assertEq(arrs(1), shortArrayConverter.convert(Double.valueOf(1)));
		assertEq(arrs(1, 7, 3), shortArrayConverter.convert(arrs(1, 7, 3)));
		assertEq(arrs(1, 7, 3), shortArrayConverter.convert(arrb(1, 7, 3)));
		assertEq(arrs(1, 7, 3), shortArrayConverter.convert(arri(1, 7, 3)));
		assertEq(arrs(173, 1022), shortArrayConverter.convert(arrs("173", "1022")));
		assertEq(arrs(173, 1022), shortArrayConverter.convert(arrs(" 173 ", " 1022 ")));
		assertEq(arrs(173, 10), shortArrayConverter.convert(arro("173", Integer.valueOf(10))));
	}

	private void assertEq(short[] arr1, short[] arr2) {
		assertEquals(arr1.length, arr2.length);
		for (int i = 0; i < arr1.length; i++) {
			assertEquals(arr1[i], arr2[i]);
		}
	}

}

