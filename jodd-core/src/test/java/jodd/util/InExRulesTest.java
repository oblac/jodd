// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InExRulesTest {

	@Test
	public void testIncludeExcludes() {
		InExRules<String, String> inExRules = new InExRules<String, String>(InExRuleMatcher.WILDCARD_RULE_MATCHER);

		// + exclude all
		inExRules.reset();
		inExRules.exclude("*");

		assertFalse(inExRules.match("foo"));
		assertFalse(inExRules.match("fight"));
		assertFalse(inExRules.match("bar"));

		// + exclude all, but one
		inExRules.reset();
		inExRules.exclude("*");
		inExRules.include("foo");

		assertTrue(inExRules.match("foo"));
		assertFalse(inExRules.match("fight"));
		assertFalse(inExRules.match("bar"));

		// + exclude all, but one (alt)
		inExRules.reset();
		inExRules.include("foo");

		assertTrue(inExRules.match("foo"));
		assertFalse(inExRules.match("fight"));
		assertFalse(inExRules.match("bar"));

		// + include all, but one
		inExRules.reset();
		inExRules.exclude("foo");

		assertFalse(inExRules.match("foo"));
		assertTrue(inExRules.match("fight"));
		assertTrue(inExRules.match("bar"));

		// + include all
		inExRules.reset();

		assertTrue(inExRules.match("foo"));
		assertTrue(inExRules.match("fight"));

		// + exclude some, but one
		inExRules.reset();
		inExRules.exclude("f*");
		inExRules.include("foo");

		assertTrue(inExRules.match("foo"));
		assertFalse(inExRules.match("fight"));
		assertTrue(inExRules.match("bar"));

		// + include only some, but one
		inExRules.reset();
		inExRules.exclude("*");
		inExRules.include("f*");
		inExRules.exclude("foo", true);

		assertFalse(inExRules.match("foo"));
		assertTrue(inExRules.match("fight"));
		assertTrue(inExRules.match("fravia"));
		assertFalse(inExRules.match("bar"));

		// + exclude only some, but one
		inExRules.reset();
		inExRules.exclude("f*");
		inExRules.include("foo");

		assertTrue(inExRules.match("foo"));
		assertFalse(inExRules.match("fight"));
		assertFalse(inExRules.match("fravia"));
		assertTrue(inExRules.match("bar"));

	}
}