package jodd.vtor;

/**
 * Validation violation description.
 */
public class Violation {

	private final String name;
	private final Object validatedObject;
	private final Object invalidValue;
	private final Check check;
	private final ValidationConstraint constraint;

	public Violation(String name, Object validatedObject, Object invalidValue) {
		this(name, validatedObject, invalidValue, null);
	}

	/**
	 * Creates new violation.
	 * @param name violation name inside of validation context
	 * @param validatedObject object that is validated
	 * @param invalidValue invalid value that is cause of violation
	 * @param check {@link Check check} that made validation. 
	 */
	public Violation(String name, Object validatedObject, Object invalidValue, Check check) {
		this.name = name;
		this.validatedObject = validatedObject;
		this.invalidValue = invalidValue;
		this.check = check;
		this.constraint = check != null ? check.getConstraint() : null;
	}

	public Object getValidatedObject() {
		return validatedObject;
	}

	public Object getInvalidValue() {
		return invalidValue;
	}

	public Check getCheck() {
		return check;
	}

	public ValidationConstraint getConstraint() {
		return constraint;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Violation{" + name + ':' + constraint.getClass().getName() + '}';
	}
}
