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

import jodd.vtor.ValidationConstraint;
import jodd.vtor.ValidationConstraintContext;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

public class SizeConstraint implements ValidationConstraint<Size> {

	public SizeConstraint() {
	}

	public SizeConstraint(int min, int max) {
		this.min = min;
		this.max = max;
	}

	// ---------------------------------------------------------------- properties

	protected int min;
	protected int max;

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	// ---------------------------------------------------------------- configure

	public void configure(Size annotation) {
		this.min = annotation.min();
		this.max = annotation.max();
	}

	// ---------------------------------------------------------------- valid

	public boolean isValid(ValidationConstraintContext vcc, Object value) {
		return validate(value, min, max);
	}

	public static boolean validate(Object value, int min, int max) {
		if (value == null) {
			return true;
		}
		if (value instanceof Collection) {
			final int size = ((Collection<?>) value).size();
			return size >= min && size <= max;
		}
		if (value instanceof Map) {
			final int size = ((Map<?, ?>) value).size();
			return size >= min && size <= max;
		}
		if (value.getClass().isArray()) {
			final int size = Array.getLength(value);
			return size >= min && size <= max;
		}
		return false;
	}

}
