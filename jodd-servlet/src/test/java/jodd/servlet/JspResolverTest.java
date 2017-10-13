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

package jodd.servlet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JspResolverTest {

	static class Foo {
		String name = "Hello";
		int[] array = new int[]{1, 7, 3};
	}

	HttpServletRequest servletRequest;
	HttpSession httpSession;
	ServletContext servletContext;


	@BeforeEach
	public void setUp() throws Exception {

		servletRequest = mock(HttpServletRequest.class);
		httpSession = mock(HttpSession.class);
		servletContext = mock(ServletContext.class);

		when(servletRequest.getAttribute("name")).thenReturn("value");
		when(servletRequest.getParameter("name2")).thenReturn("value1");
		when(servletRequest.getParameterValues("name2")).thenReturn(new String[] {"value1"});
		when(servletRequest.getSession()).thenReturn(httpSession);
		when(httpSession.getAttribute("name2")).thenReturn("value2");
		when(httpSession.getServletContext()).thenReturn(servletContext);
		when(servletContext.getAttribute("name2")).thenReturn("value3");
		when(servletContext.getAttribute("name3")).thenReturn("value3-1");
		when(servletContext.getAttribute("foo.name")).thenReturn("val.ue");
		when(servletContext.getAttribute("foo")).thenReturn(new Foo());
		when(servletContext.getAttribute("boo.attr")).thenReturn("boobaa");
	}

	@Test
	public void testAttributes() {
		assertEquals("value", JspResolver.attribute("name", servletRequest));
		assertEquals("value2", JspResolver.attribute("name2", servletRequest));
		assertEquals("value3-1", JspResolver.attribute("name3", servletRequest));
		assertEquals("Hello", JspResolver.attribute("foo.name", servletRequest)); // 'foo' is an existing property
		assertEquals(Integer.valueOf(3), JspResolver.attribute("foo.array[2]", servletRequest));
		assertEquals("boobaa", JspResolver.attribute("boo.attr", servletRequest));
		assertNull(JspResolver.attribute("xxx.attr", servletRequest));
	}

	@Test
	public void testValues() {
		JspResolver jspResolver = new JspResolver(servletRequest);
		assertEquals("value", jspResolver.value("name"));
		assertEquals("value1", jspResolver.value("name2"));
		assertEquals("value3-1", jspResolver.value("name3"));
		assertEquals("Hello", JspResolver.value("foo.name", servletRequest));
		assertEquals(Integer.valueOf(3), JspResolver.attribute("foo.array[2]", servletRequest));
		assertEquals("boobaa", JspResolver.value("boo.attr", servletRequest));
	}
}
