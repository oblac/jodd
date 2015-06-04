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

import jodd.util.ReflectUtil;

import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.Collection;

/**
 * A descriptor class for all methods/fields/properties/constructors of a class.
 * Static methods/fields are ignored.
 * <p>
 * Descriptors are 'lazy': various internal caches are created on first request.
 * <p>
 * Throughout this class, public members are defined as members
 * defined with "public" keyword and declared in a public type.
 * Public members declared by a non-public class is considered non-public
 * because access to it from outside is prohibited by the java access control
 * anyway.
 * <p>
 * Public members defined in public classes are always preferred even
 * when we allow private/protected members and types to be visible.
 * So if a non-public subtype and a public super type both have a field
 * with the same name, the field in the public super type is always used.
 */
public class ClassDescriptor {

	protected final Class type;
	protected final boolean scanAccessible;
	protected final boolean extendedProperties;
	protected final boolean includeFieldsAsProperties;
	protected final String[] propertyFieldPrefix;
	protected final Class[] interfaces;
	protected final Class[] superclasses;
	protected int usageCount;

	public ClassDescriptor(Class type, boolean scanAccessible, boolean extendedProperties, boolean includeFieldsAsProperties, String[] propertyFieldPrefix) {
		this.type = type;
		this.scanAccessible = scanAccessible;
		this.extendedProperties = extendedProperties;
		this.includeFieldsAsProperties = includeFieldsAsProperties;
		this.propertyFieldPrefix = propertyFieldPrefix;

		isArray = type.isArray();
		isMap = ReflectUtil.isTypeOf(type, Map.class);
		isList = ReflectUtil.isTypeOf(type, List.class);
		isSet = ReflectUtil.isTypeOf(type, Set.class);
		isCollection = ReflectUtil.isTypeOf(type, Collection.class);

		interfaces = ReflectUtil.resolveAllInterfaces(type);
		superclasses = ReflectUtil.resolveAllSuperclasses(type);
	}

	/**
	 * Get the class object that this descriptor describes.
	 */
	public Class getType() {
		return type;
	}

	/**
	 * Returns <code>true</code> if this class descriptor
	 * works with accessible fields/methods/constructors or with
	 * all supported.
	 */
	public boolean isScanAccessible() {
		return scanAccessible;
	}

	/**
	 * Returns <code>true</code> if properties in this class descriptor
	 * are extended and include field description.
	 */
	public boolean isExtendedProperties() {
		return extendedProperties;
	}

	/**
	 * Include fields as properties.
	 */
	public boolean isIncludeFieldsAsProperties() {
		return includeFieldsAsProperties;
	}

	/**
	 * Returns property field prefixes. May be <code>null</code>
	 * if prefixes are not set. If you need to access both prefixed
	 * and non-prefixed fields, use empty string as one of the prefixes.
	 */
	public String[] getPropertyFieldPrefix() {
		return propertyFieldPrefix;
	}

	/**
	 * Increases usage count.
	 */
	protected void increaseUsageCount() {
		usageCount++;
	}

	/**
	 * Returns number of class descriptor usages. That is number
	 * of times when class descriptor for some class has been
	 * lookuped. Higher usage count means that some class is
	 * more frequently being used.
	 */
	public int getUsageCount() {
		return usageCount;
	}

	// ---------------------------------------------------------------- special

	private final boolean isArray;
	/**
	 * Returns <code>true</code> if class is an array.
	 */
	public boolean isArray() {
		return isArray;
	}

	private final boolean isMap;
	/**
	 * Returns <code>true</code> if class is a <code>Map</code>.
	 */
	public boolean isMap() {
		return isMap;
	}

	private final boolean isList;
	/**
	 * Returns <code>true</code> if class is a <code>List</code>.
	 */
	public boolean isList() {
		return isList;
	}

	private final boolean isSet;
	/**
	 * Returns <code>true</code> if type is a <code>Set</code>.
	 */
	public boolean isSet() {
		return isSet;
	}

	private final boolean isCollection;
	/**
	 * Returns <code>true</code> if type is a collection.
	 */
	public boolean isCollection() {
		return isCollection;
	}

	// ---------------------------------------------------------------- fields

	private Fields fields;

	/**
	 * Returns {@link Fields fields collection}.
	 * Creates new fields collection on first usage.
	 */
	protected Fields getFields() {
		if (fields == null) {
			fields = new Fields(this);
		}
		return fields;
	}

	/**
	 * Returns field descriptor.
	 */
	public FieldDescriptor getFieldDescriptor(String name, boolean declared) {
		FieldDescriptor fieldDescriptor = getFields().getFieldDescriptor(name);

		if (fieldDescriptor != null) {
			if (!fieldDescriptor.matchDeclared(declared)) {
				return null;
			}
		}

		return fieldDescriptor;
	}

	/**
	 * Returns all field descriptors, including declared ones.
	 */
	public FieldDescriptor[] getAllFieldDescriptors() {
		return getFields().getAllFieldDescriptors();
	}

	// ---------------------------------------------------------------- methods

	private Methods methods;

	/**
	 * Returns methods collection.
	 * Creates new collection on first access.
	 */
	protected Methods getMethods() {
		if (methods == null) {
			methods = new Methods(this);
		}
		return methods;
	}

	/**
	 * Returns {@link MethodDescriptor method descriptor} identified by name and parameters.
	 */
	public MethodDescriptor getMethodDescriptor(String name, boolean declared) {
		MethodDescriptor methodDescriptor = getMethods().getMethodDescriptor(name);

		if ((methodDescriptor != null) && methodDescriptor.matchDeclared(declared)) {
			return methodDescriptor;
		}

		return methodDescriptor;
	}


	/**
	 * Returns {@link MethodDescriptor method descriptor} identified by name and parameters.
	 */
	public MethodDescriptor getMethodDescriptor(String name, Class[] params, boolean declared) {
		MethodDescriptor methodDescriptor = getMethods().getMethodDescriptor(name, params);

		if ((methodDescriptor != null) && methodDescriptor.matchDeclared(declared)) {
			return methodDescriptor;
		}

		return null;
	}

	/**
	 * Returns an array of all methods with the same name.
	 */
	public MethodDescriptor[] getAllMethodDescriptors(String name) {
		return getMethods().getAllMethodDescriptors(name);
	}

	/**
	 * Returns an array of all methods.
	 */
	public MethodDescriptor[] getAllMethodDescriptors() {
		return getMethods().getAllMethodDescriptors();
	}

	// ---------------------------------------------------------------- properties

	private Properties properties;

	/**
	 * Returns properties collection.
	 * Creates new collection on first access.
	 */
	protected Properties getProperties() {
		if (properties == null) {
			properties = new Properties(this);
		}
		return properties;
	}

	/**
	 * Returns property descriptor. Declared flag is matched on both read and write
	 * methods.
	 */
	public PropertyDescriptor getPropertyDescriptor(String name, boolean declared) {
		PropertyDescriptor propertyDescriptor = getProperties().getPropertyDescriptor(name);

		if ((propertyDescriptor != null) && propertyDescriptor.matchDeclared(declared)) {
			return propertyDescriptor;
		}

		return null;
	}

	/**
	 * Returns all properties descriptors.
	 */
	public PropertyDescriptor[] getAllPropertyDescriptors() {
		return getProperties().getAllPropertyDescriptors();
	}

	// ---------------------------------------------------------------- ctors

	private Ctors ctors;

	/**
	 * Returns constructors collection.
	 * Creates new collection of first access.
	 */
	protected Ctors getCtors() {
		if (ctors == null) {
			ctors = new Ctors(this);
		}
		return ctors;
	}

	/**
	 * Returns the default ctor or <code>null</code> if not found.
	 */
	public CtorDescriptor getDefaultCtorDescriptor(boolean declared) {
		CtorDescriptor defaultCtor = getCtors().getDefaultCtor();

		if ((defaultCtor != null) && defaultCtor.matchDeclared(declared)) {
			return defaultCtor;
		}
		return null;
	}

	/**
	 * Returns the constructor identified by arguments or <code>null</code> if not found.
	 */
	public CtorDescriptor getCtorDescriptor(Class[] args, boolean declared) {
		CtorDescriptor ctorDescriptor = getCtors().getCtorDescriptor(args);

		if ((ctorDescriptor != null) && ctorDescriptor.matchDeclared(declared)) {
			return ctorDescriptor;
		}
		return null;
	}

	/**
	 * Returns an array of all {@link CtorDescriptor constructor descriptors}.
	 */
	public CtorDescriptor[] getAllCtorDescriptors() {
		return getCtors().getAllCtorDescriptors();
	}


	// ---------------------------------------------------------------- interfaces

	/**
	 * Returns <b>all</b> interfaces of this class.
	 */
	public Class[] getAllInterfaces() {
		return interfaces;
	}

	/**
	 * Returns <b>all</b> superclasses of this class.
	 * <code>Object.class</code> is <b>not</b> included in the
	 * returned list.
	 */
	public Class[] getAllSuperclasses() {
		return superclasses;
	}
}