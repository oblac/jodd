package jodd.vtor.constraint;

import jodd.vtor.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(TimeBeforeConstraint.class)
public @interface ValidURL {

	// ---------------------------------------------------------------- common

	/**
	 * Profiles.
	 */
	String[] profiles() default {};

	/**
	 * Severity.
	 */
	int severity() default 0;

	/**
	 * Message.
	 */
	String message() default "jodd.vtor.constraint.ValidURL";

}
