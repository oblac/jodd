// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.vtor.constraint;

import jodd.vtor.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(SizeConstraint.class)
public @interface Size {

	/**
	 * Max value.
	 */
	int max() default Integer.MAX_VALUE;

	/**
	 * Min value.
	 */
	int min() default 0;

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
