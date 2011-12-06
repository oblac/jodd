// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.paramo;

import jodd.util.ClassLoaderUtil;
import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Extracts method or constructor parameter names from bytecode debug information in runtime.
 */
public class Paramo {

	protected static final String CTOR_METHOD = "<init>";

	/**
	 * Resolves method parameters from a method or constructor.
	 * Returns an empty array when target does not contain any parameter.
	 * No caching is involved in this process, i.e. class bytecode
	 * is examined every time this method is called.
	 */
	public static MethodParameter[] resolveParameters(AccessibleObject methodOrCtor) {
		Class[] paramTypes;
		Class declaringClass;
		String name;
		if (methodOrCtor instanceof Method) {
			Method method = (Method) methodOrCtor;
			paramTypes = method.getParameterTypes();
			name = method.getName();
			declaringClass = method.getDeclaringClass();
		} else {
			Constructor constructor = (Constructor) methodOrCtor;
			paramTypes = constructor.getParameterTypes();
			declaringClass = constructor.getDeclaringClass();
			name = CTOR_METHOD;
		}

		if (paramTypes.length == 0) {
			return MethodParameter.EMPTY_ARRAY;
		}

		InputStream stream;
		try {
			stream = ClassLoaderUtil.getClassAsStream(declaringClass);
		} catch (IOException ioex) {
			throw new ParamoException("Unable to read bytes of class '" + declaringClass.getName() + "'.", ioex);
		}
		try {
			ClassReader reader = new ClassReader(stream);
			MethodFinder visitor = new MethodFinder(declaringClass, name, paramTypes);
			reader.accept(visitor, 0);
			return visitor.getResolvedParameters();
		} catch (IOException ioex) {
			throw new ParamoException(ioex);
		}
	}

}
