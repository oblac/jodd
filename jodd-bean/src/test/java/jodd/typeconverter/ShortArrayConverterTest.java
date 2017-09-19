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

import jodd.typeconverter.impl.ShortArrayConverter;
import org.junit.jupiter.api.Test;

import static jodd.typeconverter.TypeConverterTestHelper.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ShortArrayConverterTest {

	@Test
	public void testConversion() {
		ShortArrayConverter shortArrayConverter = (ShortArrayConverter) TypeConverterManager.lookup(short[].class);

		assertNull(shortArrayConverter.convert(null));

		assertEq(arrs(1), shortArrayConverter.convert(Double.valueOf(1)));
		assertEq(arrs(1, 7, 3), shortArrayConverter.convert(arrs(1, 7, 3)));
		assertEq(arrs(1, 7, 3), shortArrayConverter.convert(arrb(1, 7, 3)));
		assertEq(arrs(1, 7, 3), shortArrayConverter.convert(arri(1, 7, 3)));
		assertEq(arrs(173, 1022), shortArrayConverter.convert(arrs("173", "1022")));
		assertEq(arrs(173, 1022), shortArrayConverter.convert(arrs(" 173 ", " 1022 ")));
		assertEq(arrs(173, 10), shortArrayConverter.convert(arro("173", Integer.valueOf(10))));
		assertEq(arrs(173, 10), shortArrayConverter.convert("173,10"));
	}

	private void assertEq(short[] arr1, short[] arr2) {
		assertEquals(arr1.length, arr2.length);
		for (int i = 0; i < arr1.length; i++) {
			assertEquals(arr1[i], arr2[i]);
		}
	}

}

