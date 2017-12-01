package jodd.datetime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateTimeStampTest {

	@Nested
	@DisplayName("tests for DateTimeStamp#compareTo")
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	class CompareTo {

		@ParameterizedTest
		@MethodSource("testData_testCompareTo")
		void testCompareTo(final int expected, final DateTimeStamp input_1, final DateTimeStamp input_2) {
			final int actual = input_1.compareTo(input_2);

			// asserts
			assertEquals(expected, actual);
		}

		private Collection<Arguments> testData_testCompareTo () {
			final List<Arguments> params = new ArrayList<>();

			{
				params.add(Arguments.of(0, new DateTimeStamp(2017,12,12,11,56,23,11),
						new DateTimeStamp(2017,12,12,11,56,23,11)));
			}

			{
				params.add(Arguments.of(-1, new DateTimeStamp(2017,12,12,11,56,23,11),
						new DateTimeStamp(2017,12,12,11,56,23,12)));
			}

			{
				params.add(Arguments.of(1, new DateTimeStamp(2017,12,12,11,56,23,12),
						new DateTimeStamp(2017,12,12,11,56,23,11)));
			}

			return params;
		}
	}

	@Nested
	@DisplayName("tests for DateTimeStamp#equals")
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	class Equals {

		@ParameterizedTest
		@MethodSource("testData_testEquals")
		void testEquals(final boolean expected, final DateTimeStamp input_1, final Object input_2) {
			final boolean actual = input_1.equals(input_2);

			// asserts
			assertEquals(expected, actual);
		}

		private Collection<Arguments> testData_testEquals () {
			final List<Arguments> params = new ArrayList<>();

			{
				params.add(Arguments.of(true, new DateTimeStamp(2017,12,12,11,56,23,11),
						new DateTimeStamp(2017,12,12,11,56,23,11)));
			}

			{
				final DateTimeStamp dateTimeStamp = new DateTimeStamp(2017, 12, 12, 11, 56, 23, 11);
				params.add(Arguments.of(true, dateTimeStamp, dateTimeStamp));
			}

			{
				params.add(Arguments.of(false, new DateTimeStamp(2017,12,12,11,56,23,12),
						null));
			}

			{
				params.add(Arguments.of(false, new DateTimeStamp(2017,12,12,11,56,23,12),
						new Date()));
			}

			{
				params.add(Arguments.of(false, new DateTimeStamp(2017,12,12,11,56,23,12),
						new DateTimeStamp(2017,12,12,11,56,23,13)));
			}

			return params;
		}
	}
}