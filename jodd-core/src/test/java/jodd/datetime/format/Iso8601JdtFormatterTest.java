package jodd.datetime.format;

import jodd.datetime.DateTimeStamp;
import jodd.datetime.JDateTime;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Iso8601JdtFormatterTest {

	@Test
	public void testFindPattern() {
		Iso8601JdtFormatter formatter = new Iso8601JdtFormatter();

		assertTrue(formatter.findPattern("YYYY".toCharArray(), 0) > -1);
		assertFalse(formatter.findPattern("YYY".toCharArray(), 0) > -1);
		assertFalse(formatter.findPattern(" YYYY".toCharArray(), 0) > -1);
		assertTrue(formatter.findPattern(" YYYY".toCharArray(), 1) > -1);

		assertArrayEquals(formatter.patterns[formatter.findPattern("DDD".toCharArray(), 0)], "DDD".toCharArray());
		assertArrayEquals(formatter.patterns[formatter.findPattern("DD".toCharArray(), 0)], "DD".toCharArray());
		assertArrayEquals(formatter.patterns[formatter.findPattern("D".toCharArray(), 0)], "D".toCharArray());
	}

	@Test
	public void testParseWithDelimiters() {
		Iso8601JdtFormatter formatter = new Iso8601JdtFormatter();

		assertEquals(new DateTimeStamp(123, 1, 2), formatter.parse("123-1-2", "YYYY-MM-DD"));
		assertEquals(new DateTimeStamp(123, 11, 12), formatter.parse("123-11-12", "YYYY-MM-DD"));
		assertEquals(new DateTimeStamp(1234, 11, 12), formatter.parse("1234-11-12", "YYYY-MM-DD"));
		assertEquals(new DateTimeStamp(12345, 11, 12), formatter.parse("12345-11-12", "YYYY-MM-DD"));
		assertEquals(new DateTimeStamp(12345, 11, 12), formatter.parse("12345 - 11 - 12", "YYYY-MM-DD"));
	}

	@Test
	public void testParseWithoutDelimiters() {
		Iso8601JdtFormatter formatter = new Iso8601JdtFormatter();

		assertEquals(new DateTimeStamp(123, 1, 2), formatter.parse("01230102", "YYYYMMDD"));
		assertEquals(new DateTimeStamp(1234, 11, 12), formatter.parse("12341112", "YYYYMMDD"));
	}


	@Test
	public void testParseBackAndFort() {
		String timePattern = "YYYYMMDDhhmmssmss";

		JDateTime jdt = new JDateTime();

		String format = jdt.toString(timePattern);

		assertEquals(jdt, new JDateTime().parse(format, timePattern));
	}


	@Test
	public void testParse_423() {
		JDateTime jdt;

		jdt = new JDateTime("20170808 100808", "YYYYMMDD hhmmss");

		assertEquals(2017, jdt.getYear());
		assertEquals(8, jdt.getMonth());

		jdt = new JDateTime("20170808_100808", "YYYYMMDD_hhmmss");

		assertEquals(2017, jdt.getYear());
		assertEquals(8, jdt.getMonth());
	}


}
