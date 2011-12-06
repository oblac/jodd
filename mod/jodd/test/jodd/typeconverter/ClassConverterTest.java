// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.ClassConverter;

public class ClassConverterTest extends BaseTestCase {

	public void testConversion() {
		assertNull(ClassConverter.valueOf(null));

		assertEquals(String.class, ClassConverter.valueOf(String.class));
		assertEquals(Integer.class, ClassConverter.valueOf("java.lang.Integer"));

		try {
			ClassConverter.valueOf("foo.Klass");
			fail();
		} catch (TypeConversionException ignore) {
		}
	}
}

