// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import org.junit.Test;

import static jodd.typeconverter.TypeConverterTestHelper.arrc;
import static org.junit.Assert.*;

public class ClassArrayConverterTest {

	@Test
	@SuppressWarnings({"unchecked"})
	public void testConversion() {
		TypeConverter<Class[]> classArrayConverter = TypeConverterManager.lookup(Class[].class);

		assertNull(classArrayConverter.convert(null));

		assertEq(arrc(String.class), classArrayConverter.convert(String.class));
		assertEq(arrc(String.class, Integer.class), classArrayConverter.convert(arrc(String.class, Integer.class)));
		assertEq(arrc(Integer.class), classArrayConverter.convert("java.lang.Integer"));
		assertEq(arrc(Integer.class, String.class), classArrayConverter.convert("java.lang.Integer,    java.lang.String"));

		try {
			classArrayConverter.convert("foo.Klass");
			fail();
		} catch (TypeConversionException ignore) {
		}

		assertEq(arrc(Integer.class, String.class), classArrayConverter.convert("java.lang.Integer\n\n  java.lang.String  \n\n#java.lang.Long"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testMoreClassArrayConversions() {
		assertEq(arrc(String.class), TypeConverterManager.convertType("java.lang.String,\n\r", Class[].class));
		assertEq(arrc(String.class), TypeConverterManager.convertType("java.lang.String,\r\n", Class[].class));
		assertEq(arrc(String.class), TypeConverterManager.convertType("java.lang.String,\r\r", Class[].class));
		assertEq(arrc(String.class), TypeConverterManager.convertType("java.lang.String,\r\r\r", Class[].class));
		assertEq(arrc(String.class), TypeConverterManager.convertType("java.lang.String,\n\n\n", Class[].class));
		assertEq(arrc(String.class), TypeConverterManager.convertType("java.lang.String,\n", Class[].class));
		assertEq(arrc(String.class), TypeConverterManager.convertType("java.lang.String,\r", Class[].class));

		assertEq(arrc(String.class), TypeConverterManager.convertType("java.lang.String,\r\n\r", Class[].class));
		assertEq(arrc(String.class), TypeConverterManager.convertType("\r\njava.lang.String,\r\n", Class[].class));
	}

	private void assertEq(Class<String>[] arr1, Class[] arr2) {
		assertEquals(arr1.length, arr2.length);
		for (int i = 0; i < arr1.length; i++) {
			assertEquals(arr1[i], arr2[i]);
		}
	}
}