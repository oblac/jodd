package jodd.vtor.constraint;

import jodd.vtor.ValidationConstraint;
import jodd.vtor.ValidationConstraintContext;

public class NotNullConstraint implements ValidationConstraint<NotNull> {

	// ---------------------------------------------------------------- configure

	public void configure(NotNull annotation) {
	}

	// ---------------------------------------------------------------- valid

	public boolean isValid(ValidationConstraintContext vcc, Object value) {
		return validate(value);
	}

	public static boolean validate(Object value) {
		return value != null;
	}
}
