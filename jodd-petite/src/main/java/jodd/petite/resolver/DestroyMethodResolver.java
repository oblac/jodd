// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.resolver;

import jodd.introspector.ClassDescriptor;
import jodd.introspector.MethodDescriptor;
import jodd.petite.DestroyMethodPoint;
import jodd.petite.PetiteException;
import jodd.petite.meta.PetiteDestroyMethod;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Resolver for destroy methods.
 */
public class DestroyMethodResolver {

	public DestroyMethodPoint[] resolve(Object bean) {
		Class<?> type = bean.getClass();

		// lookup methods
		List<DestroyMethodPoint> list = new ArrayList<DestroyMethodPoint>();
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
			methods = list.toArray(new DestroyMethodPoint[list.size()]);
		}

		return methods;
	}

}