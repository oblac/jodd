// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class InjectionPointFactoryTest {

	@Before
	public void setUp() throws Exception {
		ipf = new InjectionPointFactory(new PetiteConfig());
	}

	InjectionPointFactory ipf;

	@Test
	public void testDuplicateNamesSpecialCases() {
		String[] s = new String[]{};
		ipf.removeDuplicateNames(s);
		assertEquals(0, s.length);

		s = new String[]{"aaa"};
		ipf.removeDuplicateNames(s);
		assertEquals("aaa", s[0]);

		s = new String[]{null};
		ipf.removeDuplicateNames(s);
		assertNull(s[0]);

		s = new String[]{null, null};
		ipf.removeDuplicateNames(s);
		assertNull(s[0]);
		assertNull(s[1]);
	}

	@Test
	public void testDuplicateNames() {
		String[] s = new String[]{"foo", "foo", "boo", "foo"};
		ipf.removeDuplicateNames(s);
		assertEquals("foo", s[0]);
		assertNull(s[1]);
		assertEquals("boo", s[2]);
		assertNull(s[3]);
	}

	@Test
	public void testDuplicateNames2() {
		String[] s = new String[]{"boo", "foo", "boo", "foo"};
		ipf.removeDuplicateNames(s);
		assertEquals("boo", s[0]);
		assertEquals("foo", s[1]);
		assertNull(s[2]);
		assertNull(s[3]);
	}

	@Test
	public void testDuplicateNames3() {
		String[] s = new String[]{"boo", "boo"};
		ipf.removeDuplicateNames(s);
		assertEquals("boo", s[0]);
		assertNull(s[1]);
	}


}
