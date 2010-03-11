// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.vtor.constraint;

import jodd.util.ReflectUtil;
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
		if (value == null) {
			return true;
		}
		return ReflectUtil.castType(value, Boolean.class).booleanValue() == false;
	}

}