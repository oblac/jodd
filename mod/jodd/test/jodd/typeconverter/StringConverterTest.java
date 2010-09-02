// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import junit.framework.TestCase;

public class StringConverterTest extends TestCase {

	public void testManager() {
		TypeConverter<String> tc = TypeConverterManager.lookup(String.class);
		assertEquals("123", tc.convert(Integer.valueOf(123)));
	}
}
