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

import jodd.mutable.MutableInteger;
import jodd.typeconverter.impl.ClassConverter;
import jodd.util.fixtures.testdata.A;
import jodd.util.fixtures.testdata.B;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClassConverterTest {

	@Test
	public void testConversion() {
		ClassConverter classConverter = new ClassConverter();

		assertNull(classConverter.convert(null));

		assertEquals(String.class, classConverter.convert(String.class));
		assertEquals(Integer.class, classConverter.convert("java.lang.Integer"));

		try {
			classConverter.convert("foo.Klass");
			fail("error");
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

