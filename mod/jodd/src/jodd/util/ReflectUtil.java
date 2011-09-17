// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import jodd.typeconverter.TypeConverter;
import jodd.typeconverter.TypeConverterManager;
import jodd.typeconverter.TypeConversionException;

import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Various java.lang.reflect utilities.
 */
public class ReflectUtil {

	/** an empty class array */
	public static final Class[] NO_PARAMETERS = new Class[0];

	/** an empty object array */
	public static final Object[] NO_ARGUMENTS = new Object[0];

	/** an empty object array */
	public static final Type[] NO_TYPES = new Type[0];

	public static final String METHOD_GET_PREFIX = "get";
	public static final String METHOD_IS_PREFIX = "is";
	public static final String METHOD_SET_PREFIX = "set";

	// ---------------------------------------------------------------- method0
	private static Method _getMethod0;

	static {
		try {
			_getMethod0 = Class.class.getDeclaredMethod("getMethod0", String.class, Class[].class);
			_getMethod0.setAccessible(true);
		} catch (Exception ignore) {
			try {
				_getMethod0 = Class.class.getMethod("getMethod", String.class, Class[].class);
			} catch (Exception ignored) {
				_getMethod0 =  null;
			}
		}
	}

	/**
	 * Invokes private <code>Class.getMethod0()</code> without throwing NoSuchMethodException.
	 * Returns only public methods or <code>null</code> if method not found.
	 *
	 * @param c      			class to inspect
	 * @param name   			name of method to find
	 * @param parameterTypes	parameter types
	 * @return founded method, or null
	 */
	public static Method getMethod0(Class c, String name, Class... parameterTypes) {
		try {
			return (Method) _getMethod0.invoke(c, name, parameterTypes);
		} catch (Exception ignore) {
			return null;
		}
	}

	/**
	 * Invokes private <code>Class.getMethod0()</code> without throwing NoSuchMethod exception.
	 * Returns only accessible methods.
	 *
	 * @param o      			object to inspect
	 * @param name   			name of method to find
	 * @param parameterTypes 	parameter types
	 * @return founded method, or null
	 */
	public static Method getMethod0(Object o, String name, Class... parameterTypes) {
		try {
			return (Method) _getMethod0.invoke(o.getClass(), name, parameterTypes);
		} catch (Exception ignore) {
			return null;
		}
	}

	// ---------------------------------------------------------------- find method

	/**
	 * Returns method from an object, matched by name. This may be considered as
	 * a slow operation, since methods are matched one by one.
	 * Returns only accessible methods.
	 * Only first method is matched.
	 *
	 * @param c          class to examine
	 * @param methodName Full name of the method.
	 * @return null if method not found
	 */
	public static Method findMethod(Class c, String methodName) {
		return findDeclaredMethod(c, methodName, true);
	}

	public static Method findDeclaredMethod(Class c, String methodName) {
		return findDeclaredMethod(c, methodName, false);
	}


	private static Method findDeclaredMethod(Class c, String methodName, boolean publicOnly) {
		if ((methodName == null) || (c == null)) {
			return null;
		}
		Method[] ms = publicOnly ? c.getMethods() : c.getDeclaredMethods();
		for (Method m : ms) {
			if (m.getName().equals(methodName)) {
				return m;
			}
		}
		return null;

	}


	// ---------------------------------------------------------------- classes

	/**
	 * Returns classes from array of specified objects.
	 */
	public static Class[] getClasses(Object... objects) {
		if (objects == null) {
			return null;
		}
		Class[] result = new Class[objects.length];
		for (int i = 0; i < objects.length; i++) {
			if (objects[i] != null) {
				result[i] = objects[i].getClass();
			}
		}
		return result;
	}


	// ---------------------------------------------------------------- invoke


	/**
	 * Invokes accessible method of an object.
	 *
	 * @param c      		class that contains method
	 * @param obj    		object to execute
	 * @param method 		method to invoke
	 * @param paramClasses	classes of parameters
	 * @param params 		parameters
�	 */
	public static Object invoke(Class c, Object obj, String method, Class[] paramClasses, Object[] params) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		Method m = c.getMethod(method, paramClasses);
		return m.invoke(obj, params);
	}


	/**
	 * Invokes accessible method of an object.
	 *
	 * @param obj    		object
	 * @param method 		name of the objects method
	 * @param params 		method parameters
	 * @param paramClasses	method parameter types
	 */
	public static Object invoke(Object obj, String method, Class[] paramClasses, Object[] params) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		Method m = obj.getClass().getMethod(method, paramClasses);
		return m.invoke(obj, params);
	}

	/**
	 * Invokes accessible method of an object without specifying parameter types.
	 * @param obj    object
	 * @param method method of an object
	 * @param params method parameters
	 */
	public static Object invoke(Object obj, String method, Object[] params) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		Class[] paramClass = getClasses(params);
		return invoke(obj, method, paramClass, params);
	}

	public static Object invoke(Class c, Object obj, String method, Object[] params) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		Class[] paramClass = getClasses(params);
		return invoke(c, obj, method, paramClass, params);
	}


	// ---------------------------------------------------------------- invokeDeclared


	/**
	 * Invokes any method of a class, even private ones.
	 *
	 * @param c      		class to examine
	 * @param obj    		object to inspect
	 * @param method 		method to invoke
	 * @param paramClasses	parameter types
	 * @param params 		parameters
	 */
	public static Object invokeDeclared(Class c, Object obj, String method, Class[] paramClasses, Object[] params) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		Method m = c.getDeclaredMethod(method, paramClasses);
		m.setAccessible(true);
		return m.invoke(obj, params);
	}

	/**
	 * Invokes any method of a class suppressing java access checking.
	 *
	 * @param obj    		object to inspect
	 * @param method 		method to invoke
	 * @param paramClasses	parameter types
	 * @param params 		parameters
	 */
	public static Object invokeDeclared(Object obj, String method, Class[] paramClasses, Object[] params) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		Method m = obj.getClass().getDeclaredMethod(method, paramClasses);
		m.setAccessible(true);
		return m.invoke(obj, params);
	}

	public static Object invokeDeclared(Object obj, String method, Object[] params) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		Class[] paramClass = getClasses(params);
		return invokeDeclared(obj, method, paramClass, params);
	}

	public static Object invokeDeclared(Class c, Object obj, String method, Object[] params) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		Class[] paramClass = getClasses(params);
		return invokeDeclared(c, obj, method, paramClass, params);
	}


	// ---------------------------------------------------------------- match classes

	/**
	 * Determines if first class match the destination and simulates kind
	 * of <code>instanceof</code>. All subclasses and interface of first class
	 * are examined against second class. Method is not symmetric.
	 */
	public static boolean isSubclass(Class thisClass, Class target) {
		if (target.isInterface() != false) {
			return isInterfaceImpl(thisClass, target);
		}
		for (Class x = thisClass; x != null; x = x.getSuperclass()) {
			if (x == target) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns <code>true</code> if provided class is interface implementation.
	 */
	public static boolean isInterfaceImpl(Class thisClass, Class targetInterface) {
		for (Class x = thisClass; x != null; x = x.getSuperclass()) {
			Class[] interfaces = x.getInterfaces();
			for (Class i : interfaces) {
				if (i == targetInterface) {
					return true;
				}
				if (isInterfaceImpl(i, targetInterface)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Dynamic version of <code>instanceof</code>.
	 * Much faster then Class.isInstance().
	 *
	 * @param o			object to match
	 * @param target	target class
	 * @return			<code>true</code> if object is an instance of target class
	 */
	public static boolean isInstanceOf(Object o, Class target) {
		return isSubclass(o.getClass(), target);
	}

	/**
	 * Casts an object to destination type using {@link TypeConverterManager type conversion}.
	 * If destination type is one of common type, consider using {@link jodd.typeconverter.Convert} instead.
	 */
	@SuppressWarnings({"unchecked"})
	public static <T> T castType(Object value, Class<T> destinationType) {
		if (value == null) {
			return null;
		}
		TypeConverter converter = TypeConverterManager.lookup(destinationType);
		if (converter == null) {
			// no converter available, try to cast manually
			if (isInstanceOf(value, destinationType) == true) {
				return (T) value;
			}
			if (destinationType.isEnum()) {
				Object[] enums = destinationType.getEnumConstants();
				String valStr = value.toString();
				for (Object e : enums) {
					if (e.toString().equals(valStr)) {
						return (T) e;
					}
				}
			}
			throw new ClassCastException("Unable to cast value to type: '" + destinationType.getName() + "'.");
		}
		try {
			return (T) converter.convert(value);
		} catch (TypeConversionException tcex) {
			throw new ClassCastException("Unable to convert value to type: '" + destinationType.getName() + "'.:" + tcex.toString());
		}
	}


	// ---------------------------------------------------------------- accessible methods


	/**
	 * Returns array of all methods that are accessible from given class.
	 * @see #getAccessibleMethods(Class, Class)
	 */
	public static Method[] getAccessibleMethods(Class clazz) {
		return getAccessibleMethods(clazz, Object.class);
	}

	/**
	 * Returns array of all methods that are accessible from given class, upto limit
	 * (usually <code>Object.class</code>). Abstract methods are ignored.
	 */
	public static Method[] getAccessibleMethods(Class clazz, Class limit) {
		Package topPackage = clazz.getPackage();
		List<Method> methodList = new ArrayList<Method>();
		int topPackageHash = topPackage == null ? 0 : topPackage.hashCode();
		boolean top = true;
		do {
			if (clazz == null) {
				break;
			}
			Method[] declaredMethods = clazz.getDeclaredMethods();
			for (Method method : declaredMethods) {
				if (Modifier.isVolatile(method.getModifiers())) {
				    continue;
				}
//				if (Modifier.isAbstract(method.getModifiers())) {
//					continue;
//				}
				if (top == true) {				// add all top declared methods
					methodList.add(method);
					continue;
				}
				int modifier = method.getModifiers();
				if (Modifier.isPrivate(modifier) == true) {
					continue;										// ignore super private methods
				}
				if (Modifier.isAbstract(modifier) == true) {		// ignore super abstract methods
					continue;
				}
				if (Modifier.isPublic(modifier) == true) {
					addMethodIfNotExist(methodList, method);		// add super public methods
					continue;
				}
				if (Modifier.isProtected(modifier) == true) {
					addMethodIfNotExist(methodList, method);		// add super protected methods
					continue;
				}
				// add super default methods from the same package
				Package pckg = method.getDeclaringClass().getPackage();
				int pckgHash = pckg == null ? 0 : pckg.hashCode();
				if (pckgHash == topPackageHash) {
					addMethodIfNotExist(methodList, method);
				}
			}
			top = false;
		} while ((clazz = clazz.getSuperclass()) != limit);

		Method[] methods = new Method[methodList.size()];
		for (int i = 0; i < methods.length; i++) {
			methods[i] = methodList.get(i);
		}
		return methods;
	}

	private static void addMethodIfNotExist(List<Method> allMethods, Method newMethod) {
		for (Method m : allMethods) {
			if (compareSignatures(m, newMethod) == true) {
				return;
			}
		}
		allMethods.add(newMethod);
	}

	// ---------------------------------------------------------------- accessible fields


	public static Field[] getAccessibleFields(Class clazz) {
		return getAccessibleFields(clazz, Object.class);
	}

	public static Field[] getAccessibleFields(Class clazz, Class limit) {
		Package topPackage = clazz.getPackage();
		List<Field> fieldList = new ArrayList<Field>();
		int topPackageHash = topPackage == null ? 0 : topPackage.hashCode();
		boolean top = true;
		do {
			if (clazz == null) {
				break;
			}
			Field[] declaredFields = clazz.getDeclaredFields();
			for (Field field : declaredFields) {
				if (top == true) {				// add all top declared fields
					fieldList.add(field);
					continue;
				}
				int modifier = field.getModifiers();
				if (Modifier.isPrivate(modifier) == true) {
					continue;										// ignore super private fields
				}
				if (Modifier.isPublic(modifier) == true) {
					addFieldIfNotExist(fieldList, field);			// add super public methods
					continue;
				}
				if (Modifier.isProtected(modifier) == true) {
					addFieldIfNotExist(fieldList, field);			// add super protected methods
					continue;
				}
				// add super default methods from the same package
				Package pckg = field.getDeclaringClass().getPackage();
				int pckgHash = pckg == null ? 0 : pckg.hashCode();
				if (pckgHash == topPackageHash) {
					addFieldIfNotExist(fieldList, field);
				}
			}
			top = false;
		} while ((clazz = clazz.getSuperclass()) != limit);

		Field[] fields = new Field[fieldList.size()];
		for (int i = 0; i < fields.length; i++) {
			fields[i] = fieldList.get(i);
		}
		return fields;
	}

	private static void addFieldIfNotExist(List<Field> allFields, Field newField) {
		for (Field f : allFields) {
			if (compareSignatures(f, newField) == true) {
				return;
			}
		}
		allFields.add(newField);
	}


	// ---------------------------------------------------------------- supported methods


	public static Method[] getSupportedMethods(Class clazz) {
		return getSupportedMethods(clazz, Object.class);
	}

	/**
	 * Returns a <code>Method</code> array of the methods to which instances of the specified
	 * respond except for those methods defined in the class specified by limit
	 * or any of its superclasses. Note that limit is usually used to eliminate
	 * them methods defined by <code>java.lang.Object</code>. If limit is <code>null</code> then all
	 * methods are returned.
	 */
	public static Method[] getSupportedMethods(Class clazz, Class limit) {
		ArrayList<Method> supportedMethods = new ArrayList<Method>();
		for (Class c = clazz; c != limit; c = c.getSuperclass()) {
			Method[] methods = c.getDeclaredMethods();
			for (Method method : methods) {
				boolean found = false;
				for (Method supportedMethod : supportedMethods) {
					if (compareSignatures(method, supportedMethod)) {
						found = true;
						break;
					}
				}
				if (found == false) {
					supportedMethods.add(method);
				}
			}
		}
		return supportedMethods.toArray(new Method[supportedMethods.size()]);
	}


	public static Field[] getSupportedFields(Class clazz) {
		return getSupportedFields(clazz, Object.class);
	}

	public static Field[] getSupportedFields(Class clazz, Class limit) {
		ArrayList<Field> supportedFields = new ArrayList<Field>();
		for (Class c = clazz; c != limit; c = c.getSuperclass()) {
			Field[] fields = c.getDeclaredFields();
			for (Field field : fields) {
				boolean found = false;
				for (Field supportedField : supportedFields) {
					if (compareSignatures(field, supportedField)) {
						found = true;
						break;
					}
				}
				if (found == false) {
					supportedFields.add(field);
				}
			}
		}
		return supportedFields.toArray(new Field[supportedFields.size()]);
	}


	// ---------------------------------------------------------------- compare

	/**
	 * Compares method declarations: signature and return types.
	 */
	public static boolean compareDeclarations(Method first, Method second) {
		if (first.getReturnType() != second.getReturnType()) {
			return false;
		}
		return compareSignatures(first, second);
	}

	/**
	 * Compares method signatures: names and parameters
	 */
	public static boolean compareSignatures(Method first, Method second) {
		if (first.getName().equals(second.getName()) == false) {
			return false;
		}
		return compareParameteres(first.getParameterTypes(), second.getParameterTypes());
	}

	/**
	 * Compares constructor signatures: names and parameters
	 */
	public static boolean compareSignatures(Constructor first, Constructor second) {
		if (first.getName().equals(second.getName()) == false) {
			return false;
		}
		return compareParameteres(first.getParameterTypes(), second.getParameterTypes());
	}

	public static boolean compareSignatures(Field first, Field second) {
		return first.getName().equals(second.getName());
	}



	/**
	 * Compares method or ctor parameters.
	 */
	public static boolean compareParameteres(Class[] first, Class[] second) {
		if (first.length != second.length) {
			return false;
		}
		for (int i = 0; i < first.length; i++) {
			if (first[i] != second[i]) {
				return false;
			}
		}
		return true;
	}



	// ---------------------------------------------------------------- force

	/**
	 * Suppress access check against a reflection object. SecurityException is silently ignored.
	 * Checks first if the object is already accessible.
	 */
	public static void forceAccess(AccessibleObject accObject){
		if (accObject.isAccessible() == true) {
			return;
		}
		try {
			accObject.setAccessible(true);
		} catch (SecurityException sex) {
			// ignore
		}
	}

	/**
	 * Sets fields modifiers.
	 * <p>
	 * Warning: this is a hack! Usage may not be as expected.
	 * All changes to fields modifiers must be applied
	 * <b>before</b> first usage.
	 */
	public static void setFieldModifiers(Field field, int modifiers) {
		try {
			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(field, field.getModifiers() | modifiers);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Unsets fields modifiers. For example, it may remove <code>final</code>
	 * modifier on static fields.
	 * <p>
	 * Warning: this is a hack! Usage may not be as expected.
	 * All changes to fields  modifiers must be applied
	 * <b>before</b> first usage.
	 */
	public static void unsetFieldModifiers(Field field, int modifiers) {
		try {
			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(field, field.getModifiers() & ~modifiers);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	// ---------------------------------------------------------------- is public

	/**
	 * Returns <code>true</code> if class member is public.
	 */
	public static boolean isPublic(Member member) {
		return Modifier.isPublic(member.getModifiers());
	}

	/**
	 * Returns <code>true</code> if class member is public and if its declaring class is also public.
	 */
	public static boolean isPublicPublic(Member member) {
		if (Modifier.isPublic(member.getModifiers()) == true) {
			if (Modifier.isPublic(member.getDeclaringClass().getModifiers())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns <code>true</code> if class is public.
	 */
	public static boolean isPublic(Class c) {
		return Modifier.isPublic(c.getModifiers());
	}


	// ---------------------------------------------------------------- create


	/**
	 * Creates new intances including for common mutable classes that do not have a default constructor. 
	 * more user-friendly. It examines if class is a map, list,
	 * String, Character, Boolean or a Number. Immutable instances are cached and not created again.
	 * Arrays are also created with no elements. Note that this bunch of ifs is faster then a hashmap.
	 */
	public static Object newInstance(Class type) throws IllegalAccessException, InstantiationException {
		if (type.isPrimitive()) {
			if (type == int.class) {
				return Integer.valueOf(0);
			}
			if (type == long.class) {
				return Long.valueOf(0);
			}
			if (type == boolean.class) {
				return Boolean.FALSE;
			}
			if (type == float.class) {
				return Float.valueOf(0);
			}
			if (type == double.class) {
				return Double.valueOf(0);
			}
			if (type == byte.class) {
				return Byte.valueOf((byte) 0);
			}
			if (type == short.class) {
				return Short.valueOf((short) 0);
			}
			if (type == char.class) {
				return Character.valueOf((char) 0);
			}
			throw new IllegalArgumentException("Invalid primitive type: " + type);
		}
		if (type == Integer.class) {
			return Integer.valueOf(0);
		}
		if (type == String.class) {
			return StringPool.EMPTY;
		}
		if (type == Long.class) {
			return Long.valueOf(0);
		}
		if (type == Boolean.class) {
			return Boolean.FALSE;
		}
		if (type == Float.class) {
			Float.valueOf(0);
		}
		if (type == Double.class) {
			Double.valueOf(0);
		}

		if (type == Map.class) {
			return new HashMap();
		}
		if (type == List.class) {
			return new ArrayList();
		}
		if (type == Set.class) {
			return new LinkedHashSet();
		}
		if (type == Collection.class) {
			return new ArrayList();
		}

		if (type == Byte.class) {
			return Byte.valueOf((byte) 0);
		}
		if (type == Short.class) {
			return Short.valueOf((short) 0);
		}
		if (type == Character.class) {
			return Character.valueOf((char) 0);
		}

		if (type.isEnum() == true) {
			return type.getEnumConstants()[0];
		}

		if (type.isArray() == true) {
			return Array.newInstance(type.getComponentType(), 0);
		}

		return type.newInstance();
	}


	// ---------------------------------------------------------------- misc

	/**
	 * Returns <code>true</code> if the first member is accessable from second one.
	 */
	public static boolean isAssignableFrom(Member member1, Member member2) {
		return member1.getDeclaringClass().isAssignableFrom(member2.getDeclaringClass());
	}

	/**
	 * Returns all superclasses.
	 */
	public static Class[] getSuperclasses(Class type) {
		int i = 0;
		for (Class x = type.getSuperclass(); x != null; x = x.getSuperclass()) {
			i++;
		}
		Class[] result = new Class[i];
		i = 0;
		for (Class x = type.getSuperclass(); x != null; x = x.getSuperclass()) {
			result[i] = x;
			i++;
		}
		return result;
	}

	/**
	 * Returns <code>true</code> if method is user defined and not defined in <code>Object</code> class.
	 */
	public static boolean isUserDefinedMethod(final Method method) {
		return method.getDeclaringClass() != Object.class;
	}

	/**
	 * Returns <code>true</code> if method defined in <code>Object</code> class.
	 */
	public static boolean isObjectMethod(final Method method) {
		return method.getDeclaringClass() == Object.class;
	}


	/**
	 * Returns <code>true</code> if method is a bean property.
	 */
	public static boolean isBeanProperty(Method method) {
		if (isObjectMethod(method)) {
			return false;
		}
		String methodName = method.getName();
		Class returnType = method.getReturnType();
		Class[] paramTypes =  method.getParameterTypes();
		if (methodName.startsWith(METHOD_GET_PREFIX)) {		// getter method must starts with 'get' and it is not getClass()
			if ((returnType != null) && (paramTypes.length == 0)) {	// getter must have a return type and no arguments
				return true;
			}
		} else if (methodName.startsWith(METHOD_IS_PREFIX)) {		    // ister must starts with 'is'
			if ((returnType != null)  && (paramTypes.length == 0)) {	// ister must have return type and no arguments
				return true;
			}
		} else if (methodName.startsWith(METHOD_SET_PREFIX)) {	// setter must start with a 'set'
			if (paramTypes.length == 1) {				        // setter must have just one argument
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns <code>true</code> if method is bean getter.
	 */
	public static boolean isBeanPropertyGetter(Method method) {
		return getBeanPropertyGetterPrefixLength(method) != 0;
	}

	private static int getBeanPropertyGetterPrefixLength(Method method) {
		if (isObjectMethod(method)) {
			return 0;
		}
		String methodName = method.getName();
		Class returnType = method.getReturnType();
		Class[] paramTypes =  method.getParameterTypes();
		if (methodName.startsWith(METHOD_GET_PREFIX)) {		        // getter method must starts with 'get' and it is not getClass()
			if ((returnType != null) && (paramTypes.length == 0)) {	// getter must have a return type and no arguments
				return 3;
			}
		} else if (methodName.startsWith(METHOD_IS_PREFIX)) {		    // ister must starts with 'is'
			if ((returnType != null)  && (paramTypes.length == 0)) {	// ister must have return type and no arguments
				return 2;
			}
		}
		return 0;
	}

	/**
	 * Returns beans property getter name or <code>null</code> if method is not a real getter.
	 */
	public static String getBeanPropertyGetterName(Method method) {
		int prefixLength = getBeanPropertyGetterPrefixLength(method);
		if (prefixLength == 0) {
			return null;
		}
		String methodName = method.getName().substring(prefixLength);
		return Introspector.decapitalize(methodName);
	}

	/**
	 * Returns <code>true</code> if method is bean setter.
	 */
	public static boolean isBeanPropertySetter(Method method) {
		return getBeanPropertySetterPrefixLength(method) != 0;
	}

	private static int getBeanPropertySetterPrefixLength(Method method) {
		if (isObjectMethod(method)) {
			return 0;
		}
		String methodName = method.getName();
		Class[] paramTypes =  method.getParameterTypes();
		if (methodName.startsWith(METHOD_SET_PREFIX)) {	        // setter must start with a 'set'
			if (paramTypes.length == 1) {				        // setter must have just one argument
				return 3;
			}
		}
		return 0;
	}

	/**
	 * Returns beans property setter name or <code>null</code> if method is not a real setter.
	 */
	public static String getBeanPropertySetterName(Method method) {
		int prefixLength = getBeanPropertySetterPrefixLength(method);
		if (prefixLength == 0) {
			return null;
		}
		String methodName = method.getName().substring(prefixLength);
		return Introspector.decapitalize(methodName);
	}

	// ---------------------------------------------------------------- generics

	/**
	 * Returns component type of the given <code>type</code>.
	 * For <code>ParameterizedType</code> it returns the last type in array.
	 */
	public static Class getComponentType(Type type) {
		return getComponentType(type, -1);
	}

	/**
	 * Returns the component type of the given <code>type</code>.<br>
	 * For example the following types all have the component-type MyClass:
	 * <ul>
	 * <li>MyClass[]</li>
	 * <li>List&lt;MyClass&gt;</li>
	 * <li>Foo&lt;? extends MyClass&gt;</li>
	 * <li>Bar&lt;? super MyClass&gt;</li>
	 * <li>&lt;T extends MyClass&gt; T[]</li>
	 * </ul>
	 *
	 * @param type is the type where to get the component type from.
	 * @return the component type of the given <code>type</code> or
	 *         <code>null</code> if the given <code>type</code> does NOT have
	 *         a single (component) type.
	 */
	public static Class getComponentType(Type type, int index) {
		if (type instanceof Class) {
			Class clazz = (Class) type;
			if (clazz.isArray()) {
				return clazz.getComponentType();
			}
		} else if (type instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) type;
			Type[] generics = pt.getActualTypeArguments();
			if (index < 0) {
				index = generics.length + index;
			}
			if (index < generics.length) {
				return toClass(generics[index]);
			}
		} else if (type instanceof GenericArrayType) {
			GenericArrayType gat = (GenericArrayType) type;
			return toClass(gat.getGenericComponentType());
		}
		return null;
	}

	public static Class getGenericSupertype(Class type, int index) {
		return getComponentType(type.getGenericSuperclass(), index);
	}

	public static Class getGenericSupertype(Class type) {
		return getComponentType(type.getGenericSuperclass());
	}


	/**
	 * Returns {@link Class} for the given <code>type</code>.<br>
	 * Examples: <br>
	 * <table border="1">
	 * <tr>
	 * <th><code>type</code></th>
	 * <th><code>getSimpleType(type)</code></th>
	 * </tr>
	 * <tr>
	 * <td><code>String</code></td>
	 * <td><code>String</code></td>
	 * </td>
	 * <tr>
	 * <td><code>List&lt;String&gt;</code></td>
	 * <td><code>List</code></td>
	 * </td>
	 * <tr>
	 * <td><code>&lt;T extends MyClass&gt; T[]</code></td>
	 * <td><code>MyClass[]</code></td>
	 * </td>
	 * </table>
	 *
	 * @param type is the type to convert.
	 * @return the closest class representing the given <code>type</code>.
	 */
	public static Class toClass(Type type) {
		if (type instanceof Class) {
			return (Class) type;
		} else if (type instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) type;
			return toClass(pt.getRawType());
		} else if (type instanceof WildcardType) {
			WildcardType wt = (WildcardType) type;
			Type[] lower = wt.getLowerBounds();
			if (lower.length == 1) {
				return toClass(lower[0]);
			}
			Type[] upper = wt.getUpperBounds();
			if (upper.length == 1) {
				return toClass(upper[0]);
			}
		} else if (type instanceof GenericArrayType) {
			GenericArrayType gat = (GenericArrayType) type;
			Class componentType = toClass(gat.getGenericComponentType());
			// this is sort of stupid but there seems no other way...
			return Array.newInstance(componentType, 0).getClass();
		} else if (type instanceof TypeVariable) {
			TypeVariable tv = (TypeVariable) type;
			Type[] bounds = tv.getBounds();
			if (bounds.length == 1) {
				return toClass(bounds[0]);
			}
		}
		return null;
	}


	// ---------------------------------------------------------------- annotations

	/**
	 * Reads annotation value. Returns <code>null</code> on error.
	 */
	public static Object readAnnotationValue(Annotation annotation, String name) {
		try {
			Method method  = annotation.annotationType().getDeclaredMethod(name);
			return method.invoke(annotation);
		} catch (Exception ignore) {
			return null;
		}
	}

}