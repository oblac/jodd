// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import junit.framework.TestCase;

public class WildcardTest extends TestCase {

	public void testMatch() {
		assertTrue(Wildcard.match("CfgOptions.class", "C*class"));
		assertFalse(Wildcard.match("CfgOptions.class", "C*clas"));
		assertFalse(Wildcard.match("C*r*class", "CfgOptions.class"));

		// wildcard on the start and end of the expression
		assertTrue(Wildcard.match("CfgOptions.class", "*g*class"));
		assertTrue(Wildcard.match("CfgOptions.class", "*C*g*class"));
		assertTrue(Wildcard.match("CfgOptions.class", "C*g*cl*"));
		assertTrue(Wildcard.match("CfgOptions.class", "*C*g*cl*"));

		// multiple wildcards
		assertTrue(Wildcard.match("CfgOptions.class", "*g*c**ss"));
		assertTrue(Wildcard.match("CfgOptions.class", "*g*c**s"));	// THIS IS TRUE!!!
		assertTrue(Wildcard.match("CfgOptions.class", "*gOpti******ons.c**ss"));
		assertTrue(Wildcard.match("CfgOptions.class", "***gOpti*ons.c**ss"));
		assertTrue(Wildcard.match("CfgOptions.class", "***gOptions.c**"));

		// wildcard '?':
		assertTrue(Wildcard.match("CfgOptions.class", "*gOpti*c?ass"));
		assertFalse(Wildcard.match("CfgOptions.class", "*gOpti*c?ss"));

		// wildcard on the start and end of the expression:
		assertTrue(Wildcard.match("CfgOptions.class", "*gOpti*c?as?"));
		assertFalse(Wildcard.match("CfgOptions.class", "*gOpti*c?a?"));
		assertTrue(Wildcard.match("CfgOptions.class", "?fgOpti*c?ass"));
		assertFalse(Wildcard.match("CfgOptions.class", "?gOpti*c?as?"));
		assertTrue(Wildcard.match("CfgOptions.class", "??gOpti*c?ass"));

		// multiple wildcards
		assertTrue(Wildcard.match("CfgOptions.class", "C????ti*c?ass"));
		assertFalse(Wildcard.match("CfgOptions.class", "C???ti*c?ass"));

		// wildcards '*' and '?' mixed together:
		assertTrue(Wildcard.match("CfgOptions.class", "C??*ti*c?ass"));
		assertTrue(Wildcard.match("CfgOptions.class", "C*ti*c?as?*"));
		assertTrue(Wildcard.match("CfgOptions.class", "C*ti*c?a*?"));
		assertTrue(Wildcard.match("CfgOptions.class", "C*ti*?a*"));
		assertTrue(Wildcard.match("CfgOptions.class", "C*ti*c?a?*"));
		assertFalse(Wildcard.match("CfgOptions.class", "C*ti*c?*la?*"));

		// escaped wildcards:
		assertFalse(Wildcard.match("CfgOptions.class", "*gOpti\\*c?ass"));
		assertTrue(Wildcard.match("CfgOpti*class", "*gOpti\\*class"));
		assertTrue(Wildcard.match("CfgOpti*class", "*gOpti\\*c?ass"));
		assertFalse(Wildcard.match("CfgOpti*class", "*gOpti\\\\*c?ass"));
		assertTrue(Wildcard.match("CfgOpti\\*class", "*gOpti\\\\*c?ass"));
		assertTrue(Wildcard.match("CfgOpti?class", "*gOpti\\?c*ass"));
		assertFalse(Wildcard.match("CfgOpti\\?class", "*gOpti\\?c*ass"));
		assertTrue(Wildcard.match("CfgOptions.class", "CfgOpti\\ons.class"));
		assertTrue(Wildcard.match("What's this?", "What*\\?"));
	}

}
