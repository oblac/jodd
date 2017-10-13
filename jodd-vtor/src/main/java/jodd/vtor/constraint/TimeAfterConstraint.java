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

import jodd.datetime.JDateTime;
import jodd.typeconverter.Convert;
import jodd.vtor.ValidationConstraint;
import jodd.vtor.ValidationConstraintContext;

public class TimeAfterConstraint implements ValidationConstraint<TimeAfter> {

	public TimeAfterConstraint() {
	}

	public TimeAfterConstraint(JDateTime time) {
		this.time = time;
	}

	// ---------------------------------------------------------------- properties

	protected JDateTime time;

	public JDateTime getTime() {
		return time;
	}

	public void setTime(JDateTime time) {
		this.time = time;
	}

	// ---------------------------------------------------------------- configure

	public void configure(TimeAfter annotation) {
		time = new JDateTime(annotation.value());
	}

	// ---------------------------------------------------------------- validate

	public boolean isValid(ValidationConstraintContext vcc, Object value) {
		return validate(value, time);
	}


	public static boolean validate(Object value, JDateTime then) {
		if (value == null) {
			return true;
		}
		JDateTime now = Convert.toJDateTime(value);
		return now.isAfter(then);
	}
}
