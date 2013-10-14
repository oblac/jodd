// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.StringConverter;
import org.junit.Test;

import static jodd.typeconverter.TypeConverterTestHelper.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class StringConverterTest {

	@Test
	public void testManager() {
		StringConverter stringConverter = new StringConverter();

		assertNull(stringConverter.convert(null));

		assertEquals("123", stringConverter.convert("123"));
		assertEquals("AB", stringConverter.convert(arrb(65, 66)));
		assertEquals("Ab", stringConverter.convert(arrc('A', 'b')));
		assertEquals("One,two", stringConverter.convert(arrs("One", "two")));
		assertEquals("123", stringConverter.convert(Integer.valueOf(123)));
		assertEquals("java.lang.String", stringConverter.convert(String.class));
	}
}
