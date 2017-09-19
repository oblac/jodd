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

import jodd.typeconverter.impl.BooleanArrayConverter;
import org.junit.jupiter.api.Test;

import static jodd.typeconverter.TypeConverterTestHelper.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BooleanArrayConverterTest {

	@Test
	public void testConversion() {
		BooleanArrayConverter booleanArrayConverter = (BooleanArrayConverter) TypeConverterManager.lookup(boolean[].class);

		assertNull(booleanArrayConverter.convert(null));

		boolean[] primitiveArray = new boolean[]{false, true, false};
		Object convertedArray = booleanArrayConverter.convert(primitiveArray);
		assertEquals(boolean[].class, convertedArray.getClass());

		Boolean[] booleanArray = new Boolean[]{Boolean.FALSE, Boolean.TRUE, Boolean.FALSE};
		convertedArray = booleanArrayConverter.convert(booleanArray);
		assertEquals(boolean[].class, convertedArray.getClass());    // boolean[]!

		assertEq(arrl(true), booleanArrayConverter.convert(Boolean.TRUE));
		assertEq(arrl(true), booleanArrayConverter.convert("true"));
		assertEq(arrl(true, false, true), booleanArrayConverter.convert(arrl(true, false, true)));
		assertEq(arrl(true, false, true), booleanArrayConverter.convert(arri(-7, 0, 3)));
		assertEq(arrl(true, false, true), booleanArrayConverter.convert(arrf(-7.0f, 0.0f, 3.0f)));
		assertEq(arrl(true, false, true), booleanArrayConverter.convert(arrs("true", "0", "yes")));
		assertEq(arrl(true, false, true), booleanArrayConverter.convert(arrs(" true ", "0", " yes ")));
		assertEq(arrl(true, false, true), booleanArrayConverter.convert(" true , 0,  yes "));
	}

	@Test
	public void testArrayConversion() {
		Object[] booleanArray = new Object[]{Boolean.FALSE, "TRUE", Integer.valueOf(0)};

		boolean[] arr1 = TypeConverterManager.convertType(booleanArray, boolean[].class);
		assertEquals(3, arr1.length);
		assertEq(arrl(false, true, false), arr1);

		Boolean[] arr2 = TypeConverterManager.convertType(booleanArray, Boolean[].class);
		assertEquals(3, arr2.length);
		assertEq(arrl(false, true, false), arr2);
	}


	private void assertEq(boolean[] arr1, boolean[] arr2) {
		assertEquals(arr1.length, arr2.length);
		for (int i = 0; i < arr1.length; i++) {
			assertEquals(arr1[i], arr2[i]);
		}
	}

	private void assertEq(boolean[] arr1, Boolean[] arr2) {
		assertEquals(arr1.length, arr2.length);
		for (int i = 0; i < arr1.length; i++) {
			assertEquals(arr1[i], arr2[i]);
		}
	}

}
