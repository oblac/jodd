// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector;

import jodd.util.ReflectUtil;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.Collection;

/**
 * A descriptor class for all methods/fields/constructors of a class.
 * Static methods/fields are ignored.
 * Hash table is pre-built to speed up query.
 * <p>
 * Descriptors are 'lazy': various internal caches are created only on request.
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
	protected final boolean accessibleOnly;
	protected int usageCount;

	public ClassDescriptor(Class type, boolean accessibleOnly) {
		this.type = type;
		isArray = type.isArray();
		isMap = ReflectUtil.isSubclass(type, Map.class);
		isList = ReflectUtil.isSubclass(type, List.class);
		isSet = ReflectUtil.isSubclass(type, Set.class);
		isCollection = ReflectUtil.isSubclass(type, Collection.class);
		this.accessibleOnly = accessibleOnly;
	}

	/**
	 * Get the class object that this descriptor describes.
	 */
	public Class getType() {
		return type;
	}

	/**
	 * Returns <code>true</code> if this class descriptor
	 * works with only accessible fields and methods.
	 */
	public boolean isAccessibleOnly() {
		return accessibleOnly;
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
	 * Creates new collection on first usage.
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

		if (methodDescriptor != null) {
			if (!methodDescriptor.matchDeclared(declared)) {
				return null;
			}
		}

		return methodDescriptor;
	}


	/**
	 * Returns {@link MethodDescriptor method descriptor} identified by name and parameters.
	 */
	public MethodDescriptor getMethodDescriptor(String name, Class[] params, boolean declared) {
		MethodDescriptor methodDescriptor = getMethods().getMethodDescriptor(name, params);

		if (methodDescriptor != null) {
			if (!methodDescriptor.matchDeclared(declared)) {
				return null;
			}
		}

		return methodDescriptor;
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

	protected Properties getProperties() {
		if (properties == null) {
			properties = new Properties(this);
		}
		return properties;
	}

	public PropertyDescriptor getPropertyDescriptor(String name, boolean declared) {
		PropertyDescriptor propertyDescriptor = getProperties().getPropertyDescriptor(name);

		if (propertyDescriptor != null) {
			if (!propertyDescriptor.matchDeclared(declared)) {
				return null;
			}
		}

		return propertyDescriptor;
	}

	public PropertyDescriptor[] getAllPropertyDescriptors() {
		return getProperties().getAllPropertyDescriptors();
	}

	public MethodDescriptor getPropertySetterDescriptor(String name, boolean declared) {
		PropertyDescriptor propertyDescriptor = getProperties().getPropertyDescriptor(name);

		if (propertyDescriptor != null) {
			MethodDescriptor setter = propertyDescriptor.getWriteMethodDescriptor();
			if (setter != null) {
				if (setter.matchDeclared(declared)) {
					return setter;
				}
			}
		}
		return null;
	}

	public MethodDescriptor getPropertyGetterDescriptor(String name, boolean declared) {
		PropertyDescriptor propertyDescriptor = getProperties().getPropertyDescriptor(name);

		if (propertyDescriptor != null) {
			MethodDescriptor getter = propertyDescriptor.getReadMethodDescriptor();
			if (getter != null) {
				if (getter.matchDeclared(declared)) {
					return getter;
				}
			}
		}
		return null;
	}

	// ---------------------------------------------------------------- ctors

	protected Ctors publicCtors;
	protected Ctors allCtors;

	/**
	 * Inspect class ctors and create ctors cache.
	 */
	protected void inspectCtors() {
		if (allCtors != null) {
			return;
		}
		Ctors publicCtors = new Ctors(this);
		Ctors allCtors = new Ctors(this);

		publicCtors.addCtors(type.getConstructors());
		allCtors.addCtors(type.getDeclaredConstructors());

		Constructor[] ctors = allCtors.getAllCtors();
		for (Constructor ctor : ctors) {
			if (ReflectUtil.isPublic(ctor) == false) {
				ReflectUtil.forceAccess(ctor);
			}
		}

		this.publicCtors = publicCtors;
		this.allCtors = allCtors;
	}

	/**
	 * Returns the default ctor or <code>null</code> if not found.
	 */
	public Constructor getDefaultCtor(boolean declared) {
		inspectCtors();

		Ctors ctors = declared ? allCtors : publicCtors;
		return ctors.getDefaultCtor();
	}

	/**
	 * Returns the constructor identified by arguments or <code>null</code> if not found.
	 *
	 * @param args	ctor arguments
	 * @param declared whether to look at non-public ones.
	 */
	public Constructor getCtor(Class[] args, boolean declared) {
		inspectCtors();

		Ctors ctors = declared ? allCtors : publicCtors;
		return ctors.getCtor(args);
	}

	/**
	 * Returns the public default ctor or <code>null</code> if not found.
	 */
	public Constructor getDefaultCtor() {
		inspectCtors();

		return publicCtors.getDefaultCtor();
	}

	/**
	 * Returns the total number of constructors.
	 */
	public int getCtorsCount(boolean declared) {
		inspectCtors();

		Ctors ctors = declared ? allCtors : publicCtors;
		return ctors.getCount();
	}

	/**
	 * Returns an array of all ctors.
	 */
	public Constructor[] getAllCtors(boolean declared) {
		inspectCtors();

		Ctors ctors = declared ? allCtors : publicCtors;
		return ctors.getAllCtors();
	}

}