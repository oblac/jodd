// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

import junit.framework.TestCase;

import java.nio.CharBuffer;

public class ParsingProblemsTest extends TestCase {
	
	public void testInvalidTag() {
		String html = "<html>text1<=>text2</html>";

		LagartoParser lagartoParser = new LagartoParser(CharBuffer.wrap(html));
		
		final StringBuilder sb = new StringBuilder();
		
		try {
			lagartoParser.parse(new EmptyTagVisitor() {
				@Override
				public void tag(Tag tag) {
					sb.append(tag.getName()).append(' ');
				}

				@Override
				public void text(CharSequence text) {
					sb.append(text).append(' ');
				}

				@Override
				public void error(String message) {
					System.out.println(message);
				}
			});
		} catch (LagartoException lex) {
			lex.printStackTrace();
			fail();
		}

		assertEquals("html text1<=>text2 html ", sb.toString());
	}
}
