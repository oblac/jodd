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
import static jodd.typeconverter.TypeConverterTestHelper.arro;
import static jodd.typeconverter.TypeConverterTestHelper.arrs;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import jodd.typeconverter.impl.StringArrayConverter;
import org.junit.jupiter.api.Test;

class StringArrayConverterTest {
	
	private TypeConverterManager typeConverterManager = TypeConverterManager.get();

	@Test
	void testConversion() {
		StringArrayConverter stringArrayConverter = (StringArrayConverter) typeConverterManager.lookup(String[].class);

		assertNull(stringArrayConverter.convert(null));

		assertArrayEquals(arrs(Double.class.getName()), stringArrayConverter.convert(Double.class));
		assertArrayEquals(arrs("173"), stringArrayConverter.convert("173"));
		assertArrayEquals(arrs("173", "1022"), stringArrayConverter.convert("173,1022"));
		assertArrayEquals(arrs("173", " 1022"), stringArrayConverter.convert("173, 1022"));
		assertArrayEquals(arrs("173", "1022"), stringArrayConverter.convert(arrs("173", "1022")));
		assertArrayEquals(arrs("1", "7", "3"), stringArrayConverter.convert(arri(1, 7, 3)));
		assertArrayEquals(arrs("1", "7", "3"), stringArrayConverter.convert(arrl(1, 7, 3)));
		assertArrayEquals(arrs("1.0", "7.0", "3.0"), stringArrayConverter.convert(arrd(1, 7, 3)));
		assertArrayEquals(arrs("1.0", "7.0", "3.0"), stringArrayConverter.convert(arrf(1, 7, 3)));
		assertArrayEquals(arrs("173", "true"), stringArrayConverter.convert(arro("173", Boolean.TRUE)));
		assertArrayEquals(arrs("173", "java.lang.String"), stringArrayConverter.convert(arro("173", String.class)));
		assertArrayEquals(arrs("173.0", "654.7834"), stringArrayConverter.convert(arrd(173D, 654.7834D)));
		assertArrayEquals(arrs("72", "-21"), stringArrayConverter.convert(arrs(72, -21)));
		assertArrayEquals(arrs("72", "-21"), stringArrayConverter.convert(arrb(72, -21)));
		assertArrayEquals(arrs("J","O","D","D"), stringArrayConverter.convert(arrc('J', 'O', 'D', 'D')));
		assertArrayEquals(arrs("true","true","false","false"), stringArrayConverter.convert(arrl(true,true,false,false)));
	}
}
