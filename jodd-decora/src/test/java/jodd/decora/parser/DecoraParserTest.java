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

package jodd.decora.parser;

import jodd.io.FastCharArrayWriter;
import jodd.io.FileUtil;
import jodd.io.findfile.FindFile;
import jodd.io.findfile.WildcardFindFile;
import jodd.util.StringUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DecoraParserTest {

	protected String testDataRoot;

	@BeforeEach
	void setUp() throws Exception {
		if (testDataRoot != null) {
			return;
		}
		URL data = DecoraParserTest.class.getResource("test");
		testDataRoot = data.getFile();
	}

	@Test
	void testDecoraParser() throws IOException {
		DecoraParser decoraParser = new DecoraParser();

		FindFile ff = new WildcardFindFile().include("*.*ml");
		ff.matchType(FindFile.Match.NAME);
		ff.searchPath(testDataRoot);

		File file;
		while ((file = ff.nextFile()) != null) {

			System.out.println("+" + file.getName());

			char[] page = FileUtil.readString(file).toCharArray();

			String decoratorFileName = StringUtil.replace(file.getAbsolutePath(), ".html", "-decora.htm");
			char[] decorator = FileUtil.readString(decoratorFileName).toCharArray();

			FastCharArrayWriter writer = new FastCharArrayWriter();
			decoraParser.decorate(writer, page, decorator);
			String out = writer.toString();

			String outFileName = StringUtil.replace(file.getAbsolutePath(), ".html", "-out.htm");
			String outExpected = FileUtil.readString(outFileName);

			assertEquals(trimLines(outExpected), trimLines(out));
		}
	}


	private String trimLines(String string) throws IOException {
		BufferedReader in = new BufferedReader(new CharArrayReader(string.toCharArray()));
		StringBuilder result = new StringBuilder(string.length());
		String line;
		while ((line = in.readLine()) != null) {
			result.append(line.trim()).append('\n');
		}
		return result.toString();
	}
}
