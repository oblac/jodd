// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.vtor.constraint;

import jodd.typeconverter.Convert;
import jodd.vtor.ValidationConstraint;
import jodd.vtor.ValidationConstraintContext;

public class AssertFalseConstraint implements ValidationConstraint<AssertFalse> {

	// ---------------------------------------------------------------- configure

	public void configure(AssertFalse annotation) {
	}

	// ---------------------------------------------------------------- valid

	public boolean isValid(ValidationConstraintContext vcc, Object value) {
		return validate(value);
	}

	public static boolean validate(Object value) {
		return Convert.toBooleanValue(value, false) == false;
	}

}