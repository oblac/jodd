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

package jodd.servlet.filter;

import org.junit.jupiter.api.Test;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CharArrayResponseWrapperTest {

	@Test
	public void testWriter() {
		HttpServletResponse response = mock(HttpServletResponse.class);

		CharArrayResponseWrapper wrappedResponse = new CharArrayResponseWrapper(response);

		PrintWriter printWriter = wrappedResponse.getWriter();
		printWriter.write("One");
		printWriter.write("Two");
		printWriter.flush();

		char[] chars = wrappedResponse.toCharArray();
		String string = new String(chars);
		assertEquals("OneTwo", string);

		printWriter.write("123");
		printWriter.flush();

		chars = wrappedResponse.toCharArray();
		string = new String(chars);
		assertEquals("OneTwo123", string);
	}

	@Test
	public void testBytes() throws IOException {
		HttpServletResponseWrapper rw = mock(HttpServletResponseWrapper.class);
		ServletOutputStream os = mock(ServletOutputStream.class);
		when(rw.getOutputStream()).thenReturn(os);

		CharArrayResponseWrapper wrappedResponse = new CharArrayResponseWrapper(rw);
		try {
			wrappedResponse.getOutputStream();
			fail("error");
		} catch (IOException ignore) {
		}

		//sos.write(new byte[]{123, 123});
		//char[] chars = wrappedResponse.toCharArray();
		//assertNull(chars);
	}

}
