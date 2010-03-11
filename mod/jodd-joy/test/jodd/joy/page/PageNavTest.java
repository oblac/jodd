package jodd.joy.page;

import junit.framework.TestCase;

public class PageNavTest extends TestCase {

	public void testPage() {
		PageNav nav = new PageNav(9, 3, 10);
		assertEquals(1, nav.getFrom());
		assertEquals(9, nav.getTo());

		nav = new PageNav(10, 3, 10);
		assertEquals(1, nav.getFrom());
		assertEquals(10, nav.getTo());


		nav = new PageNav(100, 3, 10);
		assertEquals(1, nav.getFrom());
		assertEquals(10, nav.getTo());

		nav = new PageNav(100, 5, 10);
		assertEquals(1, nav.getFrom());
		assertEquals(10, nav.getTo());

		nav = new PageNav(100, 6, 10);
		assertEquals(1, nav.getFrom());
		assertEquals(10, nav.getTo());

		nav = new PageNav(100, 7, 10);
		assertEquals(2, nav.getFrom());
		assertEquals(11, nav.getTo());

		nav = new PageNav(100, 8, 10);
		assertEquals(3, nav.getFrom());
		assertEquals(12, nav.getTo());

		nav = new PageNav(100, 10, 10);
		assertEquals(5, nav.getFrom());
		assertEquals(14, nav.getTo());


		nav = new PageNav(100, 95, 10);
		assertEquals(90, nav.getFrom());
		assertEquals(99, nav.getTo());

		nav = new PageNav(100, 96, 10);
		assertEquals(91, nav.getFrom());
		assertEquals(100, nav.getTo());

		nav = new PageNav(100, 97, 10);
		assertEquals(91, nav.getFrom());
		assertEquals(100, nav.getTo());

		nav = new PageNav(100, 97, 10);
		assertEquals(91, nav.getFrom());
		assertEquals(100, nav.getTo());

		nav = new PageNav(16, 15, 10);
		assertEquals(7, nav.getFrom());
		assertEquals(16, nav.getTo());

	}

	
}