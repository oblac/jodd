// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.mutable.MutableInteger;
import jodd.typeconverter.impl.ClassConverter;
import jodd.util.testdata.A;
import jodd.util.testdata.B;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

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

	@Test
	public void testCast() {

		String s = "123";
		Integer d = TypeConverterManager.convertType(s, Integer.class);
		assertEquals(123, d.intValue());

		s = TypeConverterManager.convertType(d, String.class);
		assertEquals("123", s);

		MutableInteger md = TypeConverterManager.convertType(s, MutableInteger.class);
		assertEquals(123, md.intValue());

		B b = new B();
		A a = TypeConverterManager.convertType(b, A.class);
		assertEquals(a, b);
	}

}

