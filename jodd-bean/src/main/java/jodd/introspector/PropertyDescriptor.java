package jodd.introspector;

/**
 * Property descriptor.
 */
public class PropertyDescriptor extends Descriptor {

	protected final String name;
	protected final MethodDescriptor readMethodDescriptor;
	protected final MethodDescriptor writeMethodDescriptor;
	protected final FieldDescriptor fieldDescriptor;

	/**
	 * Creates field-only property descriptor.
	 */
	public PropertyDescriptor(ClassDescriptor classDescriptor, String propertyName, FieldDescriptor fieldDescriptor) {
		super(classDescriptor, false);
		this.name = propertyName;
		this.readMethodDescriptor = null;
		this.writeMethodDescriptor = null;
		this.fieldDescriptor = fieldDescriptor;
	}

	/**
	 * Creates property descriptor.
	 */
	public PropertyDescriptor(ClassDescriptor classDescriptor, String propertyName, MethodDescriptor readMethod, MethodDescriptor writeMethod) {
		super(classDescriptor,
				((readMethod == null) || readMethod.isPublic()) & (writeMethod == null || writeMethod.isPublic())
		);
		this.name = propertyName;
		this.readMethodDescriptor = readMethod;
		this.writeMethodDescriptor = writeMethod;

		if (classDescriptor.isExtendedProperties()) {
			this.fieldDescriptor = findField(propertyName);
		} else {
			this.fieldDescriptor = null;
		}
	}

	/**
	 * Locates property field.
	 */
	protected FieldDescriptor findField(String fieldName) {
		String prefix = classDescriptor.getPropertyFieldPrefix();

		if (prefix != null) {
			fieldName = prefix + fieldName;
		}

		return classDescriptor.getFieldDescriptor(fieldName, true);
	}

	/**
	 * Returns property name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns read method of this property.
	 * May be <code>null</code> if read method is not defined.
	 */
	public MethodDescriptor getReadMethodDescriptor() {
		return readMethodDescriptor;
	}

	/**
	 * Returns write method of this property.
	 * May be <code>null</code> for read-only properties.
	 */
	public MethodDescriptor getWriteMethodDescriptor() {
		return writeMethodDescriptor;
	}

	/**
	 * Returns the associated field of this property.
	 * May be <code>null</code> if properties are not enhanced by field description.
	 */
	public FieldDescriptor getFieldDescriptor() {
		return fieldDescriptor;
	}

	/**
	 * Returns <code>true</code> if this is extended property with
	 * only field definition and without getter and setter.
	 */
	public boolean isFieldOnlyDescriptor() {
		return (readMethodDescriptor == null) && (writeMethodDescriptor == null);
	}

	// ---------------------------------------------------------------- getters & setters

	protected Getter[] getters;
	protected Setter[] setters;

	/**
	 * Returns {@link Getter}. May return <code>null</code>
	 * if no matched getter is found.
	 */
	public Getter getGetter(boolean declared) {
		if (getters == null) {
			getters = new Getter[] {
					createGetter(false),
					createGetter(true),
			};
		}

		return getters[declared ? 1 : 0];
	}

	/**
	 * Creates a {@link Getter}.
	 */
	protected Getter createGetter(boolean declared) {
		if (readMethodDescriptor != null) {
			if (readMethodDescriptor.matchDeclared(declared)) {
				return readMethodDescriptor;
			}
		}
		if (fieldDescriptor != null) {
			if (fieldDescriptor.matchDeclared(declared)) {
				return fieldDescriptor;
			}
		}
		return null;
	}


	/**
	 * Returns {@link Setter}. May return <code>null</code>
	 * if no matched setter is found.
	 */
	public Setter getSetter(boolean declared) {
		if (setters == null) {
			setters = new Setter[] {
					createSetter(false),
					createSetter(true),
			};
		}

		return setters[declared ? 1 : 0];
	}

	/**
	 * Creates a {@link Setter}.
	 */
	protected Setter createSetter(boolean declared) {
		if (writeMethodDescriptor != null) {
			if (writeMethodDescriptor.matchDeclared(declared)) {
				return writeMethodDescriptor;
			}
		}
		if (fieldDescriptor != null) {
			if (fieldDescriptor.matchDeclared(declared)) {
				return fieldDescriptor;
			}
		}
		return null;
	}

}