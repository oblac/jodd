// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.filter;

import org.junit.Test;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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