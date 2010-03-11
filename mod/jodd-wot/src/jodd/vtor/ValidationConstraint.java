package jodd.vtor;

import java.lang.annotation.Annotation;

/**
 * Definition of validation constraint.
 */
public interface ValidationConstraint<A extends Annotation> {

	/**
	 * Configures constraint from associated annotation.
	 */
	void configure(A annotation);

	/**
	 * Performs validation in context of target object on provided value.
	 * @return <code>true</code> if validation passes, otherwise <code>false</code>.
	 */
	boolean isValid(ValidationConstraintContext vcc, Object value);
}
