// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.StringArrayConverter;
import org.junit.Test;

import static jodd.typeconverter.TypeConverterTestHelper.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class StringArrayConverterTest {

	@Test
	public void testConversion() {
		StringArrayConverter stringArrayConverter = (StringArrayConverter) TypeConverterManager.lookup(String[].class);

		assertNull(stringArrayConverter.convert(null));

		assertEq(arrs(Double.class.getName()), stringArrayConverter.convert(Double.class));
		assertEq(arrs("173"), stringArrayConverter.convert("173"));
		assertEq(arrs("173", "1022"), stringArrayConverter.convert("173,1022"));
		assertEq(arrs("173", " 1022"), stringArrayConverter.convert("173, 1022"));
		assertEq(arrs("173", "1022"), stringArrayConverter.convert(arrs("173", "1022")));
		assertEq(arrs("1", "7", "3"), stringArrayConverter.convert(arri(1, 7, 3)));
		assertEq(arrs("1", "7", "3"), stringArrayConverter.convert(arrl(1, 7, 3)));
		assertEq(arrs("1.0", "7.0", "3.0"), stringArrayConverter.convert(arrd(1, 7, 3)));
		assertEq(arrs("1.0", "7.0", "3.0"), stringArrayConverter.convert(arrf(1, 7, 3)));
		assertEq(arrs("173", "true"), stringArrayConverter.convert(arro("173", Boolean.TRUE)));
		assertEq(arrs("173", "java.lang.String"), stringArrayConverter.convert(arro("173", String.class)));
	}

	private void assertEq(String[] arr1, String[] arr2) {
		assertEquals(arr1.length, arr2.length);
		for (int i = 0; i < arr1.length; i++) {
			assertEquals(arr1[i], arr2[i]);
		}
	}

}
