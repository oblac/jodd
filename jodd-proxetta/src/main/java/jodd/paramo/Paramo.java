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

import jodd.asm7.ClassReader;
import jodd.io.StreamUtil;
import jodd.util.ClassLoaderUtil;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

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
	public static MethodParameter[] resolveParameters(final AccessibleObject methodOrCtor) {
		final Class[] paramTypes;
		final Parameter[] parameters;
		final Class declaringClass;
		final String name;

		if (methodOrCtor instanceof Method) {
			final Method method = (Method) methodOrCtor;
			paramTypes = method.getParameterTypes();
			name = method.getName();
			declaringClass = method.getDeclaringClass();
			parameters = method.getParameters();
		} else {
			final Constructor constructor = (Constructor) methodOrCtor;
			paramTypes = constructor.getParameterTypes();
			declaringClass = constructor.getDeclaringClass();
			name = CTOR_METHOD;
			parameters = constructor.getParameters();
		}

		if (paramTypes.length == 0) {
			return MethodParameter.EMPTY_ARRAY;
		}

		final InputStream stream;
		try {
			stream = ClassLoaderUtil.getClassAsStream(declaringClass);
		} catch (final IOException ioex) {
			throw new ParamoException("Failed to read class bytes: " + declaringClass.getName(), ioex);
		}

		if (stream == null) {
			throw new ParamoException("Class not found: " + declaringClass);
		}

		try {
			final ClassReader reader = new ClassReader(stream);
			final MethodFinder visitor = new MethodFinder(declaringClass, name, paramTypes, parameters);
			reader.accept(visitor, 0);
			return visitor.getResolvedParameters();
		}
		catch (final IOException ioex) {
			throw new ParamoException(ioex);
		}
		finally {
			StreamUtil.close(stream);
		}
	}

}
