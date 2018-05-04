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

package jodd.vtor.constraint;

import jodd.typeconverter.TypeConverterManager;
import jodd.typeconverter.impl.LocalDateTimeConverter;
import jodd.vtor.ValidationConstraint;
import jodd.vtor.ValidationConstraintContext;

import java.time.LocalDateTime;

public class TimeBeforeConstraint implements ValidationConstraint<TimeBefore> {

	public TimeBeforeConstraint() {
	}

	public TimeBeforeConstraint(final LocalDateTime time) {
		this.time = time;
	}

	// ---------------------------------------------------------------- properties

	protected LocalDateTime time;

	public LocalDateTime getTime() {
		return time;
	}

	public void setTime(final LocalDateTime time) {
		this.time = time;
	}

	// ---------------------------------------------------------------- configure

	@Override
	public void configure(final TimeBefore annotation) {
		time = LocalDateTime.parse(annotation.value());
	}

	// ---------------------------------------------------------------- validate

	@Override
	public boolean isValid(final ValidationConstraintContext vcc, final Object value) {
		return validate(value, time);
	}

	public static boolean validate(final Object value, final LocalDateTime then) {
		if (value == null) {
			return true;
		}
		LocalDateTimeConverter ldtc = (LocalDateTimeConverter) TypeConverterManager.get().lookup(LocalDateTime.class);
		LocalDateTime now = ldtc.convert(value);
		return now.isBefore(then);
	}
}
