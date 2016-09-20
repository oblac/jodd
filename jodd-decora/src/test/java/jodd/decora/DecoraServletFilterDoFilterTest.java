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

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.powermock.reflect.Whitebox.setInternalState;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import jodd.decora.parser.DecoraParser;
import jodd.util.ClassLoaderUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ClassLoaderUtil.class, DecoraResponseWrapper.class, DecoraServletFilter.class })
public class DecoraServletFilterDoFilterTest {

	private DecoraServletFilter decoraServletFilter;
	private HttpServletRequest httpServletRequestMock;
	private HttpServletResponse httpServletResponseMock;
	private FilterChain filterChainMock;
	private DecoraManager decoraManagerMock;
	private DecoraResponseWrapper decoraResponseWrapperMock;
	private DecoraParser decoraParserMock;
	private PrintWriter printWriterMock;

	@Before
	public void setUp() throws Exception {
		decoraServletFilter = new DecoraServletFilter();
		httpServletRequestMock = mock(HttpServletRequest.class);
		httpServletResponseMock = mock(HttpServletResponse.class);
		filterChainMock = mock(FilterChain.class);
		decoraManagerMock = mock(DecoraManager.class);
		decoraParserMock = mock(DecoraParser.class);
		decoraResponseWrapperMock = mock(DecoraResponseWrapper.class);
		printWriterMock = mock(PrintWriter.class);
		setInternalState(decoraServletFilter, "decoraParser", decoraParserMock);
		setInternalState(decoraServletFilter, "decoraManager", decoraManagerMock);
		when(httpServletResponseMock.getWriter()).thenReturn(printWriterMock);
		when(httpServletResponseMock.getOutputStream()).thenReturn(mock(ServletOutputStream.class));
		when(decoraResponseWrapperMock.isBufferStreamBased()).thenReturn(true);
		whenNew(DecoraResponseWrapper.class).withAnyArguments().thenReturn(decoraResponseWrapperMock);
	}

	@Test
	public final void testDoFilterDecorateRequestFalse() throws IOException, ServletException {
		// setup
		when(decoraManagerMock.decorateRequest(httpServletRequestMock)).thenReturn(false);

		// when
		decoraServletFilter.doFilter(httpServletRequestMock, httpServletResponseMock, filterChainMock);

		// then
		verify(filterChainMock).doFilter(httpServletRequestMock, httpServletResponseMock);

	}

	@Test
	public final void testDoFilterBufferingEnabledFalse() throws IOException, ServletException {
		// setup
		when(decoraManagerMock.decorateRequest(httpServletRequestMock)).thenReturn(true);
		when(decoraResponseWrapperMock.isBufferingEnabled()).thenReturn(false);

		// when
		decoraServletFilter.doFilter(httpServletRequestMock, httpServletResponseMock, filterChainMock);

		// then
		verify(decoraResponseWrapperMock, never()).getBufferContentAsChars();
		verify(decoraResponseWrapperMock).isBufferingEnabled();
	}

	@Test
	public final void testDoFilterPageContentNull() throws IOException, ServletException {
		// setup
		when(decoraManagerMock.decorateRequest(httpServletRequestMock)).thenReturn(true);
		when(decoraResponseWrapperMock.isBufferingEnabled()).thenReturn(true);
		when(decoraResponseWrapperMock.getBufferContentAsChars()).thenReturn(null);

		// when
		decoraServletFilter.doFilter(httpServletRequestMock, httpServletResponseMock, filterChainMock);

		// then
		verify(decoraResponseWrapperMock, never()).commitResponse();
		verify(decoraResponseWrapperMock).isBufferingEnabled();
	}

	@Test
	public final void testDoFilterPageContentEmpty() throws IOException, ServletException {
		// setup
		when(decoraManagerMock.decorateRequest(httpServletRequestMock)).thenReturn(true);
		when(decoraResponseWrapperMock.isBufferingEnabled()).thenReturn(true);
		when(decoraResponseWrapperMock.getBufferContentAsChars()).thenReturn(new char[] {});

		// when
		decoraServletFilter.doFilter(httpServletRequestMock, httpServletResponseMock, filterChainMock);

		// then
		verify(decoraResponseWrapperMock, never()).commitResponse();
		verify(decoraResponseWrapperMock).isBufferingEnabled();
	}

	@Test
	public final void testDoFilterDecoratorPathNotNull() throws IOException, ServletException {
		// setup
		when(decoraManagerMock.decorateRequest(httpServletRequestMock)).thenReturn(true);
		when(decoraManagerMock.resolveDecorator(httpServletRequestMock, null)).thenReturn("TEST");
		when(decoraResponseWrapperMock.isBufferingEnabled()).thenReturn(true);
		when(decoraResponseWrapperMock.getBufferContentAsChars()).thenReturn("TEST".toCharArray());

		// when
		decoraServletFilter.doFilter(httpServletRequestMock, httpServletResponseMock, filterChainMock);

		// then
		verify(printWriterMock).flush();
	}

	@Test
	public final void testDoFilterDecoratorPathNull() throws IOException, ServletException {
		// setup
		when(decoraManagerMock.decorateRequest(httpServletRequestMock)).thenReturn(true);
		when(decoraManagerMock.resolveDecorator(httpServletRequestMock, null)).thenReturn(null);
		when(decoraResponseWrapperMock.isBufferingEnabled()).thenReturn(true);
		when(decoraResponseWrapperMock.getBufferedChars()).thenReturn("TEST".toCharArray());
		when(decoraResponseWrapperMock.getBufferContentAsChars()).thenReturn("TEST".toCharArray());

		// when
		decoraServletFilter.doFilter(httpServletRequestMock, httpServletResponseMock, filterChainMock);

		// then
		verify(httpServletResponseMock, never()).getWriter();
		verify(decoraResponseWrapperMock).isBufferStreamBased();
	}

	@Test
	public final void testDoFilterBufferStreamBased() throws IOException, ServletException {
		// setup
		when(decoraManagerMock.decorateRequest(httpServletRequestMock)).thenReturn(true);
		when(decoraManagerMock.resolveDecorator(httpServletRequestMock, null)).thenReturn(null);
		when(decoraResponseWrapperMock.getBufferContentAsChars()).thenReturn("TEST".toCharArray());
		when(decoraResponseWrapperMock.isBufferingEnabled()).thenReturn(true);

		// when
		decoraServletFilter.doFilter(httpServletRequestMock, httpServletResponseMock, filterChainMock);

		// then
		verify(httpServletResponseMock).getOutputStream();
	}

}
