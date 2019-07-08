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

package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;
import jodd.util.StringUtil;
import jodd.time.TimeUtil;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

public class LocalDateConverter implements TypeConverter<LocalDate> {
	@Override
	public LocalDate convert(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof LocalDateTime) {
			return ((LocalDateTime)value).toLocalDate();
		}
		if (value instanceof Calendar) {
			return TimeUtil.fromCalendar((Calendar) value).toLocalDate();
		}
		if (value instanceof Timestamp) {
			return TimeUtil.fromMilliseconds(((Timestamp)value).getTime()).toLocalDate();
		}
		if (value instanceof Date) {
			return TimeUtil.fromDate((Date) value).toLocalDate();
		}
		if (value instanceof Number) {
			return TimeUtil.fromMilliseconds(((Number)value).longValue()).toLocalDate();
		}
		if (value instanceof LocalTime) {
			throw new TypeConversionException("Can't convert to date just from time: " + value);
		}

		String stringValue = value.toString().trim();

		if (!StringUtil.containsOnlyDigits(stringValue)) {
			// try to parse default string format
			return LocalDate.parse(stringValue);
		}

		try {
			return TimeUtil.fromMilliseconds(Long.parseLong(stringValue)).toLocalDate();
		} catch (NumberFormatException nfex) {
			throw new TypeConversionException(value, nfex);
		}

	}
}
