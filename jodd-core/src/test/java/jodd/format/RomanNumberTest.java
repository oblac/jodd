// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.format;

import org.junit.Test;

import static jodd.format.RomanNumber.convertToArabic;
import static jodd.format.RomanNumber.convertToRoman;
import static jodd.format.RomanNumber.isValidRomanNumber;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
			fail();
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