// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.vtor.constraint;

import jodd.vtor.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker for to validate the target.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(AssertValidConstraint.class)
public @interface AssertValid {

	// ---------------------------------------------------------------- common

	/**
	 * Profiles.
	 */
	String[] profiles() default {};

	/**
	 * Severity.
	 */
	int severity() default 0;
}
