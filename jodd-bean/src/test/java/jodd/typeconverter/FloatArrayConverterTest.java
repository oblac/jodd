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

import jodd.typeconverter.impl.FloatArrayConverter;
import org.junit.jupiter.api.Test;

import static jodd.typeconverter.TypeConverterTestHelper.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class FloatArrayConverterTest {

	@Test
	public void testConversion() {
		FloatArrayConverter floatArrayConverter = (FloatArrayConverter) TypeConverterManager.lookup(float[].class);

		assertNull(floatArrayConverter.convert(null));

		assertEq(arrf((float) 1.73), floatArrayConverter.convert(Float.valueOf((float) 1.73)));
		assertEq(arrf((float) 1.73, (float) 10.22), floatArrayConverter.convert(arrf((float) 1.73, (float) 10.22)));
		assertEq(arrf((float) 1.73, (float) 10.22), floatArrayConverter.convert(arrd(1.73, 10.22)));
		assertEq(arrf((float) 1.73, (float) 10.22), floatArrayConverter.convert(arrf(1.73f, 10.22f)));
		assertEq(arrf((float) 1.0, (float) 7.0, (float) 3.0), floatArrayConverter.convert(arri(1, 7, 3)));
		assertEq(arrf((float) 1.0, (float) 7.0, (float) 3.0), floatArrayConverter.convert(arrl(1, 7, 3)));
		assertEq(arrf((float) 1.0, (float) 7.0, (float) 3.0), floatArrayConverter.convert(arrb(1, 7, 3)));
		assertEq(arrf((float) 1.0, (float) 7.0, (float) 3.0), floatArrayConverter.convert(arrs(1, 7, 3)));
		assertEq(arrf((float) 1.73, (float) 10.22), floatArrayConverter.convert(arrs("1.73", "10.22")));
		assertEq(arrf((float) 1.73, (float) 10.22), floatArrayConverter.convert(arrs(" 1.73 ", " 10.22 ")));
		assertEq(arrf((float) 1.73, 10), floatArrayConverter.convert(arro("1.73", 10)));
		assertEq(arrf((float) 1.73, 10), floatArrayConverter.convert("1.73 \n 10"));
	}

	private void assertEq(float[] arr1, float[] arr2) {
		assertEquals(arr1.length, arr2.length);
		for (int i = 0; i < arr1.length; i++) {
			assertEquals(arr1[i], arr2[i], 0.0001);
		}
	}

}


