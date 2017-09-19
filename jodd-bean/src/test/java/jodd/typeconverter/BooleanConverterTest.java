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

import jodd.typeconverter.impl.BooleanConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BooleanConverterTest {

	private static BooleanConverter booleanConverter;

	@BeforeEach
	public void setUp(){
		booleanConverter = new BooleanConverter();
	}


	@Test
	public void testConversion() {
		assertNull(booleanConverter.convert(null));

		assertEquals(Boolean.TRUE, booleanConverter.convert(Boolean.TRUE));

		assertEquals(Boolean.TRUE, booleanConverter.convert("yes"));
		assertEquals(Boolean.TRUE, booleanConverter.convert(" yes "));
		assertEquals(Boolean.TRUE, booleanConverter.convert("YES"));
		assertEquals(Boolean.TRUE, booleanConverter.convert("y"));
		assertEquals(Boolean.TRUE, booleanConverter.convert("Y"));
		assertEquals(Boolean.TRUE, booleanConverter.convert("on"));
		assertEquals(Boolean.TRUE, booleanConverter.convert("ON"));
		assertEquals(Boolean.TRUE, booleanConverter.convert("1"));

		assertEquals(Boolean.FALSE, booleanConverter.convert("no"));
		assertEquals(Boolean.FALSE, booleanConverter.convert(" no "));
		assertEquals(Boolean.FALSE, booleanConverter.convert("NO"));
		assertEquals(Boolean.FALSE, booleanConverter.convert("n"));
		assertEquals(Boolean.FALSE, booleanConverter.convert("N"));
		assertEquals(Boolean.FALSE, booleanConverter.convert("off"));
		assertEquals(Boolean.FALSE, booleanConverter.convert("OFF"));
		assertEquals(Boolean.FALSE, booleanConverter.convert("0"));
		assertEquals(Boolean.FALSE, booleanConverter.convert(""));
	}

	@Test
	public void testConversionWithBlankInput() {
		assertThrows(TypeConversionException.class, () -> booleanConverter.convert("    "));
	}

	@Test
	public void testConversionWithUnrecognizedInput() {
		assertThrows(TypeConversionException.class, () -> booleanConverter.convert("asd#%^&(412"));
	}

}

