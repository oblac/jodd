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

package jodd.format;

import org.junit.jupiter.api.Test;

import static jodd.format.RomanNumber.convertToArabic;
import static jodd.format.RomanNumber.convertToRoman;
import static jodd.format.RomanNumber.isValidRomanNumber;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class RomanNumberTest {

	@Test
	public void testConvertToRoman() {
		assertEquals(convertToRoman(1), "I");
		assertEquals(convertToRoman(2), "II");
		assertEquals(convertToRoman(3), "III");
		assertEquals(convertToRoman(4), "IV");
		assertEquals(convertToRoman(5), "V");
		assertEquals(convertToRoman(8), "VIII");
		assertEquals(convertToRoman(10), "X");
		assertEquals(convertToRoman(11), "XI");
		assertEquals(convertToRoman(20), "XX");
		assertEquals(convertToRoman(27), "XXVII");
		assertEquals(convertToRoman(33), "XXXIII");
		assertEquals(convertToRoman(2499), "MMCDXCIX");
		assertEquals(convertToRoman(3949), "MMMCMXLIX");

		try {
			convertToRoman(-1);
			fail("error");
		} catch (Exception ignore) {
		}
	}

	@Test
	public void testConvertToArabic() {
		assertEquals(convertToArabic("I"), 1);
		assertEquals(convertToArabic("II"), 2);
		assertEquals(convertToArabic("III"), 3);
		assertEquals(convertToArabic("IV"), 4);
		assertEquals(convertToArabic("V"), 5);
		assertEquals(convertToArabic("VIII"), 8);
		assertEquals(convertToArabic("X"), 10);
		assertEquals(convertToArabic("XI"), 11);
		assertEquals(convertToArabic("XX"), 20);
		assertEquals(convertToArabic("XXVII"), 27);
		assertEquals(convertToArabic("XXXIII"), 33);
		assertEquals(convertToArabic("MMCDXCIX"), 2499);
		assertEquals(convertToArabic("MMMCMXLIX"), 3949);
	}

	@Test
	public void testIsValidRomanNumber() {
		assertTrue(isValidRomanNumber("I"));
		assertTrue(isValidRomanNumber("IV"));
		assertTrue(isValidRomanNumber("V"));
		assertTrue(isValidRomanNumber("VIII"));
		assertTrue(isValidRomanNumber("X"));
		assertTrue(isValidRomanNumber("XI"));
		assertTrue(isValidRomanNumber("XX"));
		assertTrue(isValidRomanNumber("XXVII"));
		assertTrue(isValidRomanNumber("MMCDXCIX"));
		assertTrue(isValidRomanNumber("MMMCMXLIX"));

		assertFalse(isValidRomanNumber("i"));
		assertFalse(isValidRomanNumber("IIMMMCMXLIX"));
		assertFalse(isValidRomanNumber("roman"));
	}
}
