// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.StringConverter;

public class StringConverterTest extends BaseTestCase {

	public void testManager() {
		StringConverter stringConverter = new StringConverter();
		
		assertNull(stringConverter.convert(null));

		assertEquals("123", stringConverter.convert("123"));
		assertEquals("AB", stringConverter.convert(arrb((byte)65, (byte)66)));
		assertEquals("Ab", stringConverter.convert(arrc('A', 'b')));
		assertEquals("One,two", stringConverter.convert(arrs("One", "two")));
		assertEquals("123", stringConverter.convert(Integer.valueOf(123)));
		assertEquals("java.lang.String", stringConverter.convert(String.class));
	}
}
