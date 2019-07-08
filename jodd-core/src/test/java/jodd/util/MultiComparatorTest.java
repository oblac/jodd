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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for {@link MultiComparator}.
 */
class MultiComparatorTest {

	@Test
	void testCompare_with_null_list() {
		// asserts
		assertThrows(NullPointerException.class, () -> new MultiComparator<>(null).compare(new Object(), new Object()));
	}

	@Test
	void testCompare_with_null_entry_in_list() {

		final List<Comparator<Object>> comparators = new ArrayList<>();
		comparators.add((o1, o2) -> 0);
		comparators.add(null);

		// asserts
		assertThrows(NullPointerException.class, () -> new MultiComparator<>(comparators).compare(new Object(), new Object()));
	}

	@Test
	void testCompare_with_empty_in_list() {

		final List<Comparator<Object>> comparators = new ArrayList<>();

		// asserts
		assertEquals(0, new MultiComparator<>(comparators).compare(new Object(), new Object()));
	}

	@Test
	void testCompare_with_expected_negative_value() {

		final List<Comparator<Object>> comparators = new ArrayList<>();
		Stream.of(0, 0, 0).forEach(c -> comparators.add((o1, o2) -> c));
		comparators.add((o1, o2) -> -77);

		// asserts
		assertEquals(-77, new MultiComparator<>(comparators).compare(new Object(), new Object()));
	}

	@Test
	void testCompare_with_expected_positive_value() {

		final List<Comparator<Object>> comparators = new ArrayList<>();
		Stream.of(0, 0, 0).forEach(c -> comparators.add((o1, o2) -> c));
		comparators.add((o1, o2) -> 23);

		// asserts
		assertEquals(23, new MultiComparator<>(comparators).compare(new Object(), new Object()));
	}

}