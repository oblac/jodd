// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.typeconverter;

import org.junit.jupiter.api.Test;

import static jodd.typeconverter.TypeConverterTestHelper.arrc;
import static org.junit.jupiter.api.Assertions.*;

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
			fail("error");
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
