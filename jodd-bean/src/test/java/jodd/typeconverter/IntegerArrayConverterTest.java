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

import jodd.typeconverter.impl.IntegerArrayConverter;
import org.junit.jupiter.api.Test;

import static jodd.typeconverter.TypeConverterTestHelper.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class IntegerArrayConverterTest {

	@Test
	public void testConversion() {
		IntegerArrayConverter integerArrayConverter = (IntegerArrayConverter) TypeConverterManager.lookup(int[].class);

		assertNull(integerArrayConverter.convert(null));

		assertEq(arri(173, 234), integerArrayConverter.convert("173, 234"));
		assertEq(arri(173), integerArrayConverter.convert(Double.valueOf(173)));
		assertEq(arri(1, 7, 3), integerArrayConverter.convert(arri(1, 7, 3)));
		assertEq(arri(1, 7, 3), integerArrayConverter.convert(arrl(1, 7, 3)));
		assertEq(arri(1, 7, 3), integerArrayConverter.convert(arrf(1, 7, 3)));
		assertEq(arri(1, 7, 3), integerArrayConverter.convert(arrd(1.1, 7.99, 3)));
		assertEq(arri(173, 1022), integerArrayConverter.convert(arrs("173", "1022")));
		assertEq(arri(173, 10), integerArrayConverter.convert(arro("173", Integer.valueOf(10))));

		assertEq(arri(111, 777, 333), integerArrayConverter.convert(arrs("111", "   777     ", "333")));
		assertEq(arri(111, 777, 333), integerArrayConverter.convert("111,  777,  333"));

	}

	private void assertEq(int[] arr1, int[] arr2) {
		assertEquals(arr1.length, arr2.length);
		for (int i = 0; i < arr1.length; i++) {
			assertEquals(arr1[i], arr2[i]);
		}
	}

}

