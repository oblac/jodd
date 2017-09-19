// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InExRulesTest {

	@Test
	public void testIncludeExcludes() {
		InExRules<String, String> inExRules = new InExRules<>(InExRuleMatcher.WILDCARD_RULE_MATCHER);

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
		InExRules<String, String> inExRules = new InExRules<>(InExRuleMatcher.WILDCARD_RULE_MATCHER);

		assertTrue(inExRules.isBlacklist());
		assertFalse(inExRules.isWhitelist());

		inExRules.include("xxx");

		inExRules.smartMode();

		assertFalse(inExRules.isBlacklist());
		assertTrue(inExRules.isWhitelist());
	}

}
