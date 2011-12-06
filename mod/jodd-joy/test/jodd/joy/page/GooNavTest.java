// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.page;

import junit.framework.TestCase;

public class GooNavTest extends TestCase {

	public void testPage() {
		GooNav nav = new GooNav(6, 3, 10);
		assertEquals(1, nav.getFrom());
		assertEquals(6, nav.getTo());

		nav = new GooNav(12, 3, 10);
		assertEquals(1, nav.getFrom());
		assertEquals(12, nav.getTo());

		nav = new GooNav(13, 3, 10);
		assertEquals(1, nav.getFrom());
		assertEquals(12, nav.getTo());

		nav = new GooNav(14, 3, 10);
		assertEquals(1, nav.getFrom());
		assertEquals(12, nav.getTo());

		nav = new GooNav(100, 9, 10);
		assertEquals(1, nav.getFrom());
		assertEquals(18, nav.getTo());

		nav = new GooNav(100, 10, 10);
		assertEquals(1, nav.getFrom());
		assertEquals(19, nav.getTo());

		nav = new GooNav(100, 11, 10);
		assertEquals(1, nav.getFrom());
		assertEquals(20, nav.getTo());

		nav = new GooNav(100, 12, 10);
		assertEquals(2, nav.getFrom());
		assertEquals(21, nav.getTo());

		nav = new GooNav(100, 89, 10);
		assertEquals(79, nav.getFrom());
		assertEquals(98, nav.getTo());

		nav = new GooNav(100, 91, 10);
		assertEquals(81, nav.getFrom());
		assertEquals(100, nav.getTo());

	}

	
}
