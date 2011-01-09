// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.vtor.constraint;

import jodd.util.ReflectUtil;
import jodd.vtor.ValidationConstraint;
import jodd.vtor.ValidationConstraintContext;

public class MinConstraint implements ValidationConstraint<Min> {

	public MinConstraint() {
	}

	public MinConstraint(double min) {
		this.min = min;
	}

	// ---------------------------------------------------------------- properties

	protected double min;

	public double getMin() {
		return min;
	}

	public void setMin(double min) {
		this.min = min;
	}


	// ---------------------------------------------------------------- configure

	public void configure(Min annotation) {
		this.min = annotation.value();
	}

	// ---------------------------------------------------------------- valid

	public boolean isValid(ValidationConstraintContext vcc, Object value) {
		return validate(value, min);
	}

	public static boolean validate(Object value, double min) {
		if (value == null) {
			return true;
		}
		Double val;
		try {
			val = ReflectUtil.castType(value, Double.class);
		} catch (ClassCastException ignore) {
			return false;
		}
		return val.doubleValue() > min;
	}
}