// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

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
