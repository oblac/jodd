package jodd.introspector;

/**
 * Property descriptor.
 */
public class PropertyDescriptor extends Descriptor {

	protected final String name;
	protected final MethodDescriptor readMethodDescriptor;
	protected final MethodDescriptor writeMethodDescriptor;

	public PropertyDescriptor(ClassDescriptor classDescriptor, String propertyName, MethodDescriptor readMethod, MethodDescriptor writeMethod) {
		super(classDescriptor,
				((readMethod == null) || readMethod.isPublic()) & (writeMethod == null || writeMethod.isPublic())
		);
		this.name = propertyName;
		this.readMethodDescriptor = readMethod;
		this.writeMethodDescriptor = writeMethod;
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

}