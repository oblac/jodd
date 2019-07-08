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

package jodd.typeconverter;

import jodd.typeconverter.impl.LocalDateConverter;
import jodd.time.TimeUtil;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LocalDateConverterTest {
	@Test
	void testConversion() {
		LocalDateConverter c = new LocalDateConverter();

		assertNull(c.convert(null));

		final LocalDateTime localDateTime = LocalDateTime.of(2018, 4, 11, 9, 11, 23);
		final LocalDate localDate = localDateTime.toLocalDate();
		final LocalTime localTime = localDateTime.toLocalTime();

		assertEquals(localDate, c.convert(localDate));
		assertEquals(localDate, c.convert(new GregorianCalendar(2018, 3, 11, 9, 11, 23)));
		assertEquals(localDate, c.convert(new Timestamp(118, 3, 11, 9, 11, 23, 0)));
		assertEquals(localDate, c.convert(new Date(118, 3, 11, 9, 11, 23)));
		assertEquals(localDate, c.convert("2018-04-11"));

		assertThrows(TypeConversionException.class, () -> c.convert(localTime));

		final long miliseconds = TimeUtil.toMilliseconds(localDateTime);
		assertEquals(localDate, c.convert(miliseconds));
		assertEquals(localDate, c.convert("" + miliseconds));
	}

}
