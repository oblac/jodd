// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.format;

import junit.framework.TestCase;

public class FormatTest extends TestCase {

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
		assertEquals("4294967295", Printf.str("%u", i));	// 2^32 - 1
		assertEquals("4,294,967,295", Printf.str("%,u", i));
		assertEquals("ffffffff", Printf.str("%x", i));
		assertEquals("ffff ffff", Printf.str("%,x", i));

		i =	2147483647;			// 2^31 - 1 (max int)
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




		long l = 2147483648L;	// 2^31 (max int + 1)
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

	public void testChar() {
		assertEquals("A", Printf.str("%c", 'A'));
		assertEquals("--- A ---", Printf.str("--- %c ---", 'A'));
	}


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
	}

	public void testFloats() {
		assertEquals("1.700000", Printf.str("%f", 1.7));
		assertEquals("1.7", Printf.str("%1.1f", 1.7));
		assertEquals("1.7", Printf.str("%2.1f", 1.7));
		assertEquals("1.7", Printf.str("%3.1f", 1.7));
		assertEquals(" 1.7", Printf.str("%4.1f", 1.7));
		assertEquals("1.70", Printf.str("%4.2f", 1.7));
		assertEquals("1.79", Printf.str("%4.2f", 1.79999999999));

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


	public void testIntRanges() {
		int i;

		i = 0;
		assertEquals(Integer.toString(i), Printf.str("%d", i));
		assertEquals(Integer.toString(i), Printf.str("%i", i));
		assertEquals(Integer.toString(i), Printf.str("%x", i));
		assertEquals(Integer.toString(i), Printf.str("%u", i));
		assertEquals(Integer.toString(i), Printf.str("%o", i));
		assertEquals(Integer.toString(i), Printf.str("%b", i));

		i = 1; long v = 1;
		assertEquals(Integer.toString(i), Printf.str("%d", i));
		assertEquals(Integer.toString(i), Printf.str("%i", i));
		assertEquals(Long.toString(v, 16), Printf.str("%x", i));
		assertEquals(Long.toString(v), Printf.str("%u", i));
		assertEquals(Long.toString(v, 8), Printf.str("%o", i));
		assertEquals(Long.toString(v, 2), Printf.str("%b", i));

		i = Integer.MAX_VALUE; v = (1L << 31) - 1;
		assertEquals(Integer.toString(i), Printf.str("%d", i));
		assertEquals(Integer.toString(i), Printf.str("%i", i));
		assertEquals(Long.toString(v, 16), Printf.str("%x", i));
		assertEquals(Long.toString(v), Printf.str("%u", i));
		assertEquals(Long.toString(v, 8), Printf.str("%o", i));
		assertEquals(Long.toString(v, 2), Printf.str("%b", i));

		i = Integer.MIN_VALUE; v = (1L << 31);
		assertEquals(Integer.toString(i), Printf.str("%d", i));
		assertEquals(Integer.toString(i), Printf.str("%i", i));
		assertEquals(Long.toString(v, 16), Printf.str("%x", i));
		assertEquals(Long.toString(v), Printf.str("%u", i));
		assertEquals(Long.toString(v, 8), Printf.str("%o", i));
		assertEquals(Long.toString(v, 2), Printf.str("%b", i));

		i = -1; v = (1L << 32) - 1;
		assertEquals(Integer.toString(i), Printf.str("%d", i));
		assertEquals(Integer.toString(i), Printf.str("%i", i));
		assertEquals(Long.toString(v, 16), Printf.str("%x", i));
		assertEquals(Long.toString(v), Printf.str("%u", i));
		assertEquals(Long.toString(v, 8), Printf.str("%o", i));
		assertEquals(Long.toString(v, 2), Printf.str("%b", i));
	}

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

		l = Integer.MAX_VALUE; long v = (1L << 31) - 1;
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


		l = ((long) Integer.MAX_VALUE) + 1; v = (1L << 31);
		assertEquals(Long.toString(l), Printf.str("%d", l));
		assertEquals(Long.toString(Integer.MIN_VALUE), Printf.str("%i", l));
		assertEquals(Long.toString(v, 16), Printf.str("%x", l));
		assertEquals(Long.toString(v), Printf.str("%u", l));
		assertEquals(Long.toString(v, 8), Printf.str("%o", l));
		assertEquals(Long.toString(v, 2), Printf.str("%b", l));


		l = ((long)Integer.MIN_VALUE) - 1;
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



	public void testBinary() {
		assertEquals("1", Printf.str("%b", 1));
		assertEquals("11", Printf.str("%b", 3));
		assertEquals("1101", Printf.str("%b", 13));
		assertEquals("11111111111111111111111111110011", Printf.str("%b", -13));
		assertEquals("11111111 11111111 11111111 11110011", Printf.str("%,b", -13));
		assertEquals("1111111111111111111111111111111111111111111111111111111111110011", Printf.str("%b", -13L));
		assertEquals("11111111 11111111 11111111 11111111 11111111 11111111 11111111 11110011", Printf.str("%,b", -13L));
	}

	public void testSuccessive() {
		String fmt = "...%i...%i...";
		PrintfFormat pf = new PrintfFormat();
		fmt = pf.reinit(fmt).form(1);
		fmt = pf.reinit(fmt).form(2);
		assertEquals("...1...2...", fmt);
	}

}
