// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet;

import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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


	@Before
	public void setUp() throws Exception {

		servletRequest = mock(HttpServletRequest.class);
		httpSession = mock(HttpSession.class);
		servletContext = mock(ServletContext.class);

		when(servletRequest.getAttribute("name")).thenReturn("value");
		when(servletRequest.getParameter("name2")).thenReturn("value1");
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
