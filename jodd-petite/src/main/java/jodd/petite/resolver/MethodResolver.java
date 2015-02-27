// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.resolver;

import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.introspector.MethodDescriptor;
import jodd.petite.InjectionPointFactory;
import jodd.petite.MethodInjectionPoint;
import jodd.petite.PetiteUtil;
import jodd.petite.meta.PetiteInject;
import jodd.util.ReflectUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Method reference resolver.
 */
public class MethodResolver {

	protected final InjectionPointFactory injectionPointFactory;

	public MethodResolver(InjectionPointFactory injectionPointFactory) {
		this.injectionPointFactory = injectionPointFactory;
	}

	public MethodInjectionPoint[] resolve(Class type) {
		// lookup methods
		ClassDescriptor cd = ClassIntrospector.lookup(type);
		List<MethodInjectionPoint> list = new ArrayList<MethodInjectionPoint>();
		MethodDescriptor[] allMethods = cd.getAllMethodDescriptors();

		for (MethodDescriptor methodDescriptor : allMethods) {
			Method method = methodDescriptor.getMethod();

			if (ReflectUtil.isBeanPropertySetter(method)) {
				// ignore setters
				continue;
			}

			if (method.getParameterTypes().length == 0) {
				// ignore methods with no argument
				continue;
			}

			PetiteInject ref = method.getAnnotation(PetiteInject.class);
			if (ref == null) {
				continue;
			}

			String[][] references = PetiteUtil.convertAnnValueToReferences(ref.value());
			list.add(injectionPointFactory.createMethodInjectionPoint(method, references));
		}

		MethodInjectionPoint[] methods;

		if (list.isEmpty()) {
			methods = MethodInjectionPoint.EMPTY;
		} else {
			methods = list.toArray(new MethodInjectionPoint[list.size()]);
		}
		return methods;
	}

}