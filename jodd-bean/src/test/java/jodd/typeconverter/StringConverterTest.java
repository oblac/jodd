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

import static jodd.typeconverter.TypeConverterTestHelper.arrb;
import static jodd.typeconverter.TypeConverterTestHelper.arrc;
import static jodd.typeconverter.TypeConverterTestHelper.arrd;
import static jodd.typeconverter.TypeConverterTestHelper.arrf;
import static jodd.typeconverter.TypeConverterTestHelper.arri;
import static jodd.typeconverter.TypeConverterTestHelper.arrl;
import static jodd.typeconverter.TypeConverterTestHelper.arrs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.sql.Clob;
import java.sql.SQLException;

import jodd.typeconverter.impl.StringConverter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class StringConverterTest {

	@Test
	void testStringConverter() throws SQLException {
		StringConverter stringConverter = new StringConverter();

		assertNull(stringConverter.convert(null));

		assertEquals("123", stringConverter.convert("123"));
		assertEquals("65,66", stringConverter.convert(arrb(65, 66)));
		assertEquals("Ab", stringConverter.convert(arrc('A', 'b')));
		assertEquals("One,two", stringConverter.convert(arrs("One", "two")));
		assertEquals("123", stringConverter.convert(123));
		assertEquals("java.lang.String", stringConverter.convert(String.class));
		assertEquals("123,456", stringConverter.convert(arri(123,456)));
		assertEquals("123,456", stringConverter.convert(arrl(123L,456L)));
		assertEquals("777777.6,-32321.7", stringConverter.convert(arrf(777777.6f, -32321.7F)));
		assertEquals("777777.6676732,-32321.700985", stringConverter.convert(arrd(777777.6676732D, -32321.700985D)));
		assertEquals("12,-66", stringConverter.convert(arrs(12,-66)));
		assertEquals("true,false,true", stringConverter.convert(arrl(true,false,true)));
		{
			// Clob via Mock
			final Clob mock = Mockito.mock(Clob.class);
			Mockito.when(mock.length()).thenReturn(123456789L);
			Mockito.when(mock.getSubString(Mockito.eq(1L), Mockito.eq(123456789))).thenReturn("Hello there :-)");
			assertEquals("Hello there :-)", stringConverter.convert(mock));
		}
	}
}
