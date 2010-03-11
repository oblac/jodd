package jodd.vtor.constraint;

import jodd.util.StringUtil;
import jodd.vtor.ValidationConstraint;
import jodd.vtor.ValidationConstraintContext;

public class NotBlankConstraint implements ValidationConstraint<NotBlank> {

	// ---------------------------------------------------------------- configure

	public void configure(NotBlank annotation) {
	}

	// ---------------------------------------------------------------- valid

	public boolean isValid(ValidationConstraintContext vcc, Object value) {
		return validate(value);
	}

	public static boolean validate(Object value) {
		if (value == null) {
			return true;
		}
		return StringUtil.isNotBlank(value.toString());
	}
}
