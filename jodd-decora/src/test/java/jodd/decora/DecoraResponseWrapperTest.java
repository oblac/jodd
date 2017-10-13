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

import jodd.servlet.wrapper.LastModifiedData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DecoraResponseWrapperTest {

	private DecoraResponseWrapper decoraResponseWrapper;
	private HttpServletRequest originalRequest;
	private HttpServletResponse originalResponse;
	private LastModifiedData lastModifiedData;
	private DecoraManager decoraManager;

	@BeforeEach
	public void setUp() {
		originalRequest = mock(HttpServletRequest.class);
		originalResponse = mock(HttpServletResponse.class);
		lastModifiedData = mock(LastModifiedData.class);
		decoraManager = mock(DecoraManager.class);
	}

	@Test
	public final void testConstructor() {
		// when
		decoraResponseWrapper = new DecoraResponseWrapper(originalRequest, originalResponse, lastModifiedData, decoraManager);

		// then
		assertEquals(originalRequest, decoraResponseWrapper.request);
		assertEquals(originalResponse, decoraResponseWrapper.response);
		assertEquals(lastModifiedData, decoraResponseWrapper.getLastModifiedData());
		assertEquals(decoraManager, decoraResponseWrapper.decoraManager);
	}

	@Test
	public final void testPreResponseCommit() {
		// setup
		decoraResponseWrapper = new DecoraResponseWrapper(originalRequest, originalResponse, lastModifiedData, decoraManager);
		when(lastModifiedData.getLastModified()).thenReturn(Long.valueOf(1));
		when(originalResponse.containsHeader("Last-Modified")).thenReturn(true);

		// when
		decoraResponseWrapper.preResponseCommit();

		// then
		verify(originalResponse, never()).setDateHeader("Last-Modified", 1);
		verify(originalResponse, never()).reset();
		verify(originalResponse).containsHeader("Last-Modified");
	}

	@Test
	public final void testPreResponseCommit2() {
		// setup
		decoraResponseWrapper = new DecoraResponseWrapper(originalRequest, originalResponse, lastModifiedData, decoraManager);
		when(lastModifiedData.getLastModified()).thenReturn(Long.valueOf(-1));
		when(originalResponse.containsHeader("Last-Modified")).thenReturn(true);

		// when
		decoraResponseWrapper.preResponseCommit();

		// then
		verify(originalResponse, never()).setDateHeader("Last-Modified", 1);
		verify(originalResponse, never()).reset();
		verify(lastModifiedData).getLastModified();
	}

	@Test
	public final void testPreResponseCommit3() {
		// setup
		decoraResponseWrapper = new DecoraResponseWrapper(originalRequest, originalResponse, lastModifiedData, decoraManager);
		when(lastModifiedData.getLastModified()).thenReturn(Long.valueOf(-1));
		when(originalResponse.containsHeader("Last-Modified")).thenReturn(false);

		// when
		decoraResponseWrapper.preResponseCommit();

		// then
		verify(originalResponse, never()).setDateHeader("Last-Modified", 1);
		verify(originalResponse, never()).reset();
		verify(lastModifiedData).getLastModified();
	}

	@Test
	public final void testPreResponseCommit4() {
		// setup
		decoraResponseWrapper = new DecoraResponseWrapper(originalRequest, originalResponse, lastModifiedData, decoraManager);
		when(lastModifiedData.getLastModified()).thenReturn(Long.valueOf(1));
		when(originalResponse.containsHeader("Last-Modified")).thenReturn(false);
		when(originalRequest.getDateHeader("If-Modified-Since")).thenReturn(Long.MIN_VALUE);

		// when
		decoraResponseWrapper.preResponseCommit();

		// then
		verify(originalResponse).setDateHeader("Last-Modified", lastModifiedData.getLastModified());
	}

	@Test
	public final void testPreResponseCommit5() {
		// setup
		decoraResponseWrapper = new DecoraResponseWrapper(originalRequest, originalResponse, lastModifiedData, decoraManager);
		when(lastModifiedData.getLastModified()).thenReturn(Long.valueOf(1));
		when(originalResponse.containsHeader("Last-Modified")).thenReturn(false);
		when(originalRequest.getDateHeader("If-Modified-Since")).thenReturn(Long.MAX_VALUE);

		// when
		decoraResponseWrapper.preResponseCommit();

		// then
		verify(originalResponse).reset();
	}

	@Test
	public final void testBufferContentType() {
		// setup
		decoraResponseWrapper = new DecoraResponseWrapper(originalRequest, originalResponse, lastModifiedData, decoraManager);
		String testString = "TEST";

		// when
		decoraResponseWrapper.bufferContentType(testString, testString, testString);

		// then
		verify(decoraManager).decorateContentType(testString, testString, testString);
	}

	@Test
	public final void testBufferStatusCode() {
		// setup
		decoraResponseWrapper = new DecoraResponseWrapper(originalRequest, originalResponse, lastModifiedData, decoraManager);
		int statusCode = 1;

		// when
		decoraResponseWrapper.bufferStatusCode(statusCode);

		// then
		verify(decoraManager).decorateStatusCode(statusCode);
	}

}
