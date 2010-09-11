// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

public class StringConverterTest extends BaseTestCase {

	public void testManager() {
		assertNull(StringConverter.valueOf(null));

		assertEquals("123", StringConverter.valueOf("123"));
		assertEquals("AB", StringConverter.valueOf(arrb((byte)65, (byte)66)));
		assertEquals("Ab", StringConverter.valueOf(arrc('A', 'b')));
		assertEquals("One,two", StringConverter.valueOf(arrs("One", "two")));
		assertEquals("123", StringConverter.valueOf(Integer.valueOf(123)));
		assertEquals("java.lang.String", StringConverter.valueOf(String.class));
	}
}
