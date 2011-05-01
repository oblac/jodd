// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.vtor.constraint;

import jodd.typeconverter.Convert;
import jodd.vtor.ValidationConstraint;
import jodd.vtor.ValidationConstraintContext;

public class RangeConstraint implements ValidationConstraint<Range> {

	public RangeConstraint() {
	}

	public RangeConstraint(double min, double max) {
		this.min = min;
		this.max = max;
	}

	// ---------------------------------------------------------------- properties

	protected double min;
	protected double max;

	public double getMin() {
		return min;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
	}

	// ---------------------------------------------------------------- configure

	public void configure(Range annotation) {
		this.min = annotation.min();
		this.max = annotation.max();
	}

	// ---------------------------------------------------------------- valid

	public boolean isValid(ValidationConstraintContext vcc, Object value) {
		return validate(value, min, max);
	}

	public static boolean validate(Object value, double min, double max) {
		if (value == null) {
			return true;
		}
		double val = Convert.toDouble(value);
		return val >= min && val <= max;
	}

}