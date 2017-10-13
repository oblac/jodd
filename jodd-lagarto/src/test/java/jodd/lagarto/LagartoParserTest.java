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

package jodd.lagarto;

import jodd.datetime.JStopWatch;
import jodd.io.FileUtil;
import jodd.io.findfile.FindFile;
import jodd.io.findfile.WildcardFindFile;
import jodd.util.StringUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static jodd.util.StringPool.NEWLINE;
import static org.junit.jupiter.api.Assertions.*;

public class LagartoParserTest {

	protected String testDataRoot;
	protected String testDataRoot2;
	protected String testLiveRoot;

	@BeforeEach
	public void setUp() throws Exception {
		if (testDataRoot != null) {
			return;
		}
		URL data = LagartoParserTest.class.getResource("test");
		testDataRoot = data.getFile();

		data = LagartoParserTest.class.getResource("test2");
		testDataRoot2 = data.getFile();

		data = LagartoParserTest.class.getResource("live");
		testLiveRoot = data.getFile();
	}

	@Test
	public void testHtmls() throws IOException {
		_testHtmls(testDataRoot);
	}

	@Test
	public void testHtmls2() throws IOException {
		_testHtmls(testDataRoot2);
	}

	private void _testHtmls(String root) throws IOException {
		FindFile ff = new WildcardFindFile().include("**/*.*ml");
		long reps = 1;
		JStopWatch jsw = new JStopWatch();
		boolean processed = false;
		while (reps-- > 0) {
			ff.searchPath(root);
			File file;
			while ((file = ff.nextFile()) != null) {
				processed = true;
				System.out.println('+' + file.getName());

				String content = FileUtil.readString(file);
				content = StringUtil.removeChars(content, '\r');
				String expectedResult = FileUtil.readString(new File(file.getAbsolutePath() + ".txt"));

				String formatted = null;
				File formattedFile = new File(file.getAbsolutePath() + "-fmt.htm");
				if (formattedFile.exists()) {
					formatted = FileUtil.readString(formattedFile);
				}
				if (formatted != null) {
					formatted = StringUtil.removeChars(formatted, '\r');
				}

				boolean isXml = file.getName().endsWith(".xml");

				String[] results = _parse(content, isXml);
				String result = results[0];		// parsing result
				String result2 = results[1];	// tag writer

				expectedResult = StringUtil.removeChars(expectedResult, '\r');
				result = StringUtil.removeChars(result, '\r').trim();
				result2 = StringUtil.removeChars(result2, '\r').trim();

				assertEquals(expectedResult, result);

				if (formatted != null) {
					assertEquals(formatted, result2);
				} else {
					assertEquals(content, result2);
				}
			}
		}
		assertTrue(processed);
		System.out.println(jsw);
	}

	/**
	 * 13s
	 */
	@Test
	public void testLiveHtmls() throws IOException {
		FindFile ff = new WildcardFindFile().include("**/*.html");
		ff.searchPath(testLiveRoot);
		File file;
		boolean processed = false;
		while ((file = ff.nextFile()) != null) {
			processed = true;
			String name = file.getName();
			System.out.println('+' + name);
			String content = FileUtil.readString(file);
			try {
				_parseEmpty(content);
			} catch (Exception ex) {
				ex.printStackTrace();
				fail(ex.toString());
			}
		}
		assertTrue(processed);
	}

	private String _parseEmpty(String content) {
		LagartoParser lagartoParser = new LagartoParser(content, false);
		lagartoParser.getConfig().setCalculatePosition(true);
		final StringBuilder errors = new StringBuilder();
		lagartoParser.parse(new EmptyTagVisitor() {
			@Override
			public void error(String message) {
				errors.append(message);
				errors.append('\n');
			}
		});
		return errors.toString();
	}

	private String[] _parse(String content, boolean isXml) {
		final StringBuilder result = new StringBuilder();
		final StringBuilder out = new StringBuilder();

		TagVisitor visitor = new TagVisitor() {

			public void start() {
			}

			public void end() {
			}

			public void tag(Tag tag) {
				result.append("tag:").append(tag.getName());
				result.append(':').append(tag.getDeepLevel());
				switch (tag.getType()) {
					case START:
						result.append('<');
						break;
					case END:
						result.append('>');
						break;
					case SELF_CLOSING:
						result.append("<>");
						break;
				}
				if (tag.getAttributeCount() > 0) {
					try {
						tag.writeTo(result);
					} catch (IOException ignored) {
					}
				}
				result.append(NEWLINE);
			}

			public void xml(CharSequence version, CharSequence encoding, CharSequence standalone) {
				result.append("xml:").append(version).append(':').append(encoding).append(':').append(standalone);
				result.append(NEWLINE);
			}

			public void script(Tag tag, CharSequence bodyM) {
				result.append("scr:").append(tag.getDeepLevel());
				if (tag.getAttributeCount() > 0) {
					try {
						tag.writeTo(result);
					} catch (IOException ignored) {
					}
				}
				String body = bodyM.toString();
				body = StringUtil.removeChars(body, "\r\n\t\b");
				result.append('[').append(body).append(']');
				result.append(NEWLINE);
			}

			public void comment(CharSequence commentM) {
				String comment = commentM.toString();
				comment = StringUtil.removeChars(comment, "\r\n\t\b");
				result.append("com:[").append(comment).append(']').append(NEWLINE);
			}

			public void cdata(CharSequence cdataM) {
				String cdata = cdataM.toString();
				cdata = StringUtil.removeChars(cdata, "\r\n\t\b");
				result.append("cdt:[").append(cdata).append(']').append(NEWLINE);
			}

			public void doctype(Doctype doctype) {
				result.append("doc:[").append(doctype.getName()).append(' ');
				result.append(doctype.getPublicIdentifier()).append(' ').append(doctype.getSystemIdentifier()).append(']').append(NEWLINE);
			}

			public void condComment(CharSequence expression, boolean isStartingTag, boolean isHidden, boolean isHiddenEndTag) {
				result.append(isStartingTag ? "CC" : "cc").append(isHidden ? 'H' : 'S');
				result.append(isHiddenEndTag ? "h" : "");
				result.append(":[").append(expression).append(']');
				result.append(NEWLINE);

			}

			public void text(CharSequence text) {
				String t = text.toString();
				t = StringUtil.removeChars(t, "\r\n\t\b");
				if (t.length() != 0) {
					result.append("txt:[").append(t).append(']').append(NEWLINE);
				}
			}

			public void error(String message) {
				result.append("wrn:[").append(message).append(NEWLINE);
			}
		};


		LagartoParser lagartoParser = new LagartoParser(content, false);
		lagartoParser.getConfig().setCalculatePosition(true);

		if (isXml) {
			lagartoParser.getConfig().setParseXmlTags(true);
		}

		TagWriter tagWriter = new TagWriter(out);

		lagartoParser.parse(new TagVisitorChain(visitor, tagWriter));

		return new String[]{result.toString(), out.toString()};
	}

}
