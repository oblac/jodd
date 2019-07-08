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

package jodd.petite.resolver;

import jodd.introspector.ClassDescriptor;
import jodd.introspector.MethodDescriptor;
import jodd.petite.PetiteException;
import jodd.petite.def.DestroyMethodPoint;
import jodd.petite.meta.PetiteDestroyMethod;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Resolver for destroy methods.
 */
public class DestroyMethodResolver {

	public DestroyMethodPoint[] resolve(final Class<?> type) {
		// lookup methods
		List<DestroyMethodPoint> list = new ArrayList<>();
		ClassDescriptor cd = new ClassDescriptor(type, false, false, false, null);
		MethodDescriptor[] allMethods = cd.getAllMethodDescriptors();

		for (MethodDescriptor methodDescriptor : allMethods) {
			Method method = methodDescriptor.getMethod();

			PetiteDestroyMethod petiteDestroyMethod = method.getAnnotation(PetiteDestroyMethod.class);
			if (petiteDestroyMethod == null) {
				continue;
			}
			if (method.getParameterTypes().length > 0) {
				throw new PetiteException("Arguments are not allowed for Petite destroy method: " + type.getName() + '#' + method.getName());
			}
			list.add(new DestroyMethodPoint(method));
		}

		DestroyMethodPoint[] methods;

		if (list.isEmpty()) {
			methods = DestroyMethodPoint.EMPTY;
		} else {
			methods = list.toArray(new DestroyMethodPoint[0]);
		}

		return methods;
	}

}