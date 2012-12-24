// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.decora.parser;

import jodd.io.FastCharArrayWriter;
import jodd.io.FileUtil;
import jodd.io.findfile.FindFile;
import jodd.io.findfile.WildcardFindFile;
import jodd.util.StringUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class DecoraParserTest {

	protected String testDataRoot;

	@Before
	public void setUp() throws Exception {
		if (testDataRoot != null) {
			return;
		}
		URL data = DecoraParserTest.class.getResource("test");
		testDataRoot = data.getFile();
	}

	@Test
	public void testDecoraParser() throws IOException {
		DecoraParser decoraParser = new DecoraParser();

		FindFile ff = new WildcardFindFile("*.*ml");
		ff.searchPath(testDataRoot);
		File file;
		while ((file = ff.nextFile()) != null) {

/*
if (file.getName().equals("common.html") == false) {
	continue;
}
*/
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
