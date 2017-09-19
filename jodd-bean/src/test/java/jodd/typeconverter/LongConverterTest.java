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

import jodd.typeconverter.impl.LongConverter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LongConverterTest {

	@Test
	public void testConversion() {
		LongConverter longConverter = new LongConverter();

		assertNull(longConverter.convert(null));

		assertEquals(Long.valueOf(173), longConverter.convert(Long.valueOf(173)));

		assertEquals(Long.valueOf(173), longConverter.convert(Integer.valueOf(173)));
		assertEquals(Long.valueOf(173), longConverter.convert(Short.valueOf((short) 173)));
		assertEquals(Long.valueOf(173), longConverter.convert(Double.valueOf(173.0D)));
		assertEquals(Long.valueOf(173), longConverter.convert(Float.valueOf(173.0F)));
		assertEquals(Long.valueOf(173), longConverter.convert("173"));
		assertEquals(Long.valueOf(173), longConverter.convert(" 173 "));

		assertEquals(Long.valueOf(-1), longConverter.convert(" -1 "));
		assertEquals(Long.valueOf(1), longConverter.convert(" +1 "));
		assertEquals(Long.valueOf(9223372036854775807L), longConverter.convert(" +9223372036854775807 "));
		assertEquals(Long.valueOf(-9223372036854775808L), longConverter.convert(" -9223372036854775808 "));

		try {
			longConverter.convert("9223372036854775808");
			fail("error");
		} catch (TypeConversionException ignore) {
		}
		try {
			longConverter.convert("-9223372036854775809");
			fail("error");
		} catch (TypeConversionException ignore) {
		}
		try {
			longConverter.convert("a");
			fail("error");
		} catch (TypeConversionException ignore) {
		}
	}

}
