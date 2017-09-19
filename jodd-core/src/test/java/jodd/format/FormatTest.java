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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FormatTest {

	@Test
	public void testByte() {
		assertEquals("0b010001", Printf.str("%#08b", 0x11));
		byte b = Byte.MAX_VALUE;
		assertEquals("127", Printf.str("%i", b));
		assertEquals("127", Printf.str("%u", b));
		assertEquals("7f", Printf.str("%x", b));
		assertEquals("7F", Printf.str("%X", b));
		assertEquals("0x7f", Printf.str("%#x", b));
		assertEquals("0X7F", Printf.str("%#X", b));
		assertEquals("1111111", Printf.str("%b", b));
		assertEquals("0b1111111", Printf.str("%#b", b));
		assertEquals("0B1111111", Printf.str("%#B", b));
		assertEquals("01111111", Printf.str("%08b", b));
		assertEquals("0b01111111", Printf.str("%#010b", b));
		assertEquals("0B01111111", Printf.str("%#010B", b));
		assertEquals("177", Printf.str("%o", b));

		b = -1;
		assertEquals("-1", Printf.str("%i", b));
		assertEquals("ff", Printf.str("%x", Integer.valueOf(-1).byteValue()));
		assertEquals("255", Printf.str("%u", b));
		assertEquals("ff", Printf.str("%x", b));
		assertEquals("FF", Printf.str("%X", b));
		assertEquals("0xff", Printf.str("%#x", b));
		assertEquals("0XFF", Printf.str("%#X", b));
		assertEquals("11111111", Printf.str("%b", b));
		assertEquals("0b11111111", Printf.str("%#b", b));
		assertEquals("0B11111111", Printf.str("%#B", b));
		assertEquals("11111111", Printf.str("%08b", b));
		assertEquals("0b11111111", Printf.str("%#08b", b));
		assertEquals("0B11111111", Printf.str("%#08B", b));
		assertEquals("377", Printf.str("%o", b));

		b = Byte.MIN_VALUE;
		assertEquals("-128", Printf.str("%i", b));
		assertEquals("128", Printf.str("%u", b));
		assertEquals("80", Printf.str("%x", b));
		assertEquals("80", Printf.str("%X", b));
		assertEquals("10000000", Printf.str("%b", b));
		assertEquals("0b10000000", Printf.str("%#b", b));
		assertEquals("0B10000000", Printf.str("%#B", b));
		assertEquals("10000000", Printf.str("%08b", b));
		assertEquals("0b10000000", Printf.str("%#08b", b));
		assertEquals("0B10000000", Printf.str("%#08B", b));
		assertEquals("200", Printf.str("%o", b));
	}

	@Test
	public void testShort() {
		short s = Short.MAX_VALUE;
		assertEquals("32767", Printf.str("%i", s));
		assertEquals("32767", Printf.str("%u", s));
		assertEquals("7fff", Printf.str("%x", s));
		assertEquals("7FFF", Printf.str("%X", s));
		assertEquals("0x7fff", Printf.str("%#x", s));
		assertEquals("0X7FFF", Printf.str("%#X", s));
		assertEquals("111111111111111", Printf.str("%b", s));
		assertEquals("0b111111111111111", Printf.str("%#b", s));
		assertEquals("0B111111111111111", Printf.str("%#B", s));
		assertEquals("77777", Printf.str("%o", s));

		s = -1;
		assertEquals("-1", Printf.str("%i", s));
		assertEquals("65535", Printf.str("%u", s));
		assertEquals("ffff", Printf.str("%x", s));
		assertEquals("FFFF", Printf.str("%X", s));
		assertEquals("0xffff", Printf.str("%#x", s));
		assertEquals("0XFFFF", Printf.str("%#X", s));
		assertEquals("1111111111111111", Printf.str("%b", s));
		assertEquals("0b1111111111111111", Printf.str("%#b", s));
		assertEquals("0B1111111111111111", Printf.str("%#B", s));
		assertEquals("177777", Printf.str("%o", s));
		assertEquals("ffff", Printf.str("%x", Integer.valueOf(-1).shortValue()));

		s = Short.MIN_VALUE;
		assertEquals("-32768", Printf.str("%i", s));
		assertEquals("32768", Printf.str("%u", s));
		assertEquals("8000", Printf.str("%x", s));
		assertEquals("8000", Printf.str("%X", s));
		assertEquals("1000000000000000", Printf.str("%b", s));
		assertEquals("0b1000000000000000", Printf.str("%#b", s));
		assertEquals("0B1000000000000000", Printf.str("%#B", s));
		assertEquals("100000", Printf.str("%o", s));
	}

	@Test
	public void testIntLong() {
		assertEquals("1", Printf.str("%d", 1));
		assertEquals("17", Printf.str("%d", 17));
		assertEquals("173", Printf.str("%d", 173));
		assertEquals("173000000", Printf.str("%d", 173000000));

		assertEquals("+173", Printf.str("%+i", 173));
		assertEquals("+173", Printf.str("%+0i", 173));
		assertEquals("173", Printf.str("%0i", 173));

		assertEquals("+173", Printf.str("%+u", 173));
		assertEquals("+173", Printf.str("%+0u", 173));
		assertEquals("173", Printf.str("%0u", 173));

		assertEquals("AD", Printf.str("%X", 173));
		assertEquals("ad", Printf.str("%+x", 173));
		assertEquals("7", Printf.str("%+0X", 7));
		assertEquals("0X7", Printf.str("%#X", 7));
		assertEquals("0x7", Printf.str("%#x", 7));
		assertEquals("0XAD", Printf.str("%#X", 173));
		assertEquals("255", Printf.str("%o", 173));

		int i = -1;
		assertEquals("-1", Printf.str("%,d", i));
		assertEquals("-1", Printf.str("%,i", i));
		assertEquals("4294967295", Printf.str("%u", i));    // 2^32 - 1
		assertEquals("4,294,967,295", Printf.str("%,u", i));
		assertEquals("ffffffff", Printf.str("%x", i));
		assertEquals("ffff ffff", Printf.str("%,x", i));

		i = 2147483647;            // 2^31 - 1 (max int)
		assertEquals(Integer.MAX_VALUE, i);
		assertEquals("2147483647", Printf.str("%d", i));
		assertEquals("2147483647", Printf.str("%i", i));
		assertEquals("2147483647", Printf.str("%u", i));
		assertEquals("2,147,483,647", Printf.str("%,d", i));
		assertEquals("2,147,483,647", Printf.str("%,i", i));
		assertEquals("2,147,483,647", Printf.str("%,u", i));
		assertEquals("7fffffff", Printf.str("%x", i));
		assertEquals("7fff ffff", Printf.str("%,x", i));

		i++;                    // -2^31 (min int)
		assertEquals(Integer.MIN_VALUE, i);
		assertEquals("-2147483648", Printf.str("%d", i));
		assertEquals("-2147483648", Printf.str("%i", i));
		assertEquals("2147483648", Printf.str("%u", i));
		assertEquals("-2,147,483,648", Printf.str("%,d", i));
		assertEquals("-2,147,483,648", Printf.str("%,i", i));
		assertEquals("2,147,483,648", Printf.str("%,u", i));
		assertEquals("80000000", Printf.str("%x", i));
		assertEquals("8000 0000", Printf.str("%,x", i));


		long l = 2147483648L;    // 2^31 (max int + 1)
		assertEquals("2147483648", Printf.str("%d", l));
		assertEquals("-2147483648", Printf.str("%i", l));
		assertEquals("2147483648", Printf.str("%u", l));
		assertEquals("2,147,483,648", Printf.str("%,d", l));
		assertEquals("-2,147,483,648", Printf.str("%,i", l));
		assertEquals("2,147,483,648", Printf.str("%,u", l));
		assertEquals("80000000", Printf.str("%x", l));
		assertEquals("8000 0000", Printf.str("%,x", l));

		l = -2147483649L;       // -2^31-1 (min int - 1)
		assertEquals("-2147483649", Printf.str("%d", l));
		assertEquals("2147483647", Printf.str("%i", l));
		assertEquals("18446744071562067967", Printf.str("%u", l));
		assertEquals("-2,147,483,649", Printf.str("%,d", l));
		assertEquals("2,147,483,647", Printf.str("%,i", l));
		assertEquals("18,446,744,071,562,067,967", Printf.str("%,u", l));
		assertEquals("FFFFFFFF7FFFFFFF", Printf.str("%X", l));
		assertEquals("FFFF FFFF 7FFF FFFF", Printf.str("%,X", l));

		l = -1;
		assertEquals("-1", Printf.str("%d", l));
		assertEquals("-1", Printf.str("%i", l));
		assertEquals("18446744073709551615", Printf.str("%u", l));
		assertEquals("18,446,744,073,709,551,615", Printf.str("%,u", l));
		assertEquals("FFFFFFFFFFFFFFFF", Printf.str("%X", l));
		assertEquals("FFFF FFFF FFFF FFFF", Printf.str("%,X", l));

		l = Long.MAX_VALUE;
		assertEquals("9223372036854775807", Printf.str("%d", l));
		assertEquals("9,223,372,036,854,775,807", Printf.str("%,d", l));
		assertEquals("-1", Printf.str("%i", l));
		assertEquals("9223372036854775807", Printf.str("%u", l));
		assertEquals("9,223,372,036,854,775,807", Printf.str("%,u", l));
		assertEquals("7FFFFFFFFFFFFFFF", Printf.str("%X", l));
		assertEquals("7FFF FFFF FFFF FFFF", Printf.str("%,X", l));

		l = Long.MIN_VALUE;
		assertEquals("-9223372036854775808", Printf.str("%d", l));
		assertEquals("-9,223,372,036,854,775,808", Printf.str("%,d", l));
		assertEquals("0", Printf.str("%i", l));
		assertEquals("9223372036854775808", Printf.str("%u", l));
		assertEquals("9,223,372,036,854,775,808", Printf.str("%,u", l));
		assertEquals("8000000000000000", Printf.str("%X", l));
		assertEquals("8000 0000 0000 0000", Printf.str("%,X", l));
	}

	@Test
	public void testChar() {
		assertEquals("A", Printf.str("%c", 'A'));
		assertEquals("c", Printf.str("%c", 'c'));
		assertEquals("65", Printf.str("%d", 'A'));
		assertEquals("41", Printf.str("%x", 'A'));
		assertEquals("101", Printf.str("%o", 'A'));
		assertEquals("0xdb00", Printf.str("%#x", '\udb00'));
		assertEquals("A", Printf.str("%c", 65));
		assertEquals("A", Printf.str("%c", (byte)65));
		assertEquals("--- A ---", Printf.str("--- %c ---", 'A'));
		assertEquals("A", Printf.str("%c", 65));
		assertEquals("A", Printf.str("%c", (byte) 65));
		assertEquals("A", Printf.str("%c", (short) 65));
		assertEquals("A", Printf.str("%c", Integer.valueOf(65)));
		assertEquals("A", Printf.str("%c", Integer.valueOf(65).byteValue()));
		assertEquals("A", Printf.str("%c", Integer.valueOf(65).shortValue()));
		assertEquals("A", Printf.str("%c", new Character('A')));
		assertEquals("65", Printf.str("%d", new Character('A')));
		assertEquals("41", Printf.str("%x", new Character('A')));
		assertEquals("101", Printf.str("%o", new Character('A')));
	}


	@Test
	public void testFormatedInt() {
		assertEquals("0001", Printf.str("%04d", 1));
		assertEquals("+001", Printf.str("%+04d", 1));
		assertEquals("-001", Printf.str("%+04d", -1));
		assertEquals("+0001", Printf.str("%+~04d", 1));
		assertEquals("0001", Printf.str("%~04d", 1));
		assertEquals("-0001", Printf.str("%+~04d", -1));

		assertEquals("   1", Printf.str("%4d", 1));
		assertEquals("  +1", Printf.str("%+4d", 1));
		assertEquals("  -1", Printf.str("%+4d", -1));
		assertEquals("+1  ", Printf.str("%-+4d", 1));
		assertEquals("-1  ", Printf.str("%-+4d", -1));

		assertEquals("1", Printf.str("%,d", 1));
		assertEquals("12", Printf.str("%,d", 12));
		assertEquals("123", Printf.str("%,d", 123));
		assertEquals("1,234", Printf.str("%,d", 1234));
		assertEquals("12,345", Printf.str("%,d", 12345));
		assertEquals("123,456", Printf.str("%,d", 123456));
	}

	@Test
	public void testStrings() {
		assertEquals("A", Printf.str("%c", 'A'));
		assertEquals("str", Printf.str("%s", "str"));
		assertEquals("% 1", Printf.str("%% %i", 1));
		assertEquals("% % % 1", Printf.str("%% %% %% %i", 1));
		assertEquals("1 %%", Printf.str("%i %%", 1));

		assertEquals("q w", Printf.str("%s %s", new String[]{"q", "w"}));
		assertEquals(" q w", Printf.str(" %s %s", new String[]{"q", "w"}));
		assertEquals("q w ", Printf.str("%s %s ", new String[]{"q", "w"}));
		assertEquals(" q w 1 2 3 ", Printf.str(" %s %s %s %s %s ", new String[]{"q", "w", "1", "2", "3"}));

		assertEquals("q%w", Printf.str("%s%%%s", new String[]{"q", "w"}));
		assertEquals("q%%", Printf.str("%s%%", new String[]{"q"}));
		assertEquals("q%% ", Printf.str("%s%% ", new String[]{"q"}));
		assertEquals("q %%", Printf.str("%s %%", new String[]{"q"}));
		assertEquals("q %% ", Printf.str("%s %% ", new String[]{"q"}));
		assertEquals("%q", Printf.str("%%%s", new String[]{"q"}));
		assertEquals("% q", Printf.str("%% %s", new String[]{"q"}));
		assertEquals("% q ", Printf.str("%% %s ", new String[]{"q"}));
		assertEquals(" % q ", Printf.str(" %% %s ", new String[]{"q"}));

		assertEquals("null", Printf.str("%s", (Object)null));
	}

	@Test
	public void testFloats() {
		assertEquals("1.700000", Printf.str("%f", 1.7));
		assertEquals("1.7", Printf.str("%1.1f", 1.7));
		assertEquals("1.7", Printf.str("%2.1f", 1.7));
		assertEquals("1.7", Printf.str("%3.1f", 1.7));
		assertEquals(" 1.7", Printf.str("%4.1f", 1.7));
		assertEquals("1.70", Printf.str("%4.2f", 1.7));
		assertEquals("1.80", Printf.str("%4.2f", 1.79999999999));
		assertEquals("0", Printf.str("%1.0f", 0.4999));
		assertEquals("1", Printf.str("%1.0f", 0.50));
		assertEquals("1", Printf.str("%1.0f", 0.51)); 
		assertEquals("1.01", Printf.str("%3.2f", 1.0051));
		assertEquals("1.01", Printf.str("%3.2f", 1.0099));
        
		assertEquals("17.3", Printf.str("%1.1f", 17.3));
		assertEquals("17.3", Printf.str("%2.1f", 17.3));
		assertEquals("17.3", Printf.str("%3.1f", 17.3));
		assertEquals("17.3", Printf.str("%4.1f", 17.3));
		assertEquals("17.30", Printf.str("%4.2f", 17.3));
		assertEquals(" 17.3", Printf.str("%5.1f", 17.3));
		assertEquals("17.30", Printf.str("%5.2f", 17.3));

		assertEquals("1.100000E+002", Printf.str("%E", 1.1e2));
		assertEquals("110", Printf.str("%G", 1.1e2));
		assertEquals("1.100000E+013", Printf.str("%E", 1.1e13));
		assertEquals("1.1E+013", Printf.str("%G", 1.1e13));
		assertEquals("1.100000e+010", Printf.str("%e", 1.1e10));
		assertEquals("1.1e+010", Printf.str("%g", 1.1e10));
		assertEquals("1.1000e+010", Printf.str("%.4e", 1.1e10));
		assertEquals("1.1e+010", Printf.str("%.4g", 1.1e10));
		assertEquals("1.0010e+012", Printf.str("%.4e", 100.1e10));
		assertEquals("1.001e+012", Printf.str("%.4g", 100.1e10));
		assertEquals("1.0010e+012", Printf.str("%6.4e", 100.1e10));
		assertEquals("1.001e+012", Printf.str("%g", 100.1e10));
	}

	@Test
	public void testBoolean() {
		assertEquals("true", Printf.str("%l", true));
		assertEquals("false", Printf.str("%l", false));
		assertEquals("TRUE", Printf.str("%L", true));
		assertEquals("FALSE", Printf.str("%L", false));
		assertEquals("true", Printf.str("%l", 123));
		assertEquals("false", Printf.str("%l", 0));
		assertEquals("TRUE", Printf.str("%L", 123));
		assertEquals("FALSE", Printf.str("%L", 0));
		assertEquals("true", Printf.str("%l", 123L));
		assertEquals("false", Printf.str("%l", 0L));
		assertEquals("TRUE", Printf.str("%L", 123L));
		assertEquals("FALSE", Printf.str("%L", 0L));
	}


	@Test
	public void testIntRanges() {
		int i;

		i = 0;
		assertEquals(Integer.toString(i), Printf.str("%d", i));
		assertEquals(Integer.toString(i), Printf.str("%i", i));
		assertEquals(Integer.toString(i), Printf.str("%x", i));
		assertEquals(Integer.toString(i), Printf.str("%u", i));
		assertEquals(Integer.toString(i), Printf.str("%o", i));
		assertEquals(Integer.toString(i), Printf.str("%b", i));

		i = 1;
		long v = 1;
		assertEquals(Integer.toString(i), Printf.str("%d", i));
		assertEquals(Integer.toString(i), Printf.str("%i", i));
		assertEquals(Long.toString(v, 16), Printf.str("%x", i));
		assertEquals(Long.toString(v), Printf.str("%u", i));
		assertEquals(Long.toString(v, 8), Printf.str("%o", i));
		assertEquals(Long.toString(v, 2), Printf.str("%b", i));

		i = Integer.MAX_VALUE;
		v = (1L << 31) - 1;
		assertEquals(Integer.toString(i), Printf.str("%d", i));
		assertEquals(Integer.toString(i), Printf.str("%i", i));
		assertEquals(Long.toString(v, 16), Printf.str("%x", i));
		assertEquals(Long.toString(v), Printf.str("%u", i));
		assertEquals(Long.toString(v, 8), Printf.str("%o", i));
		assertEquals(Long.toString(v, 2), Printf.str("%b", i));

		i = Integer.MIN_VALUE;
		v = (1L << 31);
		assertEquals(Integer.toString(i), Printf.str("%d", i));
		assertEquals(Integer.toString(i), Printf.str("%i", i));
		assertEquals(Long.toString(v, 16), Printf.str("%x", i));
		assertEquals(Long.toString(v), Printf.str("%u", i));
		assertEquals(Long.toString(v, 8), Printf.str("%o", i));
		assertEquals(Long.toString(v, 2), Printf.str("%b", i));

		i = -1;
		v = (1L << 32) - 1;
		assertEquals(Integer.toString(i), Printf.str("%d", i));
		assertEquals(Integer.toString(i), Printf.str("%i", i));
		assertEquals(Long.toString(v, 16), Printf.str("%x", i));
		assertEquals(Long.toString(v), Printf.str("%u", i));
		assertEquals(Long.toString(v, 8), Printf.str("%o", i));
		assertEquals(Long.toString(v, 2), Printf.str("%b", i));
	}

	@Test
	public void testLongRanges() {
		long l;

		l = 0;
		assertEquals(Long.toString(l), Printf.str("%d", l));
		assertEquals(Long.toString(l), Printf.str("%i", l));
		assertEquals(Long.toString(l), Printf.str("%x", l));
		assertEquals(Long.toString(l), Printf.str("%u", l));
		assertEquals(Long.toString(l), Printf.str("%o", l));
		assertEquals(Long.toString(l), Printf.str("%b", l));

		l = 1;
		assertEquals(Long.toString(l), Printf.str("%d", l));
		assertEquals(Long.toString(l), Printf.str("%i", l));
		assertEquals(Long.toString(l, 16), Printf.str("%x", l));
		assertEquals(Long.toString(l), Printf.str("%u", l));
		assertEquals(Long.toString(l, 8), Printf.str("%o", l));
		assertEquals(Long.toString(l, 2), Printf.str("%b", l));

		l = Integer.MAX_VALUE;
		long v = (1L << 31) - 1;
		assertEquals(Long.toString(l), Printf.str("%d", l));
		assertEquals(Long.toString(l), Printf.str("%i", l));
		assertEquals(Long.toString(v, 16), Printf.str("%x", l));
		assertEquals(Long.toString(v), Printf.str("%u", l));
		assertEquals(Long.toString(v, 8), Printf.str("%o", l));
		assertEquals(Long.toString(v, 2), Printf.str("%b", l));

		l = Integer.MIN_VALUE;
		assertEquals(Long.toString(l), Printf.str("%d", l));
		assertEquals(Long.toString(l), Printf.str("%i", l));
		assertEquals(Long.toHexString(l), Printf.str("%x", l));
		assertEquals("18446744071562067968", Printf.str("%u", l));
		assertEquals(Long.toOctalString(l), Printf.str("%o", l));
		assertEquals(Long.toBinaryString(l), Printf.str("%b", l));

		l = -1;
		assertEquals(Long.toString(l), Printf.str("%d", l));
		assertEquals(Long.toString(l), Printf.str("%i", l));
		assertEquals(Long.toHexString(l), Printf.str("%x", l));
		assertEquals("18446744073709551615", Printf.str("%u", l));
		assertEquals(Long.toOctalString(l), Printf.str("%o", l));
		assertEquals(Long.toBinaryString(l), Printf.str("%b", l));


		l = ((long) Integer.MAX_VALUE) + 1;
		v = (1L << 31);
		assertEquals(Long.toString(l), Printf.str("%d", l));
		assertEquals(Long.toString(Integer.MIN_VALUE), Printf.str("%i", l));
		assertEquals(Long.toString(v, 16), Printf.str("%x", l));
		assertEquals(Long.toString(v), Printf.str("%u", l));
		assertEquals(Long.toString(v, 8), Printf.str("%o", l));
		assertEquals(Long.toString(v, 2), Printf.str("%b", l));


		l = ((long) Integer.MIN_VALUE) - 1;
		assertEquals(Long.toString(l), Printf.str("%d", l));
		assertEquals(Long.toString(Integer.MAX_VALUE), Printf.str("%i", l));
		assertEquals(Long.toHexString(l), Printf.str("%x", l));
		assertEquals("18446744071562067967", Printf.str("%u", l));
		assertEquals(Long.toOctalString(l), Printf.str("%o", l));
		assertEquals(Long.toBinaryString(l), Printf.str("%b", l));

		l = Long.MAX_VALUE;
		assertEquals(Long.toString(l), Printf.str("%d", l));
		assertEquals(Long.toString(-1), Printf.str("%i", l));
		assertEquals(Long.toHexString(l), Printf.str("%x", l));
		assertEquals("9223372036854775807", Printf.str("%u", l));
		assertEquals(Long.toOctalString(l), Printf.str("%o", l));
		assertEquals(Long.toBinaryString(l), Printf.str("%b", l));


		l = Long.MIN_VALUE;
		assertEquals(Long.toString(l), Printf.str("%d", l));
		assertEquals(Long.toString(0), Printf.str("%i", l));
		assertEquals(Long.toHexString(l), Printf.str("%x", l));
		assertEquals("9223372036854775808", Printf.str("%u", l));
		assertEquals(Long.toOctalString(l), Printf.str("%o", l));
		assertEquals(Long.toBinaryString(l), Printf.str("%b", l));
	}


	@Test
	public void testBinary() {
		assertEquals("1", Printf.str("%b", 1));
		assertEquals("11", Printf.str("%b", 3));
		assertEquals("1101", Printf.str("%b", 13));
		assertEquals("11111111111111111111111111110011", Printf.str("%b", -13));
		assertEquals("11111111 11111111 11111111 11110011", Printf.str("%,b", -13));
		assertEquals("1111111111111111111111111111111111111111111111111111111111110011", Printf.str("%b", -13L));
		assertEquals("11111111 11111111 11111111 11111111 11111111 11111111 11111111 11110011", Printf.str("%,b", -13L));

		assertEquals("0b1", Printf.str("%#b", 1));
		assertEquals("0b11", Printf.str("%#b", 3));
		assertEquals("0b1101", Printf.str("%#b", 13));
		assertEquals("0b11111111111111111111111111110011", Printf.str("%#b", -13));
		assertEquals("0b11111111 11111111 11111111 11110011", Printf.str("%,#b", -13));
		assertEquals("0b1111111111111111111111111111111111111111111111111111111111110011", Printf.str("%#b", -13L));
		assertEquals("0b11111111 11111111 11111111 11111111 11111111 11111111 11111111 11110011", Printf.str("%,#b", -13L));

		assertEquals("0B1", Printf.str("%#B", 1));
		assertEquals("0B11", Printf.str("%#B", 3));
		assertEquals("0B1101", Printf.str("%#B", 13));
		assertEquals("0B11111111111111111111111111110011", Printf.str("%#B", -13));
		assertEquals("0B11111111 11111111 11111111 11110011", Printf.str("%,#B", -13));
		assertEquals("0B1111111111111111111111111111111111111111111111111111111111110011", Printf.str("%#B", -13L));
		assertEquals("0B11111111 11111111 11111111 11111111 11111111 11111111 11111111 11110011", Printf.str("%,#B", -13L));
	}

	@Test
	public void testSuccessive() {
		String fmt = "...%i...%i...";
		PrintfFormat pf = new PrintfFormat();
		fmt = pf.reinit(fmt).form(1);
		fmt = pf.reinit(fmt).form(2);
		assertEquals("...1...2...", fmt);
	}

	@Test
	public void testNumbers() {
		String result = Printf.str("%i %3.2f %X", Integer.valueOf(173), Double.valueOf(1.73), Long.valueOf(10));

		assertEquals("173 1.73 A", result);
	}

	@Test
	public void testDoublesRound() {
		assertEquals(Printf.str("%1.0f", 0.50), "1");
		assertEquals(Printf.str("%2.1f", 0.1499), "0.1");
		assertEquals(Printf.str("%2.1f", 0.15), "0.2");

		assertEquals(Printf.str("%1.0f", 0.51), "1");
		assertEquals(Printf.str("%2.1f", 0.1501), "0.2");
		assertEquals(Printf.str("%3.2f", 1.0099), "1.01");
	}

	@Test
	public void testObject() {
		assertTrue(Printf.str("%p", new Object()).length() > 4);
		assertTrue(Printf.str("%p", new Integer(1)).length() > 4);
	}

	@Test
	public void testMultipleObject() {
		assertEquals("173, hej true", Printf.str("%i, %s %l", 173, "hej", true));
	}

	@Test
	public void testAlternateChar() {
		assertEquals("\\u41", Printf.str("%#c", 'A'));
		assertEquals("\\u1a34", Printf.str("%#c", '\u1A34'));
		assertEquals("\\uff00", Printf.str("%#c", '\uFF00'));
		assertEquals("\\u1A34", Printf.str("%#C", '\u1A34'));
		assertEquals("\\uFF00", Printf.str("%#C", '\uFF00'));
		assertEquals("A", Printf.str("%c", 'A'));
		assertEquals("\u1234", Printf.str("%c", '\u1234'));
		assertEquals("\uFF00", Printf.str("%c", '\uFF00'));
	}

}
