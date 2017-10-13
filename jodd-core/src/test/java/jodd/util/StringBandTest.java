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

package jodd.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringBandTest {

	@Test
	public void testSbands() {
		StringBand sb = new StringBand(5);

		assertEquals("", sb.toString());

		assertEquals(5, sb.capacity());
		assertEquals(0, sb.index());
		assertEquals(0, sb.length());

		sb.append("xxx");
		assertEquals(5, sb.capacity());
		assertEquals(1, sb.index());
		assertEquals(3, sb.length());
		assertEquals('x', sb.charAt(0));
		assertEquals('x', sb.charAt(1));
		assertEquals('x', sb.charAt(2));

		sb.append("zzz");
		assertEquals(5, sb.capacity());
		assertEquals(2, sb.index());
		assertEquals(6, sb.length());

		assertEquals("xxxzzz", sb.toString());
		assertEquals("zzz", sb.stringAt(1));
		assertEquals('x', sb.charAt(0));
		assertEquals('x', sb.charAt(1));
		assertEquals('x', sb.charAt(2));
		assertEquals('z', sb.charAt(3));
		assertEquals('z', sb.charAt(4));
		assertEquals('z', sb.charAt(5));

		sb.append("www");
		assertEquals(5, sb.capacity());
		assertEquals(3, sb.index());
		assertEquals(9, sb.length());

		assertEquals("xxxzzzwww", sb.toString());
		assertEquals("www", sb.stringAt(2));
		assertEquals('x', sb.charAt(2));
		assertEquals('z', sb.charAt(3));
		assertEquals('z', sb.charAt(5));
		assertEquals('w', sb.charAt(6));
		assertEquals('w', sb.charAt(8));

		sb.setIndex(1);

		assertEquals(5, sb.capacity());
		assertEquals(1, sb.index());
		assertEquals(3, sb.length());

		assertEquals("xxx", sb.toString());
		assertEquals('x', sb.charAt(2));

	}
}
