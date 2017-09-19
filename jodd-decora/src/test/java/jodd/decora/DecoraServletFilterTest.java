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

import jodd.decora.parser.DecoraParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

public class DecoraServletFilterTest {

	private DecoraServletFilter decoraServletFilter;

	@BeforeEach
	public void setUp() {
		decoraServletFilter = new DecoraServletFilter();
	}

	@Test
	public final void testCreateDecoraManager() {
		// when
		DecoraManager decoraManager = decoraServletFilter.createDecoraManager();

		// then
		assertNotNull(decoraManager);
	}

	@Test
	public final void testCreateDecoraParser() {
		// when
		DecoraParser decoraParser = decoraServletFilter.createDecoraParser();

		// then
		assertNotNull(decoraParser);
	}

	@Test
	public final void testDestroy() {
		// when
		decoraServletFilter.destroy();

		// then
		// Function body is empty
		// No checking needed
	}

	@Test
	public final void testWrapRequest() {
		// setup
		HttpServletRequest httpServletRequestMock = mock(HttpServletRequest.class);

		// when
		HttpServletRequest httpServletRequest = decoraServletFilter.wrapRequest(httpServletRequestMock);

		// then
		assertNotNull(httpServletRequest);
	}

}
