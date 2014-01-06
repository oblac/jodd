// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.datetime.format;

import jodd.datetime.DateTimeStamp;
import jodd.datetime.JDateTime;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ParserTest {

	@Test
	public void testParseWithDelimiters() {
		Iso8601JdtFormatter formatter = new Iso8601JdtFormatter();

		DateTimeStamp dateTimeStamp = formatter.parse("1234-11-12", "YYYY-MM-DD");
		assertEquals(new DateTimeStamp(1234, 11, 12), dateTimeStamp);
	}

	@Test
	public void testParseWithoutDelimiters() {
		Iso8601JdtFormatter formatter = new Iso8601JdtFormatter();

		DateTimeStamp dateTimeStamp = formatter.parse("12341112", "YYYYMMDD");
		assertEquals(new DateTimeStamp(1234, 11, 12), dateTimeStamp);


		String timePattern = "YYYYMMDDhhmmssmss";

		JDateTime jdt = new JDateTime();

		String format = jdt.toString(timePattern);

		JDateTime jdt1 = new JDateTime();

		jdt1.parse(format, timePattern);

		assertEquals(jdt, jdt1);

	}
}
