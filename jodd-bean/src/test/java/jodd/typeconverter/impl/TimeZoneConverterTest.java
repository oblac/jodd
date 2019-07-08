package jodd.typeconverter.impl;

import org.junit.jupiter.api.Test;

import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;

class TimeZoneConverterTest {

	private final TimeZoneConverter timeZoneConverter = new TimeZoneConverter();

	@Test
	void testConvert_with_null_input() {
		assertNull(timeZoneConverter.convert(null));
	}

	@Test
	void testConvert_with_timezone_input() {
		assertNotNull(timeZoneConverter.convert(TimeZone.getDefault()));
	}

	@Test
	void testConvert_with_other_input() {
		assertNotNull(timeZoneConverter.convert("Europe/Berlin"));
	}
}