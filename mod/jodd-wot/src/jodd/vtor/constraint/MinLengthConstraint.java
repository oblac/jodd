package jodd.vtor.constraint;

import jodd.vtor.ValidationConstraint;
import jodd.vtor.ValidationConstraintContext;

public class MinLengthConstraint implements ValidationConstraint<MinLength> {

	public MinLengthConstraint() {
	}

	public MinLengthConstraint(int min) {
		this.min = min;
	}

	// ---------------------------------------------------------------- properties

	protected int min;

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	// ---------------------------------------------------------------- configure

	public void configure(MinLength annotation) {
		this.min = annotation.value();
	}

	// ---------------------------------------------------------------- valid

	public boolean isValid(ValidationConstraintContext vcc, Object value) {
		return validate(value, min);
	}

	public static boolean validate(Object value, int min) {
		if (value == null) {
			return true;
		}
		return value.toString().length() >= min;
	}

}
