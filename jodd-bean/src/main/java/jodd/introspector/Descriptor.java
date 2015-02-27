// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector;

/**
 * Common descriptor stuff.
 */
public abstract class Descriptor {

	protected final ClassDescriptor classDescriptor;
	protected final boolean isPublic;

	protected Descriptor(ClassDescriptor classDescriptor, boolean isPublic) {
		this.classDescriptor = classDescriptor;
		this.isPublic = isPublic;
	}

	/**
	 * Returns belonging class descriptor.
	 */
	public ClassDescriptor getClassDescriptor() {
		return classDescriptor;
	}

	/**
	 * Returns <code>true</code> if descriptor content is public.
	 */
	public boolean isPublic() {
		return isPublic;
	}

	/**
	 * Returns <code>true</code> if descriptor content matches required declared flag.
	 */
	public boolean matchDeclared(boolean declared) {
		if (!declared) {
			return isPublic;
		}
		return true;
	}

	/**
	 * Returns the name of descriptors target.
	 */
	public abstract String getName();

}