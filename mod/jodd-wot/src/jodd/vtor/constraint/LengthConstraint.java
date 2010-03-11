package jodd.vtor.constraint;

import jodd.vtor.ValidationConstraint;
import jodd.vtor.ValidationConstraintContext;

public class LengthConstraint implements ValidationConstraint<Length> {

	public LengthConstraint() {
	}

	public LengthConstraint(int min, int max) {
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

	public void configure(Length annotation) {
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
		final int len = value.toString().length();
		return len >= min && len <= max;
	}
}