package jodd.introspector;

/**
 * Property descriptor.
 */
public class PropertyDescriptor {

	protected final ClassDescriptor classDescriptor;
	protected final String name;
	protected final boolean isPublic;
	protected final MethodDescriptor readMethodDescriptor;
	protected final MethodDescriptor writeMethodDescriptor;

	public PropertyDescriptor(ClassDescriptor classDescriptor, String propertyName, MethodDescriptor readMethod, MethodDescriptor writeMethod) {
		this.classDescriptor = classDescriptor;
		this.name = propertyName;
		this.readMethodDescriptor = readMethod;
		this.writeMethodDescriptor = writeMethod;

		boolean isPublic = true;
		if (readMethodDescriptor != null) {
			isPublic = readMethodDescriptor.isPublic();
		}
		if (writeMethodDescriptor != null) {
			isPublic &= writeMethodDescriptor.isPublic();
		}
		this.isPublic = isPublic;
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
	 * Returns <code>true</code> if all properties methods are public.
	 */
	public boolean isPublic() {
		return isPublic;
	}

	/**
	 * Returns <code>true</code> if field matches required declared flag.
	 */
	public boolean matchDeclared(boolean declared) {
		if (!declared) {
			return isPublic;
		}
		return true;
	}

}