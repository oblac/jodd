// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.resolver;

import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.petite.InjectionPointFactory;
import jodd.petite.MethodInjectionPoint;
import jodd.petite.PetiteUtil;
import jodd.petite.meta.PetiteInject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * Method reference resolver.
 */
public class MethodResolver {

	protected final Map<Class, MethodInjectionPoint[]> methodRefs = new HashMap<Class, MethodInjectionPoint[]>();

	protected final InjectionPointFactory injectionPointFactory;

	public MethodResolver(InjectionPointFactory injectionPointFactory) {
		this.injectionPointFactory = injectionPointFactory;
	}

	public MethodInjectionPoint[] resolve(Class type) {
		MethodInjectionPoint[] methods = methodRefs.get(type);
		if (methods != null) {
			return methods;
		}

		// lookup methods
		ClassDescriptor cd = ClassIntrospector.lookup(type);
		List<MethodInjectionPoint> list = new ArrayList<MethodInjectionPoint>();
		Method[] allMethods = cd.getAllMethods(true);
		for (Method method : allMethods) {
			PetiteInject ref = method.getAnnotation(PetiteInject.class);
			if (ref == null) {
				continue;
			}
			String[][] references = PetiteUtil.convertAnnValueToReferences(ref.value());
			list.add(injectionPointFactory.createMethodInjectionPoint(method, references));
		}
		if (list.isEmpty()) {
			methods = MethodInjectionPoint.EMPTY;
		} else {
			methods = list.toArray(new MethodInjectionPoint[list.size()]);
		}
		methodRefs.put(type, methods);
		return methods;
	}

	public void remove(Class type) {
		methodRefs.remove(type);
	}

}
