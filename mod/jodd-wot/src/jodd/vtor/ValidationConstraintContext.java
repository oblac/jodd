// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.vtor;

/**
 * Validation constraint context, used for validation in {@link ValidationConstraint}.
 */
public class ValidationConstraintContext {

	protected final Vtor vtor;
	protected final Object target;
	protected final String name;

	public ValidationConstraintContext(Vtor vtor, Object target, String name) {
		this.vtor = vtor;
		this.target = target;
		this.name = name;
	}

	/**
	 * Returns validator.
	 */
	public Vtor getValidator() {
		return vtor;
	}

	/**
	 * Returns target object containing the value.
	 */
	public Object getTarget() {
		return target;
	}

	/**
	 * Returns context name.
	 */
	public String getName() {
		return name;
	}


	/**
	 * Validates provided context and value withing this constraint content.
	 */
	public void validateWithin(ValidationContext vctx, Object value) {
		vtor.validate(vctx, value, name);
	}
}
