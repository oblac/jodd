// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.ClassConverter;

public class ClassConverterTest extends BaseTestCase {

	public void testConversion() {
		ClassConverter classConverter = new ClassConverter();
		
		assertNull(classConverter.convert(null));

		assertEquals(String.class, classConverter.convert(String.class));
		assertEquals(Integer.class, classConverter.convert("java.lang.Integer"));

		try {
			classConverter.convert("foo.Klass");
			fail();
		} catch (TypeConversionException ignore) {
		}
	}
}

