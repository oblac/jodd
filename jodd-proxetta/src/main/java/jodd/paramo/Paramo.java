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

package jodd.paramo;

import jodd.io.StreamUtil;
import jodd.util.ClassLoaderUtil;
import jodd.asm5.ClassReader;

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
			throw new ParamoException("Failed to read class bytes: " + declaringClass.getName(), ioex);
		}

		if (stream == null) {
			throw new ParamoException("Class not found: " + declaringClass);
		}

		try {
			ClassReader reader = new ClassReader(stream);
			MethodFinder visitor = new MethodFinder(declaringClass, name, paramTypes);
			reader.accept(visitor, 0);
			return visitor.getResolvedParameters();
		}
		catch (IOException ioex) {
			throw new ParamoException(ioex);
		}
		finally {
			StreamUtil.close(stream);
		}
	}

}