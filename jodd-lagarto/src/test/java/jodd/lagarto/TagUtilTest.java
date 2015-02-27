// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

import org.junit.Test;

import static jodd.util.ArraysUtil.chars;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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