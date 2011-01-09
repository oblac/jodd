// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import junit.framework.TestCase;

public class TextUtilTest extends TestCase {

	public void testFormatPara() {
		String txt = "123 567 90AB";
		String p = TextUtil.formatParagraph(txt, 6, false);
		assertEquals("123 56\n7 90AB\n", p);

		p = TextUtil.formatParagraph(txt, 4, false);
		assertEquals("123\n567\n90AB\n", p);

		txt = "123  67 90AB";
		p = TextUtil.formatParagraph(txt, 4, false);
		assertEquals("123\n67\n90AB\n", p);

		txt = "123 567 90AB";
		p = TextUtil.formatParagraph(txt, 6, true);
		assertEquals("123\n567\n90AB\n", p);

		txt = "123  67 90AB";
		p = TextUtil.formatParagraph(txt, 4, true);
		assertEquals("123\n67\n90AB\n", p);
		txt = "123  67 90ABCDE";
		p = TextUtil.formatParagraph(txt, 4, true);
		assertEquals("123\n67\n90AB\nCDE\n", p);

		txt = "1234567";
		p = TextUtil.formatParagraph(txt, 4, true);
		assertEquals("1234\n567\n", p);
		p = TextUtil.formatParagraph(txt, 4, false);
		assertEquals("1234\n567\n", p);

	}

	public void testTabsToSpaces() {
		String s = TextUtil.convertTabsToSpaces("q\tqa\t", 3);
		assertEquals("q  qa ", s);
	}

}
