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

import jodd.typeconverter.impl.LongArrayConverter;
import org.junit.jupiter.api.Test;

import static jodd.typeconverter.TypeConverterTestHelper.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class LongArrayConverterTest {

	@Test
	public void testConversion() {
		LongArrayConverter longArrayConverter = (LongArrayConverter) TypeConverterManager.lookup(long[].class);

		assertNull(longArrayConverter.convert(null));

		assertEq(arrl(173), longArrayConverter.convert(Double.valueOf(173)));
		assertEq(arrl(173, 1022, 29929), longArrayConverter.convert(arrf(173, 1022, 29929)));
		assertEq(arrl(173, 1022, 29929), longArrayConverter.convert(arrd(173, 1022, 29929)));
		assertEq(arrl(173, 1022, 29929), longArrayConverter.convert(arri(173, 1022, 29929)));
		assertEq(arrl(173, 1022, 29929), longArrayConverter.convert(arrl(173, 1022, 29929)));
		assertEq(arrl(173, 1022), longArrayConverter.convert(arrs("173", "1022")));
		assertEq(arrl(173, 1022), longArrayConverter.convert(arro("173", Long.valueOf(1022))));

		assertEq(arrl(111, 777, 333), longArrayConverter.convert(arrs("111", "   777     ", "333")));
		assertEq(arrl(111, 777, 333), longArrayConverter.convert("111,  777,  333"));
	}

	private void assertEq(long[] arr1, long[] arr2) {
		assertEquals(arr1.length, arr2.length);
		for (int i = 0; i < arr1.length; i++) {
			assertEquals(arr1[i], arr2[i]);
		}
	}

}
