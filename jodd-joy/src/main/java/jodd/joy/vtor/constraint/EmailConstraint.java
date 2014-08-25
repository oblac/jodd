// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.vtor.constraint;

import jodd.mail.EmailAddress;
import jodd.vtor.ValidationConstraint;
import jodd.vtor.ValidationConstraintContext;

/**
 * Email address validator.
 */
public class EmailConstraint implements ValidationConstraint<Email> {

	public void configure(Email annotation) {
	}

	public boolean isValid(ValidationConstraintContext vcc, Object value) {
		if (value == null) {
			return true;
		}

		EmailAddress emailAddress = new EmailAddress(value.toString());

		return emailAddress.isValid();
	}
}