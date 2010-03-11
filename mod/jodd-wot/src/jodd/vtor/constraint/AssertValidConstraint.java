package jodd.vtor.constraint;

import jodd.vtor.ValidationConstraint;
import jodd.vtor.ValidationConstraintContext;
import jodd.vtor.ValidationContext;

/**
 * Special annotation that validates inner context of provided value. 
 */
public class AssertValidConstraint implements ValidationConstraint<AssertValid> {

	protected final ValidationContext targetValidationContext;

	public AssertValidConstraint(ValidationContext targetValidationContext) {
		this.targetValidationContext = targetValidationContext;
	}

	// ---------------------------------------------------------------- config

	public void configure(AssertValid annotation) {
	}

	// ---------------------------------------------------------------- valid

	/**
	 * Invokes validation on inner context. Always returns <code>true</code> since
	 * inner context violations will be appended to provided validator.
	 */
	public boolean isValid(ValidationConstraintContext vcc, Object value) {
		if (value == null) {
			return true;
		}
		vcc.validateWithin(targetValidationContext, value);
		return true;
	}
}
