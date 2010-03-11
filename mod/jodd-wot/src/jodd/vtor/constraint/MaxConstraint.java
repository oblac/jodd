package jodd.vtor.constraint;

import jodd.util.ReflectUtil;
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

		Double val;
		try {
			val = ReflectUtil.castType(value, Double.class);
		} catch (ClassCastException ignore) {
			return false;
		}
		return val.doubleValue() < max;
	}

}