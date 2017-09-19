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

package jodd.props;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

	@Test
	public void testAddonFor146() {
		String data =
				"key1=DEFAULT\n" +
				"key1<foo>=FOO\n" +
				"\n" +
				"key2=${key1}\n" +
				"\n" +
				"key3=${key1<foo>}\n" +
				"\n" +
				"key4=${key1}\n" +
				"key4<bar>=${key1<foo>}BAR\n" +
				"\n" +
				"[group1]\n" +
				"key=DEFAULT\n" +
				"key<foo>=FOO\n" +
				"[group2]\n" +
				"<= group1<foo>";

		Props props = new Props();
		props.load(data);

		assertEquals("FOO", props.getValue("key1", "foo"));
		assertEquals("DEFAULT", props.getValue("key1"));

		assertEquals("FOO", props.getValue("key3"));
		assertEquals("FOO", props.getValue("key3", "foo"));
		assertEquals("FOO", props.getValue("key3", "foo", "bar"));

		assertEquals("FOO", props.getValue("key4", "foo"));
		assertEquals("FOOBAR", props.getValue("key4", "bar"));
		assertEquals("DEFAULT", props.getValue("key4"));

		assertEquals("FOO", props.getValue("group2.key"));		// == ${group1.key<foo>}

	}

}
