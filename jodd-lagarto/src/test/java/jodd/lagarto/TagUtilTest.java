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

package jodd.lagarto;

import org.junit.jupiter.api.Test;

import static jodd.util.ArraysUtil.chars;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TagUtilTest {

	@Test
	public void testEqualsChars() {
		assertTrue(TagUtil.equals(chars('a', 'b'), chars('a', 'b')));
		assertFalse(TagUtil.equals(chars('A', 'B'), chars('a', 'b')));
		assertTrue(TagUtil.equals(chars(), chars()));
		assertFalse(TagUtil.equals(chars('a'), chars('a', 'b')));
		assertFalse(TagUtil.equals(chars('a', 'b'), chars('a')));
	}

	@Test
	public void testEqualsSeqChars() {
		assertTrue(TagUtil.equals("ab", chars('a', 'b')));
		assertFalse(TagUtil.equals("AB", chars('a', 'b')));
		assertTrue(TagUtil.equals("", chars()));
		assertFalse(TagUtil.equals("a", chars('a', 'b')));
		assertFalse(TagUtil.equals("ab", chars('a')));
	}

	@Test
	public void testEqualsSeq() {
		assertTrue(TagUtil.equals("ab", "ab"));
		assertFalse(TagUtil.equals("AB", "ab"));
		assertTrue(TagUtil.equals("", ""));
		assertFalse(TagUtil.equals("a", "ab"));
		assertFalse(TagUtil.equals("ab", "a"));
	}

	@Test
	public void testEqualsCharsToLowercase() {
		assertTrue(TagUtil.equalsToLowercase("ab", chars('a', 'b')));
		assertTrue(TagUtil.equalsToLowercase("AB", chars('a', 'b')));
		assertTrue(TagUtil.equalsToLowercase("", chars()));
		assertFalse(TagUtil.equalsToLowercase("a", chars('a', 'b')));
		assertFalse(TagUtil.equalsToLowercase("ab", chars('a')));
	}

	@Test
	public void testEqualsSeqsToLowercase() {
		assertTrue(TagUtil.equalsToLowercase("ab", "ab"));
		assertTrue(TagUtil.equalsToLowercase("AB", "ab"));
		assertTrue(TagUtil.equalsToLowercase("", ""));
		assertFalse(TagUtil.equalsToLowercase("a", "ab"));
		assertFalse(TagUtil.equalsToLowercase("ab", "a"));
	}

	@Test
	public void testStartsWithLowercase() {
		assertTrue(TagUtil.startsWithLowercase("ab", chars('a', 'b')));
		assertTrue(TagUtil.startsWithLowercase("AB", chars('a', 'b')));
		assertTrue(TagUtil.startsWithLowercase("", chars()));
		assertFalse(TagUtil.startsWithLowercase("a", chars('a', 'b')));
		assertTrue(TagUtil.startsWithLowercase("ab", chars('a')));
	}

	@Test
	public void testEqualsSeqCharsIgnoreCase() {
		assertTrue(TagUtil.equalsIgnoreCase("ab", chars('a', 'b')));
		assertTrue(TagUtil.equalsIgnoreCase("AB", chars('a', 'b')));
		assertTrue(TagUtil.equalsIgnoreCase("", chars()));
		assertFalse(TagUtil.equalsIgnoreCase("a", chars('a', 'b')));
		assertFalse(TagUtil.equalsIgnoreCase("ab", chars('a')));
	}

	@Test
	public void testEqualsSeqIgnoreCase() {
		assertTrue(TagUtil.equalsIgnoreCase("ab", "ab"));
		assertTrue(TagUtil.equalsIgnoreCase("AB", "ab"));
		assertTrue(TagUtil.equalsIgnoreCase("", ""));
		assertFalse(TagUtil.equalsIgnoreCase("a", "ab"));
		assertFalse(TagUtil.equalsIgnoreCase("ab", "a"));
	}

}
