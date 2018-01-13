// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.introspector;

/**
 * Property descriptor. It consist of read, write and field descriptor.
 * Only one of those three descriptors may exist.
 */
public class PropertyDescriptor extends Descriptor {

	protected final String name;
	protected final MethodDescriptor readMethodDescriptor;
	protected final MethodDescriptor writeMethodDescriptor;
	protected final FieldDescriptor fieldDescriptor;

	/**
	 * Creates field-only property descriptor.
	 */
	public PropertyDescriptor(final ClassDescriptor classDescriptor, final String propertyName, final FieldDescriptor fieldDescriptor) {
		super(classDescriptor, false);
		this.name = propertyName;
		this.readMethodDescriptor = null;
		this.writeMethodDescriptor = null;
		this.fieldDescriptor = fieldDescriptor;
	}

	/**
	 * Creates property descriptor.
	 */
	public PropertyDescriptor(final ClassDescriptor classDescriptor, final String propertyName, final MethodDescriptor readMethod, final MethodDescriptor writeMethod) {
		super(classDescriptor,
				((readMethod == null) || readMethod.isPublic()) & (writeMethod == null || writeMethod.isPublic())
		);
		this.name = propertyName;
		this.readMethodDescriptor = readMethod;
		this.writeMethodDescriptor = writeMethod;

		if (classDescriptor.isExtendedProperties()) {
			String[] prefix = classDescriptor.getPropertyFieldPrefix();

			FieldDescriptor fd = null;

			if (prefix != null) {
				for (String p : prefix) {
					fd = findField(p + propertyName);

					if (fd != null) {
						break;
					}
				}
			}
			else {
				fd = findField(propertyName);
			}

			this.fieldDescriptor = fd;
		} else {
			this.fieldDescriptor = null;
		}
	}

	/**
	 * Locates property field. Field is being searched also in all
	 * superclasses of current class.
	 */
	protected FieldDescriptor findField(final String fieldName) {
		FieldDescriptor fieldDescriptor = classDescriptor.getFieldDescriptor(fieldName, true);

		if (fieldDescriptor != null) {
			return fieldDescriptor;
		}

		// field descriptor not found in this class
		// try to locate it in the superclasses

		Class[] superclasses = classDescriptor.getAllSuperclasses();

		for (Class superclass : superclasses) {

			ClassDescriptor classDescriptor = ClassIntrospector.get().lookup(superclass);

			fieldDescriptor = classDescriptor.getFieldDescriptor(fieldName, true);

			if (fieldDescriptor != null) {
				return fieldDescriptor;
			}
		}

		// nothing found
		return null;
	}

	/**
	 * Returns property name.
	 */
	@Override
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
	 * Returns <code>true</code> if this is an extended property with
	 * only field definition and without getter and setter.
	 */
	public boolean isFieldOnly() {
		return (readMethodDescriptor == null) && (writeMethodDescriptor == null);
	}

	/**
	 * Returns <code>true</code> if this property has only a getter method.
	 */
	public boolean isGetterOnly() {
		return (fieldDescriptor == null) && (writeMethodDescriptor == null);
	}

	/**
	 * Returns <code>true</code> if this property has only a setter method.
	 */
	public boolean isSetterOnly() {
		return (fieldDescriptor == null) && (readMethodDescriptor == null);
	}

	// ---------------------------------------------------------------- type

	protected Class type;

	/**
	 * Returns property type. Raw types are detected.
	 */
	public Class getType() {
		if (type == null) {
			if (fieldDescriptor != null) {
				type = fieldDescriptor.getRawType();
			}
			else if (readMethodDescriptor != null) {
				type = readMethodDescriptor.getGetterRawType();
			}
			else if (writeMethodDescriptor != null) {
				type = writeMethodDescriptor.getSetterRawType();
			}
		}

		return type;
	}

	// ---------------------------------------------------------------- getters & setters

	protected Getter[] getters;
	protected Setter[] setters;

	/**
	 * Returns {@link Getter}. May return <code>null</code>
	 * if no matched getter is found.
	 */
	public Getter getGetter(final boolean declared) {
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
	protected Getter createGetter(final boolean declared) {
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
	public Setter getSetter(final boolean declared) {
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
	protected Setter createSetter(final boolean declared) {
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

	// ---------------------------------------------------------------- resolvers

	/**
	 * Resolves key type for given property descriptor.
	 */
	public Class resolveKeyType(final boolean declared) {
		Class keyType = null;

		Getter getter = getGetter(declared);

		if (getter != null) {
			keyType = getter.getGetterRawKeyComponentType();
		}

		if (keyType == null) {
			FieldDescriptor fieldDescriptor = getFieldDescriptor();

			if (fieldDescriptor != null) {
				keyType = fieldDescriptor.getRawKeyComponentType();
			}
		}

		return keyType;
	}

	/**
	 * Resolves component type for given property descriptor.
	 */
	public Class resolveComponentType(final boolean declared) {
		Class componentType = null;

		Getter getter = getGetter(declared);

		if (getter != null) {
			componentType = getter.getGetterRawComponentType();
		}

		if (componentType == null) {
			FieldDescriptor fieldDescriptor = getFieldDescriptor();

			if (fieldDescriptor != null) {
				componentType = fieldDescriptor.getRawComponentType();
			}
		}

		return componentType;
	}

}