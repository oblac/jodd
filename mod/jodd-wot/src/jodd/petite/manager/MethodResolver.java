// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.manager;

import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.petite.PetiteException;
import jodd.petite.PetiteUtil;
import jodd.petite.MethodInjectionPoint;
import jodd.petite.meta.PetiteInject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Method reference resolver.
 */
public class MethodResolver {

	protected final Map<Class, MethodInjectionPoint[]> methodRefs = new HashMap<Class, MethodInjectionPoint[]>();

	public MethodInjectionPoint[] resolve(Class type) {
		MethodInjectionPoint[] methods = methodRefs.get(type);
		if (methods != null) {
			return methods;
		}

		// lookup methods
		ClassDescriptor cd = ClassIntrospector.lookup(type);
		ArrayList<MethodInjectionPoint> list = new ArrayList<MethodInjectionPoint>();
		Method[] allMethods = cd.getAllMethods(true);
		for (Method method : allMethods) {
			PetiteInject ref = method.getAnnotation(PetiteInject.class);
			if (ref == null) {
				continue;
			}
			Class<?>[] paramTypes = method.getParameterTypes();
			String[] refNames = PetiteUtil.resolveParamReferences(ref.value(), paramTypes);
			if (refNames.length != paramTypes.length) {
				throw new PetiteException("Invalid number of method argument reference names for '" + type.getName() + '#' + method.getName() + '\'');
			}
			list.add(new MethodInjectionPoint(method, refNames));
		}
		if (list.isEmpty()) {
			methods = MethodInjectionPoint.EMPTY;
		} else {
			methods = list.toArray(new MethodInjectionPoint[list.size()]);
		}
		methodRefs.put(type, methods);
		return methods;
	}

}
