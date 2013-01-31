// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.ClassConverter;
import org.junit.Test;

import static org.junit.Assert.*;

public class ClassConverterTest {

	@Test
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

