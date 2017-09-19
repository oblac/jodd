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

import jodd.typeconverter.impl.ByteArrayConverter;
import org.junit.jupiter.api.Test;

import static jodd.typeconverter.TypeConverterTestHelper.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ByteArrayConverterTest {

	ByteArrayConverter byteArrayConverter = (ByteArrayConverter) TypeConverterManager.lookup(byte[].class);

	@Test
	public void testArrayConversion() {
		assertNull(byteArrayConverter.convert(null));

		assertEq(arrb(1, 7, 3), byteArrayConverter.convert(arrb(1, 7, 3)));
		assertEq(arrb(1, 7, 3), byteArrayConverter.convert(arrs(1, 7, 3)));
		assertEq(arrb(1, 0, 1), byteArrayConverter.convert(arrl(true, false, true)));
		assertEq(arrb(1, 7, 3), byteArrayConverter.convert(arri(1, 7, 3)));
		assertEq(arrb(1, 7, 3), byteArrayConverter.convert(arrl(1, 7, 3)));
		assertEq(arrb(1, 7, 3), byteArrayConverter.convert(arrf(1.99f, 7.99f, 3.22f)));
		assertEq(arrb(1, 7, 3), byteArrayConverter.convert(arrd(1.99, 7.99, 3.22)));
		assertEq(arrb(1, 7, 3), byteArrayConverter.convert(arrs("1", "7", "3")));
		assertEq(arrb(1, 7, 3), byteArrayConverter.convert(arrs(" 1 ", " 7 ", " 3 ")));
		assertEq(arrb(1, 7, 3), byteArrayConverter.convert(" 1 ;  7 ;  3 "));
	}

	@Test
	public void testNonArrayConversion() {
		assertEq(arrb(7), byteArrayConverter.convert(Byte.valueOf((byte) 7)));
		assertEq(arrb(7), byteArrayConverter.convert(Integer.valueOf(7)));
		assertEq(arrb(7), byteArrayConverter.convert("7"));
	}

	private void assertEq(byte[] arr1, byte[] arr2) {
		assertEquals(arr1.length, arr2.length);
		for (int i = 0; i < arr1.length; i++) {
			assertEquals(arr1[i], arr2[i]);
		}
	}

}
