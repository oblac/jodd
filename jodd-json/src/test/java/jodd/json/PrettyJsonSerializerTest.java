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

package jodd.json;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PrettyJsonSerializerTest {

	@Test
	void testObject() {
		PrettyJsonSerializer jsonSerializer = JsonSerializer.createPrettyOne();
		jsonSerializer.identSize(4);
		jsonSerializer.deep(true);

		String s = jsonSerializer.serialize(object());

		//System.out.println(s);

		assertEquals("{\n" +
			"    \"arr\" : [\n" +
			"        \"qw\",\n" +
			"        {\n" +
			"            \"four\" : \"Xxx\",\n" +
			"            \"three\" : true\n" +
			"        },\n" +
			"        \"ty\"\n" +
			"    ],\n" +
			"    \"one\" : 1,\n" +
			"    \"two\" : \"Second line\",\n" +
			"    \"inner\" : {\n" +
			"        \"four\" : \"Xxx\",\n" +
			"        \"three\" : true\n" +
			"    }\n" +
			"}", s);
	}

	private Map<String, Object> object() {
		HashMap<String, Object> m = new HashMap<>();
		m.put("one", 1);
		m.put("two", "Second line");

		HashMap<String, Object> n = new HashMap<>();
		n.put("three", true);
		n.put("four", "Xxx");

		m.put("inner", n);

		List<Object> l = new ArrayList<>();
		l.add("qw");
		l.add(n);
		l.add("ty");

		m.put("arr", l);

		return m;
	}
}
