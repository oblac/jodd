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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.reflect.Whitebox.getInternalState;
import static org.powermock.reflect.Whitebox.setInternalState;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;

public class DecoraManagerTest {

	private DecoraManager decoraManager;

	@Before
	public void setUp() {
		decoraManager = new DecoraManager();
	}

	@Test
	public final void testIsDecorateErrorPages() {
		// when
		setInternalState(decoraManager, "decorateErrorPages", true);

		// then
		assertTrue("DecorateErrorPages should be true.", decoraManager.isDecorateErrorPages());
	}

	@Test
	public final void testSetDecorateErrorPages() {
		// when
		decoraManager.setDecorateErrorPages(true);

		// then
		assertTrue("DecorateErrorPages should be true.", (boolean) getInternalState(decoraManager, "decorateErrorPages"));
	}

	@Test
	public final void testDecorateRequest() {
		// when
		HttpServletRequest httpServletRequestMock = mock(HttpServletRequest.class);

		// then
		assertTrue("DecorateRequest function always returns true.", decoraManager.decorateRequest(httpServletRequestMock));
	}

	@Test
	public final void testDecorateContentType() {
		// when
		String testString = "TEST";

		// then
		assertTrue("DecorateContentType function always returns true.", decoraManager.decorateContentType(testString, testString, testString));
	}

	@Test
	public final void testDecorateStatusCode() {
		// when
		int statusCode = 200;

		// then
		assertTrue("Returns <code>true</code> for status code 200", decoraManager.decorateStatusCode(statusCode));
	}

	@Test
	public final void testDecorateStatusCode2() {
		// when
		int statusCode = 300;
		setInternalState(decoraManager, "decorateErrorPages", true);

		// then
		assertFalse("Returns <code>false</code> for status code 300", decoraManager.decorateStatusCode(statusCode));
	}

	@Test
	public final void testDecorateStatusCode3() {
		// when
		int statusCode = 404;
		setInternalState(decoraManager, "decorateErrorPages", false);

		// then
		assertFalse("Returns <code>false</code> for status code 404", decoraManager.decorateStatusCode(statusCode));
	}

	@Test
	public final void testDecorateStatusCode4() {
		// when
		int statusCode = 404;
		setInternalState(decoraManager, "decorateErrorPages", true);

		// then
		assertTrue("For error pages (status code {@literal >=} 400) should return true", decoraManager.decorateStatusCode(statusCode));
	}

	@Test
	public final void testResolveDecoratorNull() {
		// setup
		HttpServletRequest httpServletRequestMock = mock(HttpServletRequest.class);
		String actionPath = "TEST";

		// when
		String result = decoraManager.resolveDecorator(httpServletRequestMock, actionPath);

		// then
		assertNull("If decorator is not found, returns <code>null</code>.", result);
	}

	@Test
	public final void testResolveDecoratorNotNull() {
		// setup
		HttpServletRequest httpServletRequestMock = mock(HttpServletRequest.class);
		String actionPath = "TEST.html";

		// when
		String result = decoraManager.resolveDecorator(httpServletRequestMock, actionPath);

		// then
		assertEquals("Result value must be equal to default decorator path.", DecoraManager.DEFAULT_DECORATOR, result);
	}

}
