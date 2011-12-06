// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.vtor.constraint;

import jodd.vtor.ValidationConstraint;
import jodd.vtor.ValidationConstraintContext;

public class MaxLengthConstraint implements ValidationConstraint<MaxLength> {

	public MaxLengthConstraint() {
	}

	public MaxLengthConstraint(int max) {
		this.max = max;
	}

	// ---------------------------------------------------------------- properties

	protected int max;

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}
	
	// ---------------------------------------------------------------- configure

	public void configure(MaxLength annotation) {
		this.max = annotation.value();
	}

	// ---------------------------------------------------------------- valid

	public boolean isValid(ValidationConstraintContext vcc, Object value) {
		return validate(value, max);
	}

	public static boolean validate(Object value, int max) {
		if (value == null) {
			return true;
		}
		return value.toString().length() <= max;
	}
}

