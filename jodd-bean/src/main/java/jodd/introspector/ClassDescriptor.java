// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector;

import jodd.util.ReflectUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
	 * Increases descriptor usage.
	 */
	protected void increaseUsageCount() {
		usageCount++;
	}

	/**
	 * Returns number of class description usages.
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

	protected Fields publicFields;
	protected Fields allFields;

	/**
	 * Inspect class fields and create fields cache.
	 */
	protected void inspectFields() {
		if (allFields != null) {
			return;
		}

		Field[] fields = accessibleOnly ? ReflectUtil.getAccessibleFields(type) : ReflectUtil.getSupportedFields(type);

		Fields publicFields = new Fields(fields.length);
		Fields allFields = new Fields(fields.length);

		for (Field field : fields) {
			String fName = field.getName();
			if (ReflectUtil.isPublic(field)) {
				publicFields.addField(fName, field, type);
			}
			ReflectUtil.forceAccess(field);
			allFields.addField(fName, field, type);
		}

		this.publicFields = publicFields;
		this.allFields = allFields;
	}

	/**
	 * Returns the field identified by name or <code>null</code> if not found.
	 *
	 * @param name	field name
	 * @param declared whether to look at non-public ones.
	 */
	public Field getField(String name, boolean declared) {
		inspectFields();
		if (declared == true) {
			return allFields.getField(name);
		} else {
			return publicFields.getField(name);
		}
	}

	/**
	 * Returns the total number of fields.
	 */
	public int getFieldCount(boolean declared) {
		inspectFields();
		if (declared == true) {
			return allFields.getCount();
		} else {
			return publicFields.getCount();
		}
	}

	/**
	 * Returns an array of all fields.
	 */
	public Field[] getAllFields(boolean declared) {
		inspectFields();
		if (declared == true) {
			return allFields.getAllFields();
		} else {
			return publicFields.getAllFields();
		}
	}

	/**
	 * Returns field descriptor.
	 */
	public FieldDescriptor getFieldDescriptor(String name, boolean declared) {
		inspectFields();
		if (declared == true) {
			return allFields.getFieldDescriptor(name);
		} else {
			return publicFields.getFieldDescriptor(name);
		}
	}

	// ---------------------------------------------------------------- methods

	protected Methods publicMethods;
	protected Methods allMethods;


	/**
	 * Inspect methods and create methods cache.
	 */
	protected void inspectMethods() {
		if (allMethods != null) {
			return;
		}
		Methods publicMethods = new Methods();
		Methods allMethods = new Methods();

		Method[] methods = accessibleOnly ? ReflectUtil.getAccessibleMethods(type) : ReflectUtil.getSupportedMethods(type);
		for (Method method : methods) {
			String methodName = method.getName();
			if (ReflectUtil.isPublic(method)) {
				publicMethods.addMethod(methodName, method);
			}
			ReflectUtil.forceAccess(method);
			allMethods.addMethod(methodName, method);
		}

		allMethods.lock();
		publicMethods.lock();

		this.allMethods = allMethods;
		this.publicMethods = publicMethods;
	}

	/**
	 * Returns the method identified by name or <code>null</code> if not found.
	 *
	 * @param name	method name
	 * @param declared whether to look at non-public ones.
	 */
	public Method getMethod(String name, boolean declared) {
		inspectMethods();
		if (declared == true) {
			return allMethods.getMethod(name);
		} else {
			return publicMethods.getMethod(name);
		}
	}

	/**
	 * Returns the method identified by name and parameters.
	 */
	public Method getMethod(String name, Class[] params, boolean declared) {
		inspectMethods();
		if (declared == true) {
			return allMethods.getMethod(name, params);
		} else {
			return publicMethods.getMethod(name, params);
		}
	}

	/**
	 * Returns an array of all methods with the same name.
	 */
	public Method[] getAllMethods(String name, boolean declared) {
		inspectMethods();
		if (declared == true) {
			return allMethods.getAllMethods(name);
		} else {
			return publicMethods.getAllMethods(name);
		}
	}

	/**
	 * Returns an array of all methods.
	 */
	public Method[] getAllMethods(boolean declared) {
		inspectMethods();
		if (declared == true) {
			return allMethods.getAllMethods();
		} else {
			return publicMethods.getAllMethods();
		}
	}

	// ---------------------------------------------------------------- beans

	protected Properties publicProperties;
	protected Properties allProperties;

	/**
	 * Inspect methods and create properties cache.
	 */
	protected void inspectProperties() {
		if (publicProperties != null) {
			return;
		}
		Properties publicProperties = new Properties();
		Properties allProperties = new Properties();

		Method[] methods = accessibleOnly ? ReflectUtil.getAccessibleMethods(type) : ReflectUtil.getSupportedMethods(type);
		for (Method method : methods) {
			if (Modifier.isStatic(method.getModifiers())) {
				continue;			// ignore static
			}
			boolean add = false;

			String methodName = ReflectUtil.getBeanPropertyGetterName(method);
			if (methodName != null) {
				methodName = '-' + methodName;
				add = true;
			} else {
				methodName = ReflectUtil.getBeanPropertySetterName(method);
				if (methodName != null) {
					methodName = '+' + methodName;
					add = true;
				}
			}

			if (add == true) {
				if (ReflectUtil.isPublic(method)) {
					publicProperties.addMethod(methodName, method);
				}
				ReflectUtil.forceAccess(method);
				allProperties.addMethod(methodName, method);
			}
		}
		allProperties.lock();
		publicProperties.lock();
		this.allProperties = allProperties;
		this.publicProperties = publicProperties;
	}

	/**
	 * Returns bean setter identified by name.
	 */
	public Method getBeanSetter(String name, boolean declared) {
		inspectProperties();
		if (declared == true) {
			return allProperties.setters.getMethod(name);
		} else {
			return publicProperties.setters.getMethod(name);
		}
	}
	/**
	 * Returns an array of all bean setters.
	 */
	public Method[] getAllBeanSetters(boolean declared) {
		inspectProperties();
		if (declared == true) {
			return allProperties.setters.getAllMethods();
		} else {
			return publicProperties.setters.getAllMethods();
		}
	}

	/**
	 * Returns an array of all bean setters names.
	 */
	public String[] getAllBeanSetterNames(boolean declared) {
		inspectProperties();
		if (declared == true) {
			return allProperties.setterNames;
		} else {
			return publicProperties.setterNames;
		}
	}

	/**
	 * Returns bean getter identified by name.
	 */
	public Method getBeanGetter(String name, boolean declared) {
		inspectProperties();
		if (declared == true) {
			return allProperties.getters.getMethod(name);
		} else {
			return publicProperties.getters.getMethod(name);
		}
	}

	/**
	 * Returns all bean getters.
	 */
	public Method[] getAllBeanGetters(boolean declared) {
		inspectProperties();
		if (declared == true) {
			return allProperties.getters.getAllMethods();
		} else {
			return publicProperties.getters.getAllMethods();
		}
	}

	/**
	 * Returns all bean getters names.
	 */
	public String[] getAllBeanGetterNames(boolean declared) {
		inspectProperties();
		if (declared == true) {
			return allProperties.getterNames;
		} else {
			return publicProperties.getterNames;
		}
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
		Ctors publicCtors = new Ctors();
		Ctors allCtors = new Ctors();

		publicCtors.addCtors(type.getConstructors());
		allCtors.addCtors(type.getDeclaredConstructors());

		Constructor[] ctors = allCtors.getAllCtors();
		for (Constructor ctor : ctors) {
			if (ReflectUtil.isPublic(ctor) == false) {
				ReflectUtil.forceAccess(ctor);
			}
		}
		publicCtors.lock();
		allCtors.lock();

		this.publicCtors = publicCtors;
		this.allCtors = allCtors;
	}

	/**
	 * Returns the default ctor or <code>null</code> if not found.
	 */
	public Constructor getDefaultCtor(boolean declared) {
		inspectCtors();
		if (declared == true) {
			return allCtors.getDefaultCtor();
		} else {
			return publicCtors.getDefaultCtor();
		}
	}

	/**
	 * Returns the constructor identified by arguments or <code>null</code> if not found.
	 *
	 * @param args	ctor arguments
	 * @param declared whether to look at non-public ones.
	 */
	public Constructor getCtor(Class[] args, boolean declared) {
		inspectCtors();
		if (declared == true) {
			return allCtors.getCtor(args);
		} else {
			return publicCtors.getCtor(args);
		}
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
	public int getCtorCount(boolean declared) {
		inspectCtors();
		if (declared == true) {
			return allCtors.getCount();
		} else {
			return publicCtors.getCount();
		}
	}

	/**
	 * Returns an array of all ctors.
	 */
	public Constructor[] getAllCtors(boolean declared) {
		inspectCtors();
		if (declared == true) {
			return allCtors.getAllCtors();
		} else {
			return publicCtors.getAllCtors();
		}
	}

}