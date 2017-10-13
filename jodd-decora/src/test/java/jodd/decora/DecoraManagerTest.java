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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class DecoraManagerTest {

	private DecoraManager decoraManager;

	@BeforeEach
	public void setUp() {
		decoraManager = new DecoraManager();
	}

	@Test
	public final void testIsDecorateErrorPages() {
		// when
		decoraManager.setDecorateErrorPages(true);

		// then
		assertTrue(decoraManager.isDecorateErrorPages());
	}

	@Test
	public final void testDecorateRequest() {
		// when
		HttpServletRequest httpServletRequestMock = mock(HttpServletRequest.class);

		// then
		assertTrue(decoraManager.decorateRequest(httpServletRequestMock));
	}

	@Test
	public final void testDecorateContentType() {
		// when
		String testString = "TEST";

		// then
		assertTrue(decoraManager.decorateContentType(testString, testString, testString));
	}

	@Test
	public final void testDecorateStatusCode() {
		// when
		int statusCode = 200;

		// then
		assertTrue(decoraManager.decorateStatusCode(statusCode));
	}

	@Test
	public final void testDecorateStatusCode2() {
		// when
		int statusCode = 300;
		decoraManager.setDecorateErrorPages(true);

		// then
		assertFalse(decoraManager.decorateStatusCode(statusCode));
	}

	@Test
	public final void testDecorateStatusCode3() {
		// when
		int statusCode = 404;
		decoraManager.setDecorateErrorPages(false);

		// then
		assertFalse(decoraManager.decorateStatusCode(statusCode));
	}

	@Test
	public final void testDecorateStatusCode4() {
		// when
		int statusCode = 404;
		decoraManager.setDecorateErrorPages(true);

		// then
		assertTrue(decoraManager.decorateStatusCode(statusCode));
	}

	@Test
	public final void testResolveDecoratorNull() {
		// setup
		HttpServletRequest httpServletRequestMock = mock(HttpServletRequest.class);
		String actionPath = "TEST";

		// when
		String result = decoraManager.resolveDecorator(httpServletRequestMock, actionPath);

		// then
		assertNull(result);
	}

	@Test
	public final void testResolveDecoratorNotNull() {
		// setup
		HttpServletRequest httpServletRequestMock = mock(HttpServletRequest.class);
		String actionPath = "TEST.html";

		// when
		String result = decoraManager.resolveDecorator(httpServletRequestMock, actionPath);

		// then
		assertEquals(DecoraManager.DEFAULT_DECORATOR, result);
	}

}
