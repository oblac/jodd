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

import jodd.mutable.MutableLong;
import jodd.typeconverter.impl.MutableLongConverter;
import org.junit.Test;

import static org.junit.Assert.*;

public class MutableLongConverterTest {

	@Test
	public void testConversion() {
		MutableLongConverter mutableLongConverter = (MutableLongConverter) TypeConverterManager.lookup(MutableLong.class);

		assertNull(mutableLongConverter.convert(null));

		assertEquals(new MutableLong(173), mutableLongConverter.convert(new MutableLong(173)));
		assertEquals(new MutableLong(173), mutableLongConverter.convert(Integer.valueOf(173)));
		assertEquals(new MutableLong(173), mutableLongConverter.convert(Long.valueOf(173)));
		assertEquals(new MutableLong(173), mutableLongConverter.convert(Short.valueOf((short) 173)));
		assertEquals(new MutableLong(173), mutableLongConverter.convert(Double.valueOf(173.0D)));
		assertEquals(new MutableLong(173), mutableLongConverter.convert(Float.valueOf(173.0F)));
		assertEquals(new MutableLong(173), mutableLongConverter.convert("173"));
		assertEquals(new MutableLong(173), mutableLongConverter.convert(" 173 "));

		try {
			mutableLongConverter.convert("a");
			fail();
		} catch (TypeConversionException ignore) {
		}
	}
}

