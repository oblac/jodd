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

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GzipFilterTest {

	@Test
	public void testEligibleExtensions() {
		GzipFilter gzipFilter = new GzipFilter();
		gzipFilter.requestParameterName = "gzip";
		gzipFilter.extensions = new String[] {"html"};
		gzipFilter.wildcards = false;
		gzipFilter.matches = null;
		gzipFilter.excludes = null;

		HttpServletRequest servletRequest = mock(HttpServletRequest.class);

		when(servletRequest.getRequestURI()).thenReturn("/hello.html");
		assertTrue(gzipFilter.isGzipEligible(servletRequest));

		when(servletRequest.getRequestURI()).thenReturn("/hello.htm");
		assertFalse(gzipFilter.isGzipEligible(servletRequest));
	}

	@Test
	public void testEligibleAll() {
		GzipFilter gzipFilter = new GzipFilter();
		gzipFilter.requestParameterName = "gzip";
		gzipFilter.extensions = null;
		gzipFilter.wildcards = false;
		gzipFilter.matches = null;
		gzipFilter.excludes = null;

		HttpServletRequest servletRequest = mock(HttpServletRequest.class);

		when(servletRequest.getRequestURI()).thenReturn("/hello.html");
		assertTrue(gzipFilter.isGzipEligible(servletRequest));

		when(servletRequest.getRequestURI()).thenReturn("/hello.htm");
		assertTrue(gzipFilter.isGzipEligible(servletRequest));
	}

	@Test
	public void testEligibleWildcardMatches() {
		GzipFilter gzipFilter = new GzipFilter();
		gzipFilter.requestParameterName = "gzip";
		gzipFilter.extensions = new String[] {"html"};
		gzipFilter.wildcards = true;
		gzipFilter.matches = new String[] {"/**/*.html"};
		gzipFilter.excludes = null;

		HttpServletRequest servletRequest = mock(HttpServletRequest.class);

		when(servletRequest.getRequestURI()).thenReturn("/hello.html");
		assertTrue(gzipFilter.isGzipEligible(servletRequest));

		when(servletRequest.getRequestURI()).thenReturn("/foo/hello.html");
		assertTrue(gzipFilter.isGzipEligible(servletRequest));

		when(servletRequest.getRequestURI()).thenReturn("/hello.htm");
		assertFalse(gzipFilter.isGzipEligible(servletRequest));

		when(servletRequest.getRequestURI()).thenReturn("/foo/hello.htm");
		assertFalse(gzipFilter.isGzipEligible(servletRequest));
	}

	@Test
	public void testEligibleWildcardMatchesAll() {
		GzipFilter gzipFilter = new GzipFilter();
		gzipFilter.requestParameterName = "gzip";
		gzipFilter.extensions = new String[] {"html"};
		gzipFilter.wildcards = true;
		gzipFilter.matches = new String[] {"/**/*"};
		gzipFilter.excludes = null;

		HttpServletRequest servletRequest = mock(HttpServletRequest.class);

		when(servletRequest.getRequestURI()).thenReturn("/hello.html");
		assertTrue(gzipFilter.isGzipEligible(servletRequest));

		when(servletRequest.getRequestURI()).thenReturn("/hello.htm");
		assertTrue(gzipFilter.isGzipEligible(servletRequest));

		when(servletRequest.getRequestURI()).thenReturn("/hello/foo/img.jpg");
		assertTrue(gzipFilter.isGzipEligible(servletRequest));
	}

	@Test
	public void testEligibleWildcardMatchesExcludes() {
		GzipFilter gzipFilter = new GzipFilter();
		gzipFilter.requestParameterName = "gzip";
		gzipFilter.extensions = new String[] {"html"};
		gzipFilter.wildcards = true;
		gzipFilter.matches = new String[] {"/**/*"};
		gzipFilter.excludes = new String[] {"/**/*m*"};

		HttpServletRequest servletRequest = mock(HttpServletRequest.class);

		when(servletRequest.getRequestURI()).thenReturn("/hello.html");
		assertFalse(gzipFilter.isGzipEligible(servletRequest));

		when(servletRequest.getRequestURI()).thenReturn("/hello.txt");
		assertTrue(gzipFilter.isGzipEligible(servletRequest));

		when(servletRequest.getRequestURI()).thenReturn("/hello/foo/img.jpg");
		assertFalse(gzipFilter.isGzipEligible(servletRequest));
	}
}
