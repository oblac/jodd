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

import jodd.typeconverter.impl.ShortConverter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ShortConverterTest {

	@Test
	public void testConversion() {
		ShortConverter shortConverter = new ShortConverter();

		assertNull(shortConverter.convert(null));

		assertEquals(Short.valueOf((short) 1), shortConverter.convert(Short.valueOf((short) 1)));
		assertEquals(Short.valueOf((short) 1), shortConverter.convert(Integer.valueOf(1)));
		assertEquals(Short.valueOf((short) 1), shortConverter.convert(Double.valueOf(1.0D)));
		assertEquals(Short.valueOf((short) 1), shortConverter.convert("1"));
		assertEquals(Short.valueOf((short) 1), shortConverter.convert(" 1 "));

		assertEquals(Short.valueOf((short) 1), shortConverter.convert(" +1 "));
		assertEquals(Short.valueOf((short) -1), shortConverter.convert(" -1 "));
		assertEquals(Short.valueOf((short) 32767), shortConverter.convert(" +32767 "));
		assertEquals(Short.valueOf((short) -32768), shortConverter.convert(" -32768 "));

		try {
			shortConverter.convert("a");
			fail("error");
		} catch (TypeConversionException ignore) {
		}
		try {
			shortConverter.convert("+32768");
			fail("error");
		} catch (TypeConversionException ignore) {
		}
		try {
			shortConverter.convert("-32769");
			fail("error");
		} catch (TypeConversionException ignore) {
		}
	}
}
