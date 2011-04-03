// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.manager;

import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.petite.CtorInjectionPoint;
import jodd.petite.InitMethodPoint;
import jodd.petite.MethodInjectionPoint;
import jodd.petite.PetiteException;
import jodd.petite.PetiteUtil;
import jodd.petite.PropertyInjectionPoint;
import jodd.util.ReflectUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Petite manager holds configuration and deals with registration.
 * <p>
 * Rules used for ctor/method definition:
 * <li> if parameter types doesn't exist, try to find single ctor/method that will match
 * <li> if reference names doesn't exist, resolve it from parameter types. 
 */
public class PetiteManager {

	protected final CtorResolver ctorResolver;
	protected final PropertyResolver propertyResolver;
	protected final MethodResolver methodResolver;
	protected final InitMethodResolver initMethodResolver;
	protected final ParamResolver paramResolver;

	/**
	 * Creates all Petite managers.
	 */
	public PetiteManager() {
		ctorResolver = new CtorResolver();
		propertyResolver = new PropertyResolver();
		methodResolver = new MethodResolver();
		initMethodResolver = new InitMethodResolver();
		paramResolver = new ParamResolver();
	}


	/**
	 * Removes resolvers references for given bean type.
	 */
	public void removeResolvers(Class type) {
		ctorResolver.ctors.remove(type);
		propertyResolver.properties.remove(type);
		methodResolver.methodRefs.remove(type);
		initMethodResolver.initMethods.remove(type);
	}

	// ---------------------------------------------------------------- constructors

	public CtorInjectionPoint resolveCtorInjectionPoint(Class type) {
		return ctorResolver.resolve(type);
	}

	public CtorInjectionPoint resolveDefaultCtorInjectionPoint(Class type) {
		return ctorResolver.resolveDefault(type);
	}

	public CtorInjectionPoint defineCtorInjectionPoint(Class type, Class[] paramTypes, String[] references) {
		ClassDescriptor cd = ClassIntrospector.lookup(type);
		Constructor constructor = null;
		if (paramTypes == null) {
			Constructor[] ctors = cd.getAllCtors(true);
			if (ctors.length > 0) {
				if (ctors.length > 1) {
					throw new PetiteException(ctors.length + " suitable constructor found as injection point for: '" + type.getName() + "'.");
				}
				constructor = ctors[0];
				paramTypes = constructor.getParameterTypes();
			}
		} else {
			constructor = cd.getCtor(paramTypes, true);
		}
		if (constructor == null) {
			throw new PetiteException("Constructor '" + type.getName() + "()' not found.");
		}
		if (references == null) {
			references = PetiteUtil.resolveParamReferences(paramTypes);
		} else {
			if (paramTypes.length != references.length) {
				throw new PetiteException("Different number of ctor parameters and references for: '" + constructor.getName() + "'.");
			}
		}
		return new CtorInjectionPoint(constructor, references);
	}

	// ---------------------------------------------------------------- property

	public PropertyInjectionPoint[] resolvePropertyInjectionPoint(Class type, boolean autowire) {
		return propertyResolver.resolve(type, autowire);
	}

	public PropertyInjectionPoint definePropertyInjectionPoint(Class type, String property, String[] references) {
		ClassDescriptor cd = ClassIntrospector.lookup(type);
		Field field = cd.getField(property, true);
		if (field == null) {
			throw new PetiteException("Property '" + type.getName() + '#' + property + "' doesn't exist");
		}
		if (references == null || references.length == 0) {
			references = PetiteUtil.fieldDefaultReferences(field);
		}
		return new PropertyInjectionPoint(field, references);	// todo extract this call in 1 place where config is available
	}

	// ---------------------------------------------------------------- methods

	public MethodInjectionPoint[] resolveMethodInjectionPoint(Class type) {
		return methodResolver.resolve(type);
	}

	public MethodInjectionPoint defineMethodInjectionPoint(Class type, String methodName, Class[] paramTypes, String[] references) {
		ClassDescriptor cd = ClassIntrospector.lookup(type);
		Method method = null;
		if (paramTypes == null) {
			Method[] methods = cd.getAllMethods(methodName, true);
			if (methods.length > 0) {
				if (methods.length > 1) {
					throw new PetiteException(methods.length + " suitable methods found as injection points for '" + type.getName() + '#' + methodName + "()'.");
				}
				method = methods[0];
				paramTypes = method.getParameterTypes();
			}
		} else {
			method = cd.getMethod(methodName, paramTypes, true);
		}
		if (method == null) {
			throw new PetiteException("Method '" + type.getName() + '#' + methodName + "()' not found.");
		}
		if (references == null) {
			references = PetiteUtil.resolveParamReferences(paramTypes);
		} else {
			if (paramTypes.length != references.length) {
				throw new PetiteException("Different number of method parameters and references for: '" + type.getName() + '#' + methodName + "()'.");
			}
		}
		return new MethodInjectionPoint(method, references);
	}

	// ---------------------------------------------------------------- initialization methods

	public InitMethodPoint[] resolveInitMethods(Object bean) {
		return initMethodResolver.resolve(bean);
	}

	public InitMethodPoint[] defineInitMethods(Class type, String[] methodNames) {
		return defineInitMethods(type, null, methodNames);
	}

	public InitMethodPoint[] defineInitMethods(Class type, String[] beforeMethodNames, String[] afterMethodNames) {
		ClassDescriptor cd = ClassIntrospector.lookup(type);
		if (beforeMethodNames == null) {
			beforeMethodNames = new String[0];
		}
		if (afterMethodNames == null) {
			afterMethodNames = new String[0];
		}
		int total = beforeMethodNames.length + afterMethodNames.length;
		InitMethodPoint[] methods = new InitMethodPoint[total];
		int i;
		for (i = 0; i < beforeMethodNames.length; i++) {
			Method m = cd.getMethod(beforeMethodNames[i], ReflectUtil.NO_PARAMETERS, true);
			if (m == null) {
				throw new PetiteException("Init method '" + type.getName() + '#' + beforeMethodNames[i] + "()' not found.");
			}
			methods[i] = new InitMethodPoint(m, i, true);
		}
		for (int j = 0; j < afterMethodNames.length; j++) {
			Method m = cd.getMethod(afterMethodNames[j], ReflectUtil.NO_PARAMETERS, true);
			if (m == null) {
				throw new PetiteException("Init method '" + type.getName() + '#' + afterMethodNames[j] + "()' not found.");
			}
			methods[i + j] = new InitMethodPoint(m, i + j, true);
		}
		return methods;
	}


	// ---------------------------------------------------------------- params

	public void defineParameter(String name, Object value) {
		paramResolver.put(name, value);
	}

	public Object getParameter(String name) {
		return paramResolver.get(name);
	}

	/**
	 * Prepares list of all bean parameters and optionally resolves inner references.
	 */
	public String[] resolveBeanParams(String name, boolean resolveReferenceParams) {
		return paramResolver.resolve(name, resolveReferenceParams);
	}
}
