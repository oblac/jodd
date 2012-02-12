// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.vtor.constraint;

import jodd.typeconverter.Convert;
import jodd.vtor.ValidationConstraint;
import jodd.vtor.ValidationConstraintContext;

public class MaxConstraint implements ValidationConstraint<Max> {

	public MaxConstraint() {
	}

	public MaxConstraint(double max) {
		this.max = max;
	}

	// ---------------------------------------------------------------- properties

	protected double max;

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
	}

	// ---------------------------------------------------------------- configure

	public void configure(Max annotation) {
		this.max = annotation.value();
	}

	// ---------------------------------------------------------------- valid

	public boolean isValid(ValidationConstraintContext vcc, Object value) {
		return validate(value, max);
	}

	public static boolean validate(Object value, double max) {
		if (value == null) {
			return true;
		}
		double val = Convert.toDoubleValue(value);
		return val < max;
	}

}