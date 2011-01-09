// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.ShortArrayConverter;

public class ShortArrayConverterTest extends BaseTestCase {

    public void testConversion() {
        assertNull(ShortArrayConverter.valueOf(null));

        assertEq(arrs((short) 1), ShortArrayConverter.valueOf(Double.valueOf(1)));
        assertEq(arrs((short) 1, (short) 7, (short) 3), ShortArrayConverter.valueOf(arrs((short) 1, (short) 7, (short) 3)));
        assertEq(arrs((short) 1, (short) 7, (short) 3), ShortArrayConverter.valueOf(arrb((byte) 1, (byte) 7, (byte) 3)));
        assertEq(arrs((short) 1, (short) 7, (short) 3), ShortArrayConverter.valueOf(arri(1, 7, 3)));
        assertEq(arrs((short) 173, (short) 1022), ShortArrayConverter.valueOf(arrs("173", "1022")));
        assertEq(arrs((short) 173, (short) 10), ShortArrayConverter.valueOf(arro("173", Integer.valueOf(10))));
    }

    private void assertEq(short[] arr1, short[] arr2) {
        assertEquals(arr1.length, arr2.length);
        for (int i = 0; i < arr1.length; i++) {
            assertEquals(arr1[i], arr2[i]);
        }
    }

}

