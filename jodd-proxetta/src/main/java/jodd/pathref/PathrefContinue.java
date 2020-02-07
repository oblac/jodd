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

package jodd.pathref;

import jodd.util.ClassUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class PathrefContinue {

	private final Pathref pathref;

	public PathrefContinue(final Pathref pathref) {
		this.pathref = pathref;
	}

	/**
	 * Factory of next target. It handles special cases of maps, sets
	 * and lists. In case target can not be proxified (like for Java classes)
	 * it returns <code>null</code>.
	 */
	@SuppressWarnings("unchecked")
	public <T> T continueWith(final Object currentInstance, final String methodName, final Class<T> target) {
		final Class currentClass = currentInstance.getClass();

		final Method method;

		try {
			method = currentClass.getDeclaredMethod(methodName);
		}
		catch (final NoSuchMethodException e) {
			throw new PathrefException("Not a getter: " + methodName, e);
		}

		if (!ClassUtil.isBeanPropertyGetter(method)) {
			throw new PathrefException("Not a getter: " + methodName);
		}

		final String getterName = ClassUtil.getBeanPropertyGetterName(method);

		pathref.append(getterName);

		if (ClassUtil.isTypeOf(target, List.class)) {
			final Class componentType =
				ClassUtil.getComponentType(method.getGenericReturnType(), currentClass, 0);

			if (componentType == null) {
				throw new PathrefException("Unknown component name for: " + methodName);
			}

			return (T) new ArrayList() {
				@Override
				public Object get(final int index) {
					if (index >= 0) {
						pathref.append("[" + index + "]");
					}
					return new Pathref<>(componentType, pathref).get();
				}
			};
		}

		try {
			return new Pathref<>(target, pathref).get();
		}
		catch (final Exception ex) {
			return null;
		}
	}

}
