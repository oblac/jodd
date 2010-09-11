// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

public class LongArrayConverterTest extends BaseTestCase {

    public void testConversion() {
        assertNull(LongArrayConverter.valueOf(null));

        assertEq(arrl(173), LongArrayConverter.valueOf(Double.valueOf(173)));
        assertEq(arrl(173, 1022, 29929), LongArrayConverter.valueOf(arrf(173, 1022, 29929)));
        assertEq(arrl(173, 1022, 29929), LongArrayConverter.valueOf(arrd(173, 1022, 29929)));
        assertEq(arrl(173, 1022, 29929), LongArrayConverter.valueOf(arri(173, 1022, 29929)));
        assertEq(arrl(173, 1022, 29929), LongArrayConverter.valueOf(arrl(173, 1022, 29929)));
        assertEq(arrl(173, 1022), LongArrayConverter.valueOf(arrs("173", "1022")));
        assertEq(arrl(173, 1022), LongArrayConverter.valueOf(arro("173", Long.valueOf(1022))));
    }

    private void assertEq(long[] arr1, long[] arr2) {
        assertEquals(arr1.length, arr2.length);
        for (int i = 0; i < arr1.length; i++) {
            assertEquals(arr1[i], arr2[i]);
        }
    }

}