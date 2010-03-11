package jodd.joy.vtor.constraint;

import jodd.mail.EmailAddress;
import jodd.vtor.ValidationConstraint;
import jodd.vtor.ValidationConstraintContext;

public class EmailConstraint implements ValidationConstraint<Email> {

	public void configure(Email annotation) {
	}

	public boolean isValid(ValidationConstraintContext vcc, Object value) {
		if (value == null) {
			return true;
		}
		return EmailAddress.isValidText(value.toString());
	}
}
