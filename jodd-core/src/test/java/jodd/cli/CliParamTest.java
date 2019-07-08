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

package jodd.cli;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CliParamTest {

	@Test
	void testParam_1required() {
		final List<String> out = new ArrayList<>();
		final Cli cli = new Cli();

		cli.param()
			.required()
			.with(v -> out.addAll(Arrays.asList(v)));

		cli.accept("123");
		assertEquals("[123]", out.toString());
		out.clear();

		cli.accept("123", "456");
		assertEquals("[123]", out.toString());
	}

	@Test
	void testOneParam_2required() {
		final List<String> out = new ArrayList<>();
		final Cli cli = new Cli();

		cli.param()
			.required(2)
			.with(v -> out.addAll(Arrays.asList(v)));

		cli.accept("123", "456");
		assertEquals("[123, 456]", out.toString());
	}

	@Test
	void testOneParam_2required_1optional() {
		final List<String> out = new ArrayList<>();
		final Cli cli = new Cli();

		cli.param()
			.required(2)
			.optional(1)
			.with(v -> out.addAll(Arrays.asList(v)));

		cli.accept("123", "456");
		assertEquals("[123, 456]", out.toString());
		out.clear();

		cli.accept("123", "456", "789");
		assertEquals("[123, 456, 789]", out.toString());
		out.clear();
	}

	@Test
	void testOneParam_3optional() {
		final List<String> out = new ArrayList<>();
		final Cli cli = new Cli();

		cli.param()
			.optional(3)
			.with(v -> out.addAll(Arrays.asList(v)));

		cli.accept("123");
		assertEquals("[123]", out.toString());
		out.clear();

		cli.accept("123", "456");
		assertEquals("[123, 456]", out.toString());
		out.clear();

		cli.accept("123", "456", "789");
		assertEquals("[123, 456, 789]", out.toString());
		out.clear();
	}

	@Test
	void testDoubleDash() {
		final List<String> out = new ArrayList<>();
		final Cli cli = new Cli();

		cli.param()
			.required()
			.with(v -> out.addAll(Arrays.asList(v)));

		cli.param()
			.optional()
			.with(v -> out.addAll(Arrays.asList(v)));

		cli.accept("123");
		assertEquals("[123]", out.toString());
		out.clear();

		cli.accept("123", "456");
		assertEquals("[123, 456]", out.toString());
		out.clear();

		cli.accept("123", "--", "--789");
		assertEquals("[123, --789]", out.toString());
		out.clear();
	}

	@Test
	void testAll() {
		final List<String> out = new ArrayList<>();
		final Cli cli = new Cli();

		cli.param()
			.required()
			.with(v -> out.addAll(Arrays.asList(v)));

		cli.param()
			.all()
			.with(v -> out.addAll(Arrays.asList(v)));

		cli.accept("123");
		assertEquals("[123]", out.toString());
		out.clear();

		cli.accept("123", "456");
		assertEquals("[123, 456]", out.toString());
		out.clear();

		cli.accept("123", "456", "789");
		assertEquals("[123, 456, 789]", out.toString());
		out.clear();
	}

	@Test
	void testThreeParams() {
		final List<String> out = new ArrayList<>();
		final Cli cli = new Cli();

		cli.param()
			.with(v -> out.addAll(Arrays.asList(v)));
		cli.param()
			.with(v -> out.addAll(Arrays.asList(v)));
		cli.param()
			.with(v -> out.addAll(Arrays.asList(v)));

		cli.accept();
		assertEquals("[]", out.toString());

		cli.accept("123");
		assertEquals("[123]", out.toString());
		out.clear();

		cli.accept("123", "456");
		assertEquals("[123, 456]", out.toString());
		out.clear();

		cli.accept("123", "456", "789");
		assertEquals("[123, 456, 789]", out.toString());
		out.clear();
	}

	@Test
	void testTwoAndOne() {
		final List<String> out = new ArrayList<>();
		final Cli cli = new Cli();

		cli.param()
			.required(2)
			.optional(1)
			.with(v -> out.addAll(Arrays.asList(v)));

		cli.accept("1", "2");
		assertEquals("[1, 2]", out.toString());
		out.clear();

		cli.accept("1", "2", "3");
		assertEquals("[1, 2, 3]", out.toString());
		out.clear();

		cli.accept("1", "2", "3", "4");
		assertEquals("[1, 2, 3]", out.toString());
		out.clear();

		assertThrows(CliException.class, () -> cli.accept("1"));
	}


}
