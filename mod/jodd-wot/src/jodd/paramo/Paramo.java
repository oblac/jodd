// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

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
	 * Resolves parameter names from method or constructor.
	 */
	public static String[] resolveParameterNames(AccessibleObject methodOrCtor) {
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
			return MethodFinder.EMPTY_NAMES;
		}
		InputStream stream;
		try {
			stream = ClassLoaderUtil.getClassAsStream(declaringClass);
		} catch (IOException ioex) {
			throw new ParamoException("Unable to read class bytes.");
		}
		try {
			ClassReader reader = new ClassReader(stream);
			MethodFinder visitor = new MethodFinder(name, paramTypes);
			reader.accept(visitor, 0);
			return visitor.getParameterNames();
		} catch (IOException ioex) {
			throw new ParamoException(ioex);
		}
	}

}