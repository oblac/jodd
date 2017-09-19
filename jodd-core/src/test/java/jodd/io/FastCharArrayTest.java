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

package jodd.io;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FastCharArrayTest {

	@Test
	public void testFcat() throws IOException {
		FastCharArrayWriter fcaw = new FastCharArrayWriter();

		fcaw.write(65);
		fcaw.write(new char[]{'a', 'z', 'r'});
		fcaw.write(new char[]{'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l'}, 4, 3);

		char[] result = fcaw.toCharArray();
		char[] expected = new char[]{'A', 'a', 'z', 'r', 'g', 'h', 'j'};

		assertTrue(Arrays.equals(expected, result));
	}

	@Test
	public void testFcatSingle() throws IOException {
		FastCharArrayWriter fcaw = new FastCharArrayWriter();

		fcaw.write(73);
		fcaw.write(74);
		fcaw.write(75);
		fcaw.write(76);
		fcaw.write(77);

		char[] result = fcaw.toCharArray();
		char[] expected = new char[]{73, 74, 75, 76, 77};

		assertTrue(Arrays.equals(expected, result));
	}

	@Test
	public void testWriteTo() throws IOException {
		FastCharArrayWriter fcaw = new FastCharArrayWriter(2);
		fcaw.write("Hello");
		fcaw.write(' ');
		fcaw.write("World");
		fcaw.write('!');

		StringWriter sw = new StringWriter();
		fcaw.writeTo(sw);

		assertEquals("Hello World!", sw.toString());
	}

}
