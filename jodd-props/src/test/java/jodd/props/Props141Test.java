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

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class Props141Test extends BasePropsTest {

	@Test
	public void test141Simple() throws IOException {
		Props props = new Props();
		String data = readDataFile("i141.props");
		props.load(data);

		assertEquals("value1", props.getValue("key1"));

		assertNull(props.getValue(".key1", "ONE"));
		assertEquals("value1#ONE", props.getValue("key1", "ONE"));
		assertEquals("value1", props.getValue("key1", "qwe", null));
	}

	@Test
	public void test141Complex() throws IOException {
		Props props = new Props();
		String data = readDataFile("i141-2.props");
		props.load(data);

		// Without profile, and using ERROR profile
		assertEquals("NOT AN ERROR 1", props.getValue("code", null));
		assertEquals("NOT AN ERROR 2", props.getValue("label", null));
		assertEquals("NOT AN ERROR 3", props.getValue("details", null));

		assertEquals("#UNDEFINED", props.getValue("code", "ERROR"));
		assertEquals("UNDEFINED LABEL", props.getValue("label", "ERROR"));
		assertEquals("UNDEFINED DETAILS", props.getValue("details", "ERROR"));

		// Using the ERROR.ONE inner profile
		assertEquals("#ONE", props.getValue("code", "ERROR.ONE"));
		assertEquals("THIS IS ERROR #ONE", props.getValue("label", "ERROR.ONE"));
		assertEquals("UNDEFINED DETAILS", props.getValue("details", "ERROR.ONE"));

		// Now, using ERROR.TWO inner profile, which uses another syntax:
		assertEquals("#TWO", props.getValue("code", "ERROR.TWO"));
		assertEquals("THIS IS ERROR #TWO", props.getValue("label", "ERROR.TWO"));
		assertEquals("UNDEFINED DETAILS", props.getValue("details", "ERROR.TWO"));

		// trying to use a third inner profile, not defined in the properties
		assertEquals("#UNDEFINED", props.getValue("code", "ERROR.THREE"));
		assertEquals("UNDEFINED LABEL", props.getValue("label", "ERROR.THREE"));
		assertEquals("UNDEFINED DETAILS", props.getValue("details", "ERROR.THREE"));
	}
}
