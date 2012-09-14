// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.LongArrayConverter;

public class LongArrayConverterTest extends BaseTestCase {

    public void testConversion() {
		LongArrayConverter longArrayConverter = (LongArrayConverter) TypeConverterManager.lookup(long[].class);

        assertNull(longArrayConverter.convert(null));

        assertEq(arrl(173), longArrayConverter.convert(Double.valueOf(173)));
        assertEq(arrl(173, 1022, 29929), longArrayConverter.convert(arrf(173, 1022, 29929)));
        assertEq(arrl(173, 1022, 29929), longArrayConverter.convert(arrd(173, 1022, 29929)));
        assertEq(arrl(173, 1022, 29929), longArrayConverter.convert(arri(173, 1022, 29929)));
        assertEq(arrl(173, 1022, 29929), longArrayConverter.convert(arrl(173, 1022, 29929)));
        assertEq(arrl(173, 1022), longArrayConverter.convert(arrs("173", "1022")));
        assertEq(arrl(173, 1022), longArrayConverter.convert(arro("173", Long.valueOf(1022))));

		assertEq(arrl(111, 777, 333), longArrayConverter.convert(arrs("111", "   777     ", "333")));
		assertEq(arrl(111, 777, 333), longArrayConverter.convert("111,  777,  333"));
    }

    private void assertEq(long[] arr1, long[] arr2) {
        assertEquals(arr1.length, arr2.length);
        for (int i = 0; i < arr1.length; i++) {
            assertEquals(arr1[i], arr2[i]);
        }
    }

}