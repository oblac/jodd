// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.props;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Props146Test {

	@Test
	public void testIssue146ActiveProfile() {
		String data =
			"root=/app\n" +
			"root<foo>=/foo\n" +
			"root<bar>=/bar\n" +
			"data.path=${root}/data";

		Props props = new Props();

		props.load(data);

		assertEquals("/app", props.getValue("root"));
		assertEquals("/app/data", props.getValue("data.path"));

		// set active profile, now we expect
		props.setActiveProfiles("foo");

		assertEquals("/foo", props.getValue("root"));
		assertEquals("/foo/data", props.getValue("data.path"));

		// different active profile
		props.setActiveProfiles("bar");

		assertEquals("/bar", props.getValue("root"));
		assertEquals("/bar/data", props.getValue("data.path"));
	}

	@Test
	public void testIssue146DeclaredProfile() {
		String data =
			"root=/app\n" +
			"root<foo>=/foo\n" +
			"data.path=${root}/data\n" +
			"data.path<foo>=${root}/data"
			;

		Props props = new Props();
		props.load(data);

		assertEquals("/app", props.getValue("root"));
		assertEquals("/app/data", props.getValue("data.path"));

		props.setActiveProfiles("foo");

		assertEquals("/foo", props.getValue("root"));
		assertEquals("/foo/data", props.getValue("data.path"));
	}

	@Test
	public void testIssue146Directly() {
		String data =
			"root=/app\n" +
			"root<foo>=/foo\n" +
			"data.path=${root}/data";

		Props props = new Props();

		props.load(data);

		assertEquals("/app", props.getValue("root"));
		assertEquals("/app/data", props.getValue("data.path"));

		assertEquals("/foo", props.getValue("root", "foo"));
		assertEquals("/foo/data", props.getValue("data.path", "foo"));
	}

}