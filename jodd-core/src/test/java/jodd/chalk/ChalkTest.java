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

package jodd.chalk;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * test class for {@link Chalk}
 */
class ChalkTest {

	private Chalk chalk;

	@BeforeEach
	void beforeTest() {
		chalk = new Chalk();
	}

	@ParameterizedTest(name = "{index} - Chalk#{0}()")
	@MethodSource(value = "testdata_checkChalkOutput")
	void checkChalkOutput(final String methodname, final String text, final String expected) throws Exception {

		final String actual = ((Chalk) (Chalk.class.getMethod(methodname).invoke(chalk))).on(text);

		// output on console
		System.out.println(actual);

		// asserts
		assertNotNull(actual);
		assertEquals(expected, actual);
	}

	private static Collection<Arguments> testdata_checkChalkOutput() {
		final List<Arguments> params = new ArrayList<>();

		final String text = "Jodd";

		// style - methods
		params.add(Arguments.of("bold", text, "\u001B[1mJodd\u001B[22m"));
		params.add(Arguments.of("italic", text, "\u001B[3mJodd\u001B[23m"));
		params.add(Arguments.of("dim", text, "\u001B[2mJodd\u001B[22m"));
		params.add(Arguments.of("underline", text, "\u001B[4mJodd\u001B[24m"));
		params.add(Arguments.of("inverse", text, "\u001B[7mJodd\u001B[27m"));
		params.add(Arguments.of("hidden", text, "\u001B[8mJodd\u001B[28m"));
		params.add(Arguments.of("strikeThrough", text, "\u001B[9mJodd\u001B[29m"));
		// colors - methods
		params.add(Arguments.of("black", text, "\u001B[30mJodd\u001B[39m"));
		params.add(Arguments.of("red", text, "\u001B[31mJodd\u001B[39m"));
		params.add(Arguments.of("green", text, "\u001B[32mJodd\u001B[39m"));
		params.add(Arguments.of("yellow", text, "\u001B[33mJodd\u001B[39m"));
		params.add(Arguments.of("blue", text, "\u001B[34mJodd\u001B[39m"));
		params.add(Arguments.of("magenta", text, "\u001B[35mJodd\u001B[39m"));
		params.add(Arguments.of("cyan", text, "\u001B[36mJodd\u001B[39m"));
		params.add(Arguments.of("white", text, "\u001B[37mJodd\u001B[39m"));
		params.add(Arguments.of("gray", text, "\u001B[90mJodd\u001B[39m"));
		params.add(Arguments.of("grey", text, "\u001B[90mJodd\u001B[39m"));
		// bg colors - methods
		params.add(Arguments.of("bgBlack", text, "\u001B[40mJodd\u001B[49m"));
		params.add(Arguments.of("bgRed", text, "\u001B[41mJodd\u001B[49m"));
		params.add(Arguments.of("bgGreen", text, "\u001B[42mJodd\u001B[49m"));
		params.add(Arguments.of("bgYellow", text, "\u001B[43mJodd\u001B[49m"));
		params.add(Arguments.of("bgBlue", text, "\u001B[44mJodd\u001B[49m"));
		params.add(Arguments.of("bgMagenta", text, "\u001B[45mJodd\u001B[49m"));
		params.add(Arguments.of("bgCyan", text, "\u001B[46mJodd\u001B[49m"));
		params.add(Arguments.of("bgWhite", text, "\u001B[47mJodd\u001B[49m"));

		return params;
	}

	@Test
	void testCombinedOutput() {
		StringBuilder sb = new StringBuilder();

		Chalk chalk = Chalk.chalk();

		sb.append(chalk.bgCyan().green().on("Jodd"));
		sb.append(chalk.bgBlue().yellow().on("Makes"));
		sb.append(chalk.bgRed().black().bold().on("FUN"));

		final String actual = sb.toString();
		// output on console
		System.out.println(actual);

		// asserts
		assertEquals("\u001B[46;32mJodd\u001B[39;49m\u001B[46;32;44;33mMakes\u001B[39;49;39;49m\u001B[46;32;44;33;41;30;1mFUN\u001B[22;39;49;39;49;39;49m", actual);
	}
}