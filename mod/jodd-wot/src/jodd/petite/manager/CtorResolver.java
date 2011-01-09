// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.manager;

import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.petite.CtorInjectionPoint;
import jodd.petite.PetiteException;
import jodd.petite.PetiteUtil;
import jodd.petite.meta.PetiteInject;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.HashMap;

/**
 * Resolver for constructor injection points.
 */
public class CtorResolver {

	protected Map<Class, CtorInjectionPoint> ctors = new HashMap<Class, CtorInjectionPoint>();

	protected static final String[] EMPTY_STRING = new String[0];

	/**
	 * Resolves constructor injection point from type. Looks for single annotated constructor.
	 * If no annotated constructors found, the total number of constructors will be checked.
	 * If there is only one constructor, that one will be used as injection point. If more
	 * constructors exist, the default one will be used as injection point.
	 */
	public CtorInjectionPoint resolve(Class type) {
		return resolve(type, true);
	}

	public CtorInjectionPoint resolveDefault(Class type) {
		return resolve(type, false);
	}


	protected CtorInjectionPoint resolve(Class type, boolean useAnnotation) {
		CtorInjectionPoint cip = ctors.get(type);
		if (cip != null) {
			return cip;
		}
		ClassDescriptor cd = ClassIntrospector.lookup(type);
		Constructor[] allCtors = cd.getAllCtors(true);
		Constructor foundedCtor = null;
		Constructor defaultCtor = null;
		String[] refNames = null;
		for (Constructor<?> ctor : allCtors) {
			Class<?>[] paramTypes = ctor.getParameterTypes();
			if (paramTypes.length == 0) {
				defaultCtor = ctor;	// detects default ctors
			}
			if (useAnnotation == false) {
				continue;
			}
			PetiteInject ref = ctor.getAnnotation(PetiteInject.class);
			if (ref == null) {
				continue;
			}
			if (foundedCtor != null) {
				throw new PetiteException("Two or more constructors are annotated as injection points in bean: " + type.getName());
			}
			refNames = PetiteUtil.resolveParamReferences(ref.value(), paramTypes);
			if (refNames.length != paramTypes.length) {
				throw new PetiteException("Invalid number of constructor argument reference names for '" + type.getName() + '\'');
			}
			foundedCtor = ctor;
		}
		if (foundedCtor == null) {
			if (allCtors.length == 1) {
				foundedCtor = allCtors[0];
			} else {
				foundedCtor = defaultCtor;
			}
		}
		if (foundedCtor == null) {
			throw new PetiteException("No constructor (annotated, single or default) founded as injection point for '" + type.getName() + '\'');
		}
		if (refNames == null) {
			refNames = PetiteUtil.resolveParamReferences(foundedCtor.getParameterTypes());
		}
		cip = new CtorInjectionPoint(foundedCtor, refNames);
		ctors.put(type, cip);
		return cip;
	}

}
