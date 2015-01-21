// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.form;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FormTextTest {

	@BeforeClass
	public static void beforeClass() {
		LagartoFormSuite.startTomcat();
	}

	@AfterClass
	public static void afterClass() {
		LagartoFormSuite.stopTomcat();
	}

	private static final String TEXT_RESULT = "<input name=\"iname\" type=\"text\" value=\"foo\">";

	@Test
	public void testFormTagTextGet() {
		HttpResponse response = HttpRequest
				.get("localhost:8173/text.jsp")
				.query("iname", "foo")
				.send();

		assertEquals(TEXT_RESULT, response.bodyText().trim());
	}

	@Test
	public void testFormTagTextGetWithValue() {
		HttpResponse response = HttpRequest
				.get("localhost:8173/text2.jsp")
				.query("iname", "foo")
				.send();

		assertEquals(TEXT_RESULT, response.bodyText().trim());
	}

	@Test
	public void testFormTagTextPost() {
		HttpResponse response = HttpRequest
				.post("localhost:8173/text.jsp")
				.form("iname", "foo")
				.send();

		assertEquals(TEXT_RESULT, response.bodyText().trim());
	}

	@Test
	public void testFormTagTextPostMulti() {
		HttpResponse response = HttpRequest
				.post("localhost:8173/text.jsp")
				.form("iname", "foo")
				.multipart(true)
				.send();

		assertEquals(TEXT_RESULT, response.bodyText().trim());
	}

	@Test
	public void testFormTagDuplicateNames() {
		HttpResponse response = HttpRequest
				.post("localhost:8173/text3.jsp")
				.form("cc", "one")
				.form("cc", "two")
				.send();

		System.out.println(response.bodyText().trim());

		assertEquals("<input type=\"text\" name=\"cc\" id=\"cc1\" value=\"one\"/>\n" +
			"<input type=\"text\" name=\"cc\" id=\"cc2\" value=\"two\"/>\n" +
			"<input type=\"text\" name=\"cc\" id=\"cc3\"/>", response.bodyText().trim());
	}
}
