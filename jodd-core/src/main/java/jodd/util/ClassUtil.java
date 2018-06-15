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

package jodd.util;

import jodd.util.cl.ClassLoaderStrategy;
import jodd.net.URLDecoder;

import java.io.File;
import java.io.IOException;
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
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;

/**
 * Class utilities.
 */
public class ClassUtil {

	/** Empty class array. */
	public static final Class[] EMPTY_CLASS_ARRAY = new Class[0];

	@SuppressWarnings("unchecked")
	public static <T> Class<T>[] emptyClassArray() {
		return EMPTY_CLASS_ARRAY;
	}

	public static final String METHOD_GET_PREFIX = "get";
	public static final String METHOD_IS_PREFIX = "is";
	public static final String METHOD_SET_PREFIX = "set";

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
	public static Method findMethod(final Class c, final String methodName) {
		return findDeclaredMethod(c, methodName, true);
	}

	/**
	 * @see #findMethod(Class, String)
	 */
	public static Method findDeclaredMethod(final Class c, final String methodName) {
		return findDeclaredMethod(c, methodName, false);
	}

	private static Method findDeclaredMethod(final Class c, final String methodName, final boolean publicOnly) {
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

	// ---------------------------------------------------------------- find ctor

	/**
	 * Finds constructor with given parameter types. First matched ctor is returned.
	 */
	public static <T> Constructor<T> findConstructor(final Class<T> clazz, final Class<?>... parameterTypes) {
		final Constructor<?>[] constructors = clazz.getConstructors();

		Class<?>[] pts;

		for (Constructor<?> constructor : constructors) {
			pts = constructor.getParameterTypes();

			if (isAllAssignableFrom(pts, parameterTypes)) {
				return (Constructor<T>) constructor;
			}
		}
		return null;
	}

	/**
	 * Returns {@code true} if all types are assignable from the other array of types.
	 */
	public static boolean isAllAssignableFrom(final Class<?>[] typesTarget, final Class<?>[] typesFrom) {
		if (typesTarget.length == typesFrom.length) {
			for (int i = 0; i < typesTarget.length; i++) {
				if (!typesTarget[i].isAssignableFrom(typesFrom[i])) {
					return false;
				}
			}
			return true;
		}
		return false;
	}


	// ---------------------------------------------------------------- classes

	/**
	 * Returns classes from array of objects. It accepts {@code null}
	 * values.
	 */
	public static Class[] getClasses(final Object... objects) {
		if (objects.length == 0) {
			return EMPTY_CLASS_ARRAY;
		}
		Class[] result = new Class[objects.length];
		for (int i = 0; i < objects.length; i++) {
			if (objects[i] != null) {
				result[i] = objects[i].getClass();
			}
		}
		return result;
	}

	// ---------------------------------------------------------------- match classes

	/**
	 * Safe version of <code>isAssignableFrom</code> method that
	 * returns <code>false</code> if one of the arguments is <code>null</code>.
	 */
	public static boolean isTypeOf(final Class<?> lookupClass, final Class<?> targetClass) {
		if (targetClass == null || lookupClass == null) {
			return false;
		}
		return targetClass.isAssignableFrom(lookupClass);
	}

	/**
	 * Safe version of <code>isInstance</code>, returns <code>false</code>
	 * if any of the arguments is <code>null</code>.
	 */
	public static boolean isInstanceOf(final Object object, final Class target) {
		if (object == null || target == null) {
			return false;
		}
		return target.isInstance(object);
	}

	/**
	 * Resolves all interfaces of a type. No duplicates are returned.
	 * Direct interfaces are prior the interfaces of subclasses in
	 * the returned array.
	 */
	public static Class[] resolveAllInterfaces(final Class type) {
		Set<Class> bag = new LinkedHashSet<>();
		_resolveAllInterfaces(type, bag);

		return bag.toArray(new Class[0]);
	}

	private static void _resolveAllInterfaces(final Class type, final Set<Class> bag) {
		// add types interfaces
		Class[] interfaces = type.getInterfaces();
		Collections.addAll(bag, interfaces);

		// resolve interfaces of each interface
		for (Class iface : interfaces) {
			_resolveAllInterfaces(iface, bag);
		}

		// continue with super type
		Class superClass = type.getSuperclass();

		if (superClass == null) {
			return;
		}

		if (superClass == Object.class) {
			return;
		}

		_resolveAllInterfaces(type.getSuperclass(), bag);
	}

	/**
	 * Resolves all super classes, from top (direct subclass) to down. <code>Object</code>
	 * class is not included in the list.
	 */
	public static Class[] resolveAllSuperclasses(Class type) {
		List<Class> list = new ArrayList<>();

		while (true) {
			type = type.getSuperclass();

			if ((type == null) || (type == Object.class)) {
				break;
			}
			list.add(type);
		}

		return list.toArray(new Class[0]);
	}

	// ---------------------------------------------------------------- accessible methods


	/**
	 * Returns array of all methods that are accessible from given class.
	 * @see #getAccessibleMethods(Class, Class)
	 */
	public static Method[] getAccessibleMethods(final Class clazz) {
		return getAccessibleMethods(clazz, Object.class);
	}

	/**
	 * Returns array of all methods that are accessible from given class, upto limit
	 * (usually <code>Object.class</code>). Abstract methods are ignored.
	 */
	public static Method[] getAccessibleMethods(Class clazz, final Class limit) {
		Package topPackage = clazz.getPackage();
		List<Method> methodList = new ArrayList<>();
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
				if (top) {				// add all top declared methods
					methodList.add(method);
					continue;
				}
				int modifier = method.getModifiers();
				if (Modifier.isPrivate(modifier)) {
					continue;										// ignore super private methods
				}
				if (Modifier.isAbstract(modifier)) {		// ignore super abstract methods
					continue;
				}
				if (Modifier.isPublic(modifier)) {
					addMethodIfNotExist(methodList, method);		// add super public methods
					continue;
				}
				if (Modifier.isProtected(modifier)) {
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

	private static void addMethodIfNotExist(final List<Method> allMethods, final Method newMethod) {
		for (Method m : allMethods) {
			if (compareSignatures(m, newMethod)) {
				return;
			}
		}
		allMethods.add(newMethod);
	}

	// ---------------------------------------------------------------- accessible fields


	public static Field[] getAccessibleFields(final Class clazz) {
		return getAccessibleFields(clazz, Object.class);
	}

	public static Field[] getAccessibleFields(Class clazz, final Class limit) {
		Package topPackage = clazz.getPackage();
		List<Field> fieldList = new ArrayList<>();
		int topPackageHash = topPackage == null ? 0 : topPackage.hashCode();
		boolean top = true;
		do {
			if (clazz == null) {
				break;
			}
			Field[] declaredFields = clazz.getDeclaredFields();
			for (Field field : declaredFields) {
				if (top) {				// add all top declared fields
					fieldList.add(field);
					continue;
				}
				int modifier = field.getModifiers();
				if (Modifier.isPrivate(modifier)) {
					continue;										// ignore super private fields
				}
				if (Modifier.isPublic(modifier)) {
					addFieldIfNotExist(fieldList, field);			// add super public methods
					continue;
				}
				if (Modifier.isProtected(modifier)) {
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

	private static void addFieldIfNotExist(final List<Field> allFields, final Field newField) {
		for (Field f : allFields) {
			if (compareSignatures(f, newField)) {
				return;
			}
		}
		allFields.add(newField);
	}


	// ---------------------------------------------------------------- supported methods


	public static Method[] getSupportedMethods(final Class clazz) {
		return getSupportedMethods(clazz, Object.class);
	}

	/**
	 * Returns a <code>Method</code> array of the methods to which instances of the specified
	 * respond except for those methods defined in the class specified by limit
	 * or any of its superclasses. Note that limit is usually used to eliminate
	 * them methods defined by <code>java.lang.Object</code>. If limit is <code>null</code> then all
	 * methods are returned.
	 */
	public static Method[] getSupportedMethods(final Class clazz, final Class limit) {
		ArrayList<Method> supportedMethods = new ArrayList<>();
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
				if (!found) {
					supportedMethods.add(method);
				}
			}
		}
		return supportedMethods.toArray(new Method[0]);
	}


	public static Field[] getSupportedFields(final Class clazz) {
		return getSupportedFields(clazz, Object.class);
	}

	public static Field[] getSupportedFields(final Class clazz, final Class limit) {
		ArrayList<Field> supportedFields = new ArrayList<>();
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
				if (!found) {
					supportedFields.add(field);
				}
			}
		}
		return supportedFields.toArray(new Field[0]);
	}


	// ---------------------------------------------------------------- compare

	/**
	 * Compares method declarations: signature and return types.
	 */
	public static boolean compareDeclarations(final Method first, final Method second) {
		if (first.getReturnType() != second.getReturnType()) {
			return false;
		}
		return compareSignatures(first, second);
	}

	/**
	 * Compares method signatures: names and parameters.
	 */
	public static boolean compareSignatures(final Method first, final Method second) {
		if (!first.getName().equals(second.getName())) {
			return false;
		}
		return compareParameters(first.getParameterTypes(), second.getParameterTypes());
	}

	/**
	 * Compares constructor signatures: names and parameters.
	 */
	public static boolean compareSignatures(final Constructor first, final Constructor second) {
		if (!first.getName().equals(second.getName())) {
			return false;
		}
		return compareParameters(first.getParameterTypes(), second.getParameterTypes());
	}

	public static boolean compareSignatures(final Field first, final Field second) {
		return first.getName().equals(second.getName());
	}

	/**
	 * Compares classes, usually method or ctor parameters.
	 */
	public static boolean compareParameters(final Class[] first, final Class[] second) {
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
	public static void forceAccess(final AccessibleObject accObject) {
		try {
			if (System.getSecurityManager() == null)
				accObject.setAccessible(true);
			else {
				AccessController.doPrivileged((PrivilegedAction) () -> {
					accObject.setAccessible(true);
					return null;
				});
			}
		} catch (SecurityException sex) {
			// ignore
		}
	}

	// ---------------------------------------------------------------- is public

	/**
	 * Returns <code>true</code> if class member is public.
	 */
	public static boolean isPublic(final Member member) {
		return Modifier.isPublic(member.getModifiers());
	}

	/**
	 * Returns <code>true</code> if class member is public and if its declaring class is also public.
	 */
	public static boolean isPublicPublic(final Member member) {
		if (Modifier.isPublic(member.getModifiers())) {
			if (Modifier.isPublic(member.getDeclaringClass().getModifiers())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns <code>true</code> if class is public.
	 */
	public static boolean isPublic(final Class c) {
		return Modifier.isPublic(c.getModifiers());
	}


	// ---------------------------------------------------------------- create

	/**
	 * Creates new instance of given class with given optional arguments.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(final Class<T> clazz, final Object... params) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (params.length == 0) {
			return newInstance(clazz);
		}

		final Class<?>[] paramTypes = getClasses(params);

		final Constructor<?> constructor = findConstructor(clazz, paramTypes);

		if (constructor == null) {
			throw new InstantiationException("No constructor matched parameter types.");
		}

		return (T) constructor.newInstance(params);
	}


	/**
	 * Creates new instances including for common mutable classes that do not have a default constructor.
	 * more user-friendly. It examines if class is a map, list,
	 * String, Character, Boolean or a Number. Immutable instances are cached and not created again.
	 * Arrays are also created with no elements. Note that this bunch of <code>if</code> blocks
	 * is faster then using a <code>HashMap</code>.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(final Class<T> type) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
		if (type.isPrimitive()) {
			if (type == int.class) {
				return (T) Integer.valueOf(0);
			}
			if (type == long.class) {
				return (T) Long.valueOf(0);
			}
			if (type == boolean.class) {
				return (T) Boolean.FALSE;
			}
			if (type == float.class) {
				return (T) Float.valueOf(0);
			}
			if (type == double.class) {
				return (T) Double.valueOf(0);
			}
			if (type == byte.class) {
				return (T) Byte.valueOf((byte) 0);
			}
			if (type == short.class) {
				return (T) Short.valueOf((short) 0);
			}
			if (type == char.class) {
				return (T) Character.valueOf((char) 0);
			}
			throw new IllegalArgumentException("Invalid primitive: " + type);
		}

		if (type.getName().startsWith("java.")) {

			if (type == Integer.class) {
				return (T) Integer.valueOf(0);
			}
			if (type == String.class) {
				return (T) StringPool.EMPTY;
			}
			if (type == Long.class) {
				return (T) Long.valueOf(0);
			}
			if (type == Boolean.class) {
				return (T) Boolean.FALSE;
			}
			if (type == Float.class) {
				return (T) Float.valueOf(0);
			}
			if (type == Double.class) {
				return (T) Double.valueOf(0);
			}

			if (type == Map.class) {
				return (T) new HashMap();
			}
			if (type == List.class) {
				return (T) new ArrayList();
			}
			if (type == Set.class) {
				return (T) new HashSet();
			}
			if (type == Collection.class) {
				return (T) new ArrayList();
			}

			if (type == Byte.class) {
				return (T) Byte.valueOf((byte) 0);
			}
			if (type == Short.class) {
				return (T) Short.valueOf((short) 0);
			}
			if (type == Character.class) {
				return (T) Character.valueOf((char) 0);
			}
		}

		if (type.isEnum()) {
			return type.getEnumConstants()[0];
		}

		if (type.isArray()) {
			return (T) Array.newInstance(type.getComponentType(), 0);
		}

		Constructor<T> declaredConstructor = type.getDeclaredConstructor();

		forceAccess(declaredConstructor);

		return declaredConstructor.newInstance();
	}


	// ---------------------------------------------------------------- misc

	/**
	 * Returns <code>true</code> if the first member is accessible from second one.
	 */
	public static boolean isAssignableFrom(final Member member1, final Member member2) {
		return member1.getDeclaringClass().isAssignableFrom(member2.getDeclaringClass());
	}

	/**
	 * Returns all superclasses.
	 */
	public static Class[] getSuperclasses(final Class type) {
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
	public static boolean isBeanProperty(final Method method) {
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
	public static boolean isBeanPropertyGetter(final Method method) {
		return getBeanPropertyGetterPrefixLength(method) != 0;
	}

	private static int getBeanPropertyGetterPrefixLength(final Method method) {
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
	 * Returns property name from a getter method.
	 * Returns <code>null</code> if method is not a real getter.
	 */
	public static String getBeanPropertyGetterName(final Method method) {
		int prefixLength = getBeanPropertyGetterPrefixLength(method);
		if (prefixLength == 0) {
			return null;
		}
		String methodName = method.getName().substring(prefixLength);
		return StringUtil.decapitalize(methodName);
	}

	/**
	 * Returns <code>true</code> if method is bean setter.
	 */
	public static boolean isBeanPropertySetter(final Method method) {
		return getBeanPropertySetterPrefixLength(method) != 0;
	}

	private static int getBeanPropertySetterPrefixLength(final Method method) {
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
	public static String getBeanPropertySetterName(final Method method) {
		int prefixLength = getBeanPropertySetterPrefixLength(method);
		if (prefixLength == 0) {
			return null;
		}
		String methodName = method.getName().substring(prefixLength);
		return StringUtil.decapitalize(methodName);
	}

	// ---------------------------------------------------------------- generics

	/**
	 * Returns single component type. Index is used when type consist of many
	 * components. If negative, index will be calculated from the end of the
	 * returned array. Returns <code>null</code> if component type
	 * does not exist or if index is out of bounds.
	 *
	 * @see #getComponentTypes(java.lang.reflect.Type)
	 */
	public static Class getComponentType(final Type type, final int index) {
		return getComponentType(type, null, index);
	}

	/**
	 * Returns single component type for given type and implementation.
	 * Index is used when type consist of many
	 * components. If negative, index will be calculated from the end of the
	 * returned array.  Returns <code>null</code> if component type
	 * does not exist or if index is out of bounds.
	 * <p>
	 *
	 * @see #getComponentTypes(java.lang.reflect.Type, Class)
	 */
	public static Class getComponentType(final Type type, final Class implClass, int index) {
		Class[] componentTypes = getComponentTypes(type, implClass);
		if (componentTypes == null) {
			return null;
		}

		if (index < 0) {
			index += componentTypes.length;
		}

		if (index >= componentTypes.length) {
			return null;
		}

		return componentTypes[index];
	}

	/**
	 * @see #getComponentTypes(java.lang.reflect.Type, Class)
	 */
	public static Class[] getComponentTypes(final Type type) {
		return getComponentTypes(type, null);
	}

	/**
	 * Returns all component types of the given type.
	 * For example the following types all have the
	 * component-type MyClass:
	 * <ul>
	 * <li>MyClass[]</li>
	 * <li>List&lt;MyClass&gt;</li>
	 * <li>Foo&lt;? extends MyClass&gt;</li>
	 * <li>Bar&lt;? super MyClass&gt;</li>
	 * <li>&lt;T extends MyClass&gt; T[]</li>
	 * </ul>
	 */
	public static Class[] getComponentTypes(final Type type, final Class implClass) {
		if (type instanceof Class) {
			Class clazz = (Class) type;
			if (clazz.isArray()) {
				return new Class[] {clazz.getComponentType()};
			}
		}
		else if (type instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) type;

			Type[] generics = pt.getActualTypeArguments();

			if (generics.length == 0) {
				return null;
			}

			Class[] types = new Class[generics.length];

			for (int i = 0; i < generics.length; i++) {
				types[i] = getRawType(generics[i], implClass);
			}
			return types;
		}
		else if (type instanceof GenericArrayType) {
			GenericArrayType gat = (GenericArrayType) type;

			Class rawType = getRawType(gat.getGenericComponentType(), implClass);
			if (rawType == null) {
				return null;
			}

			return new Class[] {rawType};
		}
		return null;
	}

	/**
	 * Shortcut for <code>getComponentTypes(type.getGenericSuperclass())</code>.
	 *
	 * @see #getComponentTypes(java.lang.reflect.Type)
	 */
	public static Class[] getGenericSupertypes(final Class type) {
		return getComponentTypes(type.getGenericSuperclass());
	}

	/**
	 * Shortcut for <code>getComponentType(type.getGenericSuperclass())</code>.
	 *
	 * @see #getComponentType(java.lang.reflect.Type, int)
	 */
	public static Class getGenericSupertype(final Class type, final int index) {
		return getComponentType(type.getGenericSuperclass(), index);
	}


	/**
	 * Returns raw class for given <code>type</code>. Use this method with both
	 * regular and generic types.
	 *
	 * @param type the type to convert
	 * @return the closest class representing the given <code>type</code>
	 * @see #getRawType(java.lang.reflect.Type, Class)
	 */
	public static Class getRawType(final Type type) {
		return getRawType(type, null);
	}

	/**
	 * Returns raw class for given <code>type</code> when implementation class is known
	 * and it makes difference.
	 * @see #resolveVariable(java.lang.reflect.TypeVariable, Class)
	 */
	public static Class<?> getRawType(final Type type, final Class implClass) {
		if (type instanceof Class) {
			return (Class) type;
		}
		if (type instanceof ParameterizedType) {
			ParameterizedType pType = (ParameterizedType) type;
			return getRawType(pType.getRawType(), implClass);
		}
		if (type instanceof WildcardType) {
			WildcardType wType = (WildcardType) type;

			Type[] lowerTypes = wType.getLowerBounds();
			if (lowerTypes.length > 0) {
				return getRawType(lowerTypes[0], implClass);
			}

			Type[] upperTypes = wType.getUpperBounds();
			if (upperTypes.length != 0) {
				return getRawType(upperTypes[0], implClass);
			}

			return Object.class;
		}
		if (type instanceof GenericArrayType) {
			Type genericComponentType = ((GenericArrayType) type).getGenericComponentType();
			Class<?> rawType = getRawType(genericComponentType, implClass);
			// this is sort of stupid, but there seems no other way (consider don't creating new instances each time)...
			return Array.newInstance(rawType, 0).getClass();
		}
		if (type instanceof TypeVariable) {
			TypeVariable<?> varType = (TypeVariable<?>) type;
			if (implClass != null) {
				Type resolvedType = resolveVariable(varType, implClass);
				if (resolvedType != null) {
					return getRawType(resolvedType, null);
				}
			}
			Type[] boundsTypes = varType.getBounds();
			if (boundsTypes.length == 0) {
				return Object.class;
			}
			return getRawType(boundsTypes[0], implClass);
		}
		return null;
	}


	/**
	 * Resolves <code>TypeVariable</code> with given implementation class.
	 */
	public static Type resolveVariable(final TypeVariable variable, final Class implClass) {
		final Class rawType = getRawType(implClass, null);

		int index = ArraysUtil.indexOf(rawType.getTypeParameters(), variable);
		if (index >= 0) {
			return variable;
		}

		final Class[] interfaces = rawType.getInterfaces();
		final Type[] genericInterfaces = rawType.getGenericInterfaces();

		for (int i = 0; i <= interfaces.length; i++) {
			Class rawInterface;

			if (i < interfaces.length) {
				rawInterface = interfaces[i];
			} else {
				rawInterface = rawType.getSuperclass();
				if (rawInterface == null) {
					continue;
				}
			}

			final Type resolved = resolveVariable(variable, rawInterface);
			if (resolved instanceof Class || resolved instanceof ParameterizedType) {
				return resolved;
			}

			if (resolved instanceof TypeVariable) {
				final TypeVariable typeVariable = (TypeVariable) resolved;
				index = ArraysUtil.indexOf(rawInterface.getTypeParameters(), typeVariable);

				if (index < 0) {
					throw new IllegalArgumentException("Invalid type variable:" + typeVariable);
				}

				final Type type = i < genericInterfaces.length ? genericInterfaces[i] : rawType.getGenericSuperclass();

				if (type instanceof Class) {
					return Object.class;
				}

				if (type instanceof ParameterizedType) {
					return ((ParameterizedType) type).getActualTypeArguments()[index];
				}

				throw new IllegalArgumentException("Unsupported type: " + type);
			}
		}
		return null;
	}

	/**
	 * Converts <code>Type</code> to a <code>String</code>. Supports successor interfaces:
	 * <ul>
	 * <li><code>java.lang.Class</code> - represents usual class</li>
	 * <li><code>java.lang.reflect.ParameterizedType</code> - class with generic parameter (e.g. <code>List</code>)</li>
	 * <li><code>java.lang.reflect.TypeVariable</code> - generic type literal (e.g. <code>List</code>, <code>T</code> - type variable)</li>
	 * <li><code>java.lang.reflect.WildcardType</code> - wildcard type (<code>List&lt;? extends Number&gt;</code>, <code>"? extends Number</code> - wildcard type)</li>
	 * <li><code>java.lang.reflect.GenericArrayType</code> - type for generic array (e.g. <code>T[]</code>, <code>T</code> - array type)</li>
	 * </ul>
	 */
	public static String typeToString(final Type type) {
		StringBuilder sb = new StringBuilder();
		typeToString(sb, type, new HashSet<Type>());
		return sb.toString();
	}

	private static void typeToString(final StringBuilder sb, final Type type, final Set<Type> visited) {
		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			final Class<?> rawType = (Class<?>) parameterizedType.getRawType();
			sb.append(rawType.getName());
			boolean first = true;
			for (Type typeArg : parameterizedType.getActualTypeArguments()) {
				if (first) {
					first = false;
				} else {
					sb.append(", ");
				}
				sb.append('<');
				typeToString(sb, typeArg, visited);
				sb.append('>');
			}
		} else if (type instanceof WildcardType) {
			WildcardType wildcardType = (WildcardType) type;
			sb.append('?');

			// According to JLS(http://java.sun.com/docs/books/jls/third_edition/html/typesValues.html#4.5.1):
			// - Lower and upper can't coexist: (for instance, this is not allowed: <? extends List<String> & super MyInterface>)
			// - Multiple bounds are not supported (for instance, this is not allowed: <? extends List<String> & MyInterface>)

			final Type bound;
			if (wildcardType.getLowerBounds().length != 0) {
				sb.append(" super ");
				bound = wildcardType.getLowerBounds()[0];
			} else {
				sb.append(" extends ");
				bound = wildcardType.getUpperBounds()[0];
			}
			typeToString(sb, bound, visited);
		} else if (type instanceof TypeVariable<?>) {
			TypeVariable<?> typeVariable = (TypeVariable<?>) type;
			sb.append(typeVariable.getName());

			// prevent cycles in case: <T extends List<T>>

			if (!visited.contains(type)) {
				visited.add(type);
				sb.append(" extends ");
				boolean first = true;
				for (Type bound : typeVariable.getBounds()) {
					if (first) {
						first = false;
					} else {
						sb.append(" & ");
					}
					typeToString(sb, bound, visited);
				}
				visited.remove(type);
			}
		} else if (type instanceof GenericArrayType) {
			GenericArrayType genericArrayType = (GenericArrayType) type;
			typeToString(genericArrayType.getGenericComponentType());
			sb.append(genericArrayType.getGenericComponentType());
			sb.append("[]");
		} else if (type instanceof Class) {
			Class<?> typeClass = (Class<?>) type;
			sb.append(typeClass.getName());
		} else {
			throw new IllegalArgumentException("Unsupported type: " + type);
		}
	}

	// ---------------------------------------------------------------- annotations

	/**
	 * Reads annotation value. Returns <code>null</code> on error
	 * (e.g. when value name not found).
	 */
	public static Object readAnnotationValue(final Annotation annotation, final String name) {
		try {
			Method method  = annotation.annotationType().getDeclaredMethod(name);
			return method.invoke(annotation);
		} catch (Exception ignore) {
			return null;
		}
	}

	// ---------------------------------------------------------------- caller

	private static class ReflectUtilSecurityManager extends SecurityManager {
		public Class getCallerClass(final int callStackDepth) {
			return getClassContext()[callStackDepth + 1];
		}
	}

	private static ReflectUtilSecurityManager SECURITY_MANAGER;

	static {
		try {
			SECURITY_MANAGER = new ReflectUtilSecurityManager();
		} catch (Exception ex) {
			SECURITY_MANAGER = null;
		}
	}

	/**
	 * Emulates <code>Reflection.getCallerClass</code> using standard API.
	 * This implementation uses custom <code>SecurityManager</code>
	 * and it is the fastest. Other implementations are:
	 * <ul>
	 * <li><code>new Throwable().getStackTrace()[callStackDepth]</code></li>
	 * <li><code>Thread.currentThread().getStackTrace()[callStackDepth]</code> (the slowest)</li>
	 * </ul>
	 * <p>
	 * In case when usage of <code>SecurityManager</code> is not allowed,
	 * this method fails back to the second implementation.
	 * <p>
	 * Note that original <code>Reflection.getCallerClass</code> is way faster
	 * then any emulation.
	 */
	public static Class getCallerClass(int framesToSkip) {
		if (SECURITY_MANAGER != null) {
			return SECURITY_MANAGER.getCallerClass(framesToSkip);
		}

		StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();

		if (framesToSkip >= 2) {
			framesToSkip += 4;
		}

		String className = stackTraceElements[framesToSkip].getClassName();

		try {
			return Thread.currentThread().getContextClassLoader().loadClass(className);
		} catch (ClassNotFoundException cnfex) {
			throw new UnsupportedOperationException(className + " not found.");
		}
	}

	/**
	 * Smart variant of {@link #getCallerClass(int)} that skips all relevant Jodd calls.
	 * However, this one does not use the security manager.
	 */
	public static Class getCallerClass() {
		String className = null;
		StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();

		for (StackTraceElement stackTraceElement : stackTraceElements) {
			className = stackTraceElement.getClassName();
			String methodName = stackTraceElement.getMethodName();

			if (methodName.equals("loadClass")) {
				if (className.contains(ClassLoaderStrategy.class.getSimpleName())) {
					continue;
				}
				if (className.equals(ClassLoaderUtil.class.getName())) {
					continue;
				}
			} else if (methodName.equals("getCallerClass")) {
				continue;
			}
			break;
		}

		try {
			return Thread.currentThread().getContextClassLoader().loadClass(className);
		} catch (ClassNotFoundException cnfex) {
			throw new UnsupportedOperationException(className + " not found.");
		}
	}

	// ---------------------------------------------------------------- enum

	/**
	 * Returns <code>enum</code> class or <code>null</code> if class is not an enum.
	 */
	public static Class findEnum(Class target) {
		if (target.isPrimitive()) {
			return null;
		}
		while (target != Object.class) {
			if (target.isEnum()) {
				return target;
			}

			target = target.getSuperclass();
		}

		return null;
	}


	// ---------------------------------------------------------------- misc

	/**
	 * Returns the class of the immediate subclass of the given parent class for
	 * the given object instance; or null if such immediate subclass cannot be
	 * uniquely identified for the given object instance.
	 */
	public static Class<?> childClassOf(final Class<?> parentClass, final Object instance) {

		if (instance == null || instance == Object.class) {
			return null;
		}

		if (parentClass != null) {
			if (parentClass.isInterface()) {
				return null;
			}
		}

		Class<?> childClass = instance.getClass();
		while (true) {
			Class<?> parent = childClass.getSuperclass();
			if (parent == parentClass) {
				return childClass;
			}
			if (parent == null) {
				return null;
			}
			childClass = parent;
		}
	}

	/**
	 * Returns the jar file from which the given class is loaded; or null
	 * if no such jar file can be located.
	 */
	public static JarFile jarFileOf(final Class<?> klass) {
		URL url = klass.getResource(
			"/" + klass.getName().replace('.', '/') + ".class");

		if (url == null) {
			return null;
		}

		String s = url.getFile();
		int beginIndex = s.indexOf("file:") + "file:".length();
		int endIndex = s.indexOf(".jar!");
		if (endIndex == -1) {
			return null;
		}

		endIndex += ".jar".length();
		String f = s.substring(beginIndex, endIndex);
		// decode URL string - it may contain encoded chars (e.g. whitespaces) which are not supported for file-instances
		f = URLDecoder.decode(f, "UTF-8");
		File file = new File(f);

		try {
			return file.exists() ? new JarFile(file) : null;
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	// ---------------------------------------------------------------- class names

	/**
	 * Resolves class file name from class name by replacing dot's with '/' separator
	 * and adding class extension at the end. If array, component type is returned.
	 */
	public static String convertClassNameToFileName(Class clazz) {
		if (clazz.isArray()) {
			clazz = clazz.getComponentType();
		}
		return convertClassNameToFileName(clazz.getName());
	}

	/**
	 * Resolves class file name from class name by replacing dot's with '/' separator.
	 */
	public static String convertClassNameToFileName(final String className) {
		return className.replace('.', '/') + ".class";
	}

	/**
	 * Returns short class name: packages are replaces with single letter.
	 */
	public static String getShortClassName(final Class clazz) {
		return getShortClassName(clazz, 1);
	}
	public static String getShortClassName(final Class clazz, final int shortUpTo) {
		final String[] chunks = StringUtil.splitc(clazz.getName(), '.');
		final StringBand stringBand = new StringBand(chunks.length);
		int ndx = chunks.length - shortUpTo;
		if (ndx < 0) {
			ndx = 0;
		}

		for (int i = 0; i < ndx; i++) {
			if (i > 0) {
				stringBand.append('.');
			}
			stringBand.append(chunks[i].charAt(0));
		}

		for (int i = ndx; i < chunks.length; i++) {
			if (i > 0) {
				stringBand.append('.');
			}
			stringBand.append(chunks[i]);
		}
		return stringBand.toString();
	}

	// ---------------------------------------------------------------- kotlin

	/**
	 * Returns {@code true} if type is a Kotlin class.
	 */
	public static boolean isKotlinClass(final Class type) {
		final Annotation[] annotations = type.getAnnotations();
		for (Annotation annotation : annotations) {
			if (annotation.annotationType().getName().equals("kotlin.Metadata")) {
				return true;
			}
		}
		return false;
	}

}