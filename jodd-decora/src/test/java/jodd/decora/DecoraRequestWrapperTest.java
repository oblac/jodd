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

package jodd.decora;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DecoraRequestWrapperTest {

	private HttpServletRequest httpServletRequestMock;
	private final String TEST_STRING = "TEST";

	@BeforeEach
	public void setUp() {
		httpServletRequestMock = mock(HttpServletRequest.class);
	}

	@Test
	public final void testConstructor() {
		// when
		DecoraRequestWrapper decoraRequestWrapper = new DecoraRequestWrapper(httpServletRequestMock);

		// then
		assertEquals(httpServletRequestMock, decoraRequestWrapper.getRequest());
	}

	@Test
	public final void testGetHeaderString() {
		// setup
		DecoraRequestWrapper decoraRequestWrapper = new DecoraRequestWrapper(httpServletRequestMock);

		// when
		decoraRequestWrapper.getHeader(TEST_STRING);

		// then
		verify(httpServletRequestMock).getHeader(TEST_STRING);
	}

	@Test
	public final void testGetHeaderStringReturnNull() {
		// setup
		DecoraRequestWrapper decoraRequestWrapper = new DecoraRequestWrapper(httpServletRequestMock);
		String nullRespondingString = "If-Modified-Since";

		// when
		String result = decoraRequestWrapper.getHeader(nullRespondingString);

		// then
		assertNull(result);
	}

	@Test
	public final void testGetDateHeaderString() {
		// setup
		DecoraRequestWrapper decoraRequestWrapper = new DecoraRequestWrapper(httpServletRequestMock);

		// when
		decoraRequestWrapper.getDateHeader(TEST_STRING);

		// then
		verify(httpServletRequestMock).getDateHeader(TEST_STRING);
	}

	@Test
	public final void testGetDateHeaderStringReturnMinusOne() {
		// setup
		DecoraRequestWrapper decoraRequestWrapper = new DecoraRequestWrapper(httpServletRequestMock);
		String nullRespondingString = "If-Modified-Since";

		// when
		long result = decoraRequestWrapper.getDateHeader(nullRespondingString);

		// then
		assertEquals(-1, result);
	}

}
