// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.StringArrayConverter;

public class StringArrayConverterTest extends BaseTestCase {

    public void testConversion() {
        assertNull(StringArrayConverter.valueOf(null));

        assertEq(arrs(Double.class.getName()), StringArrayConverter.valueOf(Double.class));
        assertEq(arrs("173"), StringArrayConverter.valueOf("173"));
        assertEq(arrs("173", "1022"), StringArrayConverter.valueOf("173,1022"));
        assertEq(arrs("173", " 1022"), StringArrayConverter.valueOf("173, 1022"));
        assertEq(arrs("173", "1022"), StringArrayConverter.valueOf(arrs("173", "1022")));
        assertEq(arrs("1", "7", "3"), StringArrayConverter.valueOf(arri(1, 7, 3)));
        assertEq(arrs("1", "7", "3"), StringArrayConverter.valueOf(arrl(1, 7, 3)));
        assertEq(arrs("1.0", "7.0", "3.0"), StringArrayConverter.valueOf(arrd(1, 7, 3)));
        assertEq(arrs("1.0", "7.0", "3.0"), StringArrayConverter.valueOf(arrf(1, 7, 3)));
        assertEq(arrs("173", "true"), StringArrayConverter.valueOf(arro("173", Boolean.TRUE)));
        assertEq(arrs("173", "java.lang.String"), StringArrayConverter.valueOf(arro("173", String.class)));
    }

    private void assertEq(String[] arr1, String[] arr2) {
        assertEquals(arr1.length, arr2.length);
        for (int i = 0; i < arr1.length; i++) {
            assertEquals(arr1[i], arr2[i]);
        }
    }

}
