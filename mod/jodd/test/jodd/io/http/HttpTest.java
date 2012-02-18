// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.http;

import junit.framework.TestCase;

public class HttpTest extends TestCase {

	public void testQueryParameters() {
		HttpTransfer ht = new HttpTransfer();

		ht.setPath("");
		assertEquals("/", ht.getPath());

		ht.setPath("jodd");
		assertEquals("/jodd", ht.getPath());
		assertNull(ht.getQueryParameters());

		ht.setQueryParameters(new HttpParams("one=two"));
		assertEquals("/jodd?one=two", ht.getPath());
		HttpParams map = ht.getQueryParameters();
		assertEquals(1, map.getParamsCount());
		assertEquals("two", map.getParameter("one"));

		ht.setQueryParameters(new HttpParams("one"));
		assertEquals("/jodd?one", ht.getPath());
		map = ht.getQueryParameters();
		assertEquals(1, map.getParamsCount());
		assertNull(map.getParameter("one"));

		ht.setQueryParameters(new HttpParams("one="));
		assertEquals("/jodd?one=", ht.getPath());
		map = ht.getQueryParameters();
		assertEquals(1, map.getParamsCount());
		assertEquals("", map.getParameter("one"));

		ht.setQueryParameters(new HttpParams("one=aaa&two=bbb"));
		assertEquals("/jodd?one=aaa&two=bbb", ht.getPath());
		map = ht.getQueryParameters();
		assertEquals(2, map.getParamsCount());
		assertEquals("aaa", map.getParameter("one"));
		assertEquals("bbb", map.getParameter("two"));

		ht.setQueryParameters(new HttpParams("one=&two=aaa"));
		assertEquals("/jodd?one=&two=aaa", ht.getPath());
		map = ht.getQueryParameters();
		assertEquals(2, map.getParamsCount());
		assertEquals("", map.getParameter("one"));
		assertEquals("aaa", map.getParameter("two"));

		ht.setQueryParameters(new HttpParams("one=Супер"));
		assertEquals("/jodd?one=%D0%A1%D1%83%D0%BF%D0%B5%D1%80", ht.getPath());
		map = ht.getQueryParameters();
		assertEquals(1, map.getParamsCount());
		assertEquals("Супер", map.getParameter("one"));

		ht.setQueryParameters(new HttpParams("one=Sуp"));
		assertEquals("/jodd?one=S%D1%83p", ht.getPath());

		ht.setQueryParameters(new HttpParams("one=1&one=2"));
		assertEquals("/jodd?one=1&one=2", ht.getPath());
		map = ht.getQueryParameters();
		assertEquals(1, map.getParamsCount());
		assertEquals("1", ((String[])map.getParameter("one"))[0]);
		assertEquals("2", ((String[])map.getParameter("one"))[1]);
		
		map.addParameter("two", "xxx");
		ht.setQueryParameters(map);
		assertEquals("/jodd?one=1&one=2&two=xxx", ht.getPath());
		
	}

}
