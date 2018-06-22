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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CliOptionsTest {

	private Cli buildCli(final List<String> out) {
		final Cli cli = new Cli();

		cli.option()
			.shortName("a")
			.with(out::add);

		cli.option()
			.longName("bbb")
			.with(out::add);

		cli.option()
			.shortName("c")
			.longName("ccc")
			.with(out::add);

		cli.option()
			.shortName("A")
			.hasArg()
			.with(out::add);

		cli.param()
			.with(arr -> out.add(arr[0]));

		return cli;
	}

	@Test
	void testEmpty() {
		final List<String> out = new ArrayList<>();
		final Cli cli = buildCli(out);

		cli.accept("");
		assertEquals("[]", out.toString());
	}


	@Test
	void testFlags_short() {
		final List<String> out = new ArrayList<>();
		final Cli cli = buildCli(out);

		cli.accept("-a", "-c");
		assertEquals("[a, c]", out.toString());
	}

	@Test
	void testFlags_short_group() {
		final List<String> out = new ArrayList<>();
		final Cli cli = buildCli(out);

		cli.accept("-ac", "--ccc");
		assertEquals("[a, c, ccc]", out.toString());
	}

	@Test
	void testFlags_long() {
		final List<String> out = new ArrayList<>();
		final Cli cli = buildCli(out);

		cli.accept("--bbb", "--ccc");
		assertEquals("[bbb, ccc]", out.toString());
	}

	@Test
	void testOption() {
		final List<String> out = new ArrayList<>();
		final Cli cli = buildCli(out);

		cli.accept("-A", "123");
		assertEquals("[123]", out.toString());
	}

	@Test
	void testOptionWithValue() {
		final List<String> out = new ArrayList<>();
		final Cli cli = new Cli();

		cli.option()
			.shortName("a")
			.hasArg()
			.with(out::add);

		cli.option()
			.longName("foo")
			.hasArg()
			.with(out::add);

		cli.printUsage("cmd");

		cli.accept("-a", "1", "--foo", "F");
		assertEquals("[1, F]", out.toString());
		out.clear();

		cli.accept("-a=1", "--foo=F");
		assertEquals("[1, F]", out.toString());
		out.clear();
	}
}
