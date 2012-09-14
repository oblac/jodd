// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import junit.framework.TestCase;

public class StringBandTest extends TestCase {

	public void testSbands() {
		StringBand sb = new StringBand(5);

		assertEquals("", sb.toString());

		assertEquals(5, sb.capacity());
		assertEquals(0, sb.index());
		assertEquals(0, sb.length());

		sb.append("xxx");
		assertEquals(5, sb.capacity());
		assertEquals(1, sb.index());
		assertEquals(3, sb.length());
		assertEquals('x', sb.charAt(0));
		assertEquals('x', sb.charAt(1));
		assertEquals('x', sb.charAt(2));

		sb.append("zzz");
		assertEquals(5, sb.capacity());
		assertEquals(2, sb.index());
		assertEquals(6, sb.length());

		assertEquals("xxxzzz", sb.toString());
		assertEquals("zzz", sb.stringAt(1));
		assertEquals('x', sb.charAt(0));
		assertEquals('x', sb.charAt(1));
		assertEquals('x', sb.charAt(2));
		assertEquals('z', sb.charAt(3));
		assertEquals('z', sb.charAt(4));
		assertEquals('z', sb.charAt(5));

		sb.append("www");
		assertEquals(5, sb.capacity());
		assertEquals(3, sb.index());
		assertEquals(9, sb.length());

		assertEquals("xxxzzzwww", sb.toString());
		assertEquals("www", sb.stringAt(2));
		assertEquals('x', sb.charAt(2));
		assertEquals('z', sb.charAt(3));
		assertEquals('z', sb.charAt(5));
		assertEquals('w', sb.charAt(6));
		assertEquals('w', sb.charAt(8));

		sb.setIndex(1);

		assertEquals(5, sb.capacity());
		assertEquals(1, sb.index());
		assertEquals(3, sb.length());

		assertEquals("xxx", sb.toString());
		assertEquals('x', sb.charAt(2));

	}
}
