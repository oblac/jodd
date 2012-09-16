// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet;

import junit.framework.TestCase;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JspResolverTest extends TestCase {

	static class Foo {
		String name = "Hello";
		int[] array = new int[] {1, 7, 3};
	}

	HttpServletRequest servletRequest;
	HttpSession httpSession;
	ServletContext servletContext;


	@Override
	public void setUp() throws Exception {
		super.setUp();

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

	public void testAttributes() {
		assertEquals("value", JspResolver.attribute("name", servletRequest));
		assertEquals("value2", JspResolver.attribute("name2", servletRequest));
		assertEquals("value3-1", JspResolver.attribute("name3", servletRequest));
		assertEquals("Hello", JspResolver.attribute("foo.name", servletRequest)); // 'foo' is an existing property
		assertEquals(Integer.valueOf(3), JspResolver.attribute("foo.array[2]", servletRequest));
		assertEquals("boobaa", JspResolver.attribute("boo.attr", servletRequest));
		assertNull(JspResolver.attribute("xxx.attr", servletRequest));
	}

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
