package jodd.lagarto;

import jodd.datetime.JStopWatch;
import jodd.io.FileUtil;
import jodd.io.findfile.FindFile;
import jodd.io.findfile.WildcardFindFile;
import jodd.util.StringUtil;
import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.CharBuffer;

import static jodd.util.StringPool.NEWLINE;

public class LagartoParserTest extends TestCase {

	protected String testDataRoot;
	protected String testLiveRoot;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		if (testDataRoot != null) {
			return;
		}
		URL data = LagartoParserTest.class.getResource("test");
		testDataRoot = data.getFile();

		data = LagartoParserTest.class.getResource("live");
		testLiveRoot = data.getFile();
	}

	public void testHtmls() throws IOException {
		FindFile ff = new WildcardFindFile("*.*ml");
		long reps = 1;
		JStopWatch jsw = new JStopWatch();
		while (reps-- > 0) {
			ff.searchPath(testDataRoot);
			File file;
			while ((file = ff.nextFile()) != null) {
				System.out.println("+" + file.getName());
				String content = FileUtil.readString(file);
				String expectedResult = FileUtil.readString(new File(file.getAbsolutePath() + ".txt"));

				String formattedOut = null;
				File formatted = new File(file.getAbsolutePath() + ".htm");
				if (formatted.exists()) {
					formattedOut = FileUtil.readString(formatted);
				}
				String formattedOut2 = null;
				formatted = new File(file.getAbsolutePath() + "-fmt.htm");
				if (formatted.exists()) {
					formattedOut2 = FileUtil.readString(formatted);
				}

				String[] results = parse(content);
				String result = results[0];
				String result2 = results[1];
				String result3 = results[2];

				expectedResult = StringUtil.removeChars(expectedResult, '\r');
				result = StringUtil.removeChars(result, '\r').trim();

				assertEquals(expectedResult, result);

				if (formattedOut != null) {
					assertEquals(formattedOut, result2);
				} else {
					assertEquals(content, result2);
				}

				if (formattedOut2 != null) {
					assertEquals(formattedOut2, result3);
				} else {
					assertEquals(content, result3);
				}
			}
		}
		System.out.println(jsw);
	}

	/**
	 * 13s
	 */
	public void testLiveHtmls() throws IOException {
		FindFile ff = new WildcardFindFile("*.html");
		ff.searchPath(testLiveRoot);
		File file;
		while ((file = ff.nextFile()) != null) {
			String name = file.getName();
			System.out.println("+" + name);
			String content = FileUtil.readString(file);
			String errors = "";
			try {
				errors = parseEmpty(content);
			} catch (Exception ex) {
				ex.printStackTrace();
				fail(ex.toString());
			}

			if (name.equals("Answers.com.html") || name.equals("Yahoo!.html")) {
				System.out.println(errors);
				continue;
			}
			assertEquals(0, errors.length());
		}
	}

	private String parseEmpty(String content) {
		LagartoParser lagartoParser = new LagartoParser(CharBuffer.wrap(content));
		final StringBuilder errors = new StringBuilder();
		lagartoParser.parse(new EmptyTagVisitor() {
			@Override
			public void error(String message) {
				errors.append(message);
				errors.append("\n");
			}
		});
		return errors.toString();
	}

	private String[] parse(String content) {
		final StringBuilder result = new StringBuilder();
		final StringBuilder out = new StringBuilder();
		final StringBuilder out2 = new StringBuilder();
		TagVisitor visitor = new TagVisitor() {

			public void start() {
			}

			public void end() {
			}

			public void tag(Tag tag) {
				result.append("tag:").append(tag.getName());
				result.append(':').append(tag.getDeepLevel());
				switch (tag.getType()) {
					case OPEN:
						result.append('<');
						break;
					case CLOSE:
						result.append('>');
						break;
					case EMPTY:
						result.append("<>");
						break;
				}
				if (tag.getAttributeCount() > 0) {
					try {
						tag.writeTo(result, true);
					} catch (IOException ignored) {
					}
				}
				result.append(NEWLINE);
			}

			public void xml(Tag tag) {
				result.append("xml:").append(tag.getDeepLevel());
				if (tag.getAttributeCount() > 0) {
					try {
						tag.writeTo(result, true);
					} catch (IOException ignored) {

					}
				}
				result.append(NEWLINE);
			}

			public void xmp(Tag tag, CharSequence bodyM) {
				result.append("xmp:").append(tag.getDeepLevel());
				if (tag.getAttributeCount() > 0) {
					try {
						tag.writeTo(result, true);
					} catch (IOException ignored) {
					}
				}
				String body = bodyM.toString();
				body = StringUtil.removeChars(body, "\r\n\t\b");
				result.append('[').append(body).append(']');
				result.append(NEWLINE);
			}

			public void style(Tag tag, CharSequence bodyM) {
				result.append("css:").append(tag.getDeepLevel());
				if (tag.getAttributeCount() > 0) {
					try {
						tag.writeTo(result, true);
					} catch (IOException ignored) {
					}
				}
				String body = bodyM.toString();
				body = StringUtil.removeChars(body, "\r\n\t\b");
				result.append('[').append(body).append(']');
				result.append(NEWLINE);
			}

			public void script(Tag tag, CharSequence bodyM) {
				result.append("scr:").append(tag.getDeepLevel());
				if (tag.getAttributeCount() > 0) {
					try {
						tag.writeTo(result, true);
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

			public void doctype(String name, String publicId, String baseUri) {
				result.append("doc:[").append(name).append(' ');
				result.append(publicId).append(' ').append(baseUri).append(']').append(NEWLINE);
			}

			public void condComment(CharSequence conditionalComment, boolean isStartingTag, boolean isDownlevelHidden) {
				result.append(isStartingTag ? "CC" : "cc").append(isDownlevelHidden ? 'H' : 'S');
				result.append(":[").append(conditionalComment).append(']').append(NEWLINE);

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
		TagWriter writer1 = new TagWriter(out, false);
		TagWriter writer2 = new TagWriter(out2, true);

		LagartoParser lagartoParser = new LagartoParser(CharBuffer.wrap(content));

		TagAdapterWrapper taw = new TagAdapterWrapper(visitor,
				new TagAdapterWrapper(writer1, writer2));

		lagartoParser.parse(taw);
		return new String[] {result.toString(), out.toString(), out2.toString()};
	}

}
