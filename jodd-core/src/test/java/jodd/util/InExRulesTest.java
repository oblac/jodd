// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InExRulesTest {

	@Test
	public void testIncludeExcludes() {
		InExRules<String, String> inExRules = new InExRules<String, String>(InExRuleMatcher.WILDCARD_RULE_MATCHER);

		assertTrue(inExRules.isBlacklist());
		assertFalse(inExRules.isWhitelist());

		// + exclude all
		inExRules.reset();
		inExRules.blacklist();						// default
		inExRules.exclude("*");

		assertFalse(inExRules.match("foo"));
		assertFalse(inExRules.match("fight"));
		assertFalse(inExRules.match("bar"));


		// + exclude all (alt)
		inExRules.reset();
		inExRules.whitelist();

		assertFalse(inExRules.match("foo"));
		assertFalse(inExRules.match("fight"));
		assertFalse(inExRules.match("bar"));



		// + exclude all, but one (no alt)
		inExRules.reset();
		inExRules.whitelist();
		inExRules.include("foo");

		assertTrue(inExRules.match("foo"));
		assertFalse(inExRules.match("fight"));
		assertFalse(inExRules.match("bar"));



		// + include all, but one (no alt)
		inExRules.reset();
		inExRules.blacklist();
		inExRules.exclude("foo");

		assertFalse(inExRules.match("foo"));
		assertTrue(inExRules.match("fight"));
		assertTrue(inExRules.match("bar"));



		// + include all
		inExRules.reset();
		inExRules.blacklist();

		assertTrue(inExRules.match("foo"));
		assertTrue(inExRules.match("fight"));

		// + include all (alt)
		inExRules.reset();
		inExRules.whitelist();
		inExRules.include("*");

		assertTrue(inExRules.match("foo"));
		assertTrue(inExRules.match("fight"));



		// + exclude some, but one
		inExRules.reset();
		inExRules.blacklist();
		inExRules.exclude("f*");
		inExRules.include("foo");

		assertTrue(inExRules.match("foo"));
		assertFalse(inExRules.match("fight"));
		assertTrue(inExRules.match("bar"));



		// + include only some, but one
		inExRules.reset();
		inExRules.whitelist();
		inExRules.include("f*");
		inExRules.exclude("foo");

		assertFalse(inExRules.match("foo"));
		assertTrue(inExRules.match("fight"));
		assertTrue(inExRules.match("fravia"));
		assertFalse(inExRules.match("bar"));



		// + exclude only some, but one
		inExRules.reset();
		inExRules.blacklist();
		inExRules.exclude("f*");
		inExRules.include("foo");

		assertTrue(inExRules.match("foo"));
		assertFalse(inExRules.match("fight"));
		assertFalse(inExRules.match("fravia"));
		assertTrue(inExRules.match("bar"));
	}

	@Test
	public void testSmartMode() {
		InExRules<String, String> inExRules = new InExRules<String, String>(InExRuleMatcher.WILDCARD_RULE_MATCHER);

		assertTrue(inExRules.isBlacklist());
		assertFalse(inExRules.isWhitelist());

		inExRules.include("xxx");

		inExRules.smartMode();

		assertFalse(inExRules.isBlacklist());
		assertTrue(inExRules.isWhitelist());
	}

}