// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.manager;

import jodd.petite.BeanDefinition;
import jodd.petite.CtorInjectionPoint;
import jodd.petite.PropertyInjectionPoint;
import jodd.petite.MethodInjectionPoint;
import jodd.petite.WiringMode;
import jodd.petite.PetiteUtil;
import jodd.petite.PetiteException;
import jodd.petite.InitMethodPoint;
import jodd.petite.PetiteConfig;
import jodd.petite.scope.Scope;
import jodd.petite.scope.DefaultScope;
import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.util.ReflectUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.util.Iterator;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Petite manager holds configuration and deals with registration.
 * <p>
 * Rules used for ctor/method definition:
 * <li> if parameter types doesn't exist, try to find single ctor/method that will match
 * <li> if reference names doesn't exist, resolve it from parameter types. 
 */
public class PetiteManager {

	protected static final Logger log = LoggerFactory.getLogger(PetiteManager.class);

	protected final BeanManager beanManager;
	protected final CtorResolver ctorResolver;
	protected final PropertyResolver propertyResolver;
	protected final MethodResolver methodResolver;
	protected final InitMethodResolver initMethodResolver;
	protected final ParamResolver paramResolver;

	/**
	 * Creates all Petite managers.
	 */
	public PetiteManager() {
		beanManager = new BeanManager();
		ctorResolver = new CtorResolver();
		propertyResolver = new PropertyResolver();
		methodResolver = new MethodResolver();
		initMethodResolver = new InitMethodResolver();
		paramResolver = new ParamResolver();
	}

	// ---------------------------------------------------------------- bean

	/**
	 * Returns {@link BeanDefinition} for given bean name.
	 */
	public BeanDefinition lookupBeanDefinition(String name) {
		return beanManager.beans.get(name);
	}

	/**
	 * Registers bean. The following rules are applied:
	 * <li>if name is missing, it will be resolved from the class (name or annotation)
	 * <li>if wiring mode is missing, it will be resolved from the class (annotation or default one)
	 * <li>if scope type is missing, it will be resolved from the class (annotation or default one)
	 */
	public BeanDefinition registerBean(String name, Class type, Class<? extends Scope> scopeType, WiringMode wiringMode, PetiteConfig pcfg) {
		if (name == null) {
			name = PetiteUtil.resolveBeanName(type);
		}
		if (wiringMode == null) {
			wiringMode = PetiteUtil.resolveBeanWiringMode(type);
		}
		if (wiringMode == WiringMode.DEFAULT) {
			wiringMode = pcfg.getDefaultWiringMode();
		}
		if (scopeType == null) {
			scopeType = PetiteUtil.resolveBeanScopeType(type);
		}
		if (scopeType == DefaultScope.class) {
			scopeType = pcfg.getDefaultScope();
		}
		BeanDefinition existing = removeBean(name);
		if (existing != null) {
			if (pcfg.getDetectDuplicatedBeanNames()) {
				throw new PetiteException(
						"Duplicated bean name detected while registering class '" + type.getName() + "'. Petite bean class '" +
						existing.type.getName() + "' is already registered with the name '" + name + "'.");
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("Registering bean: " + name +
					" of type: " + type.getSimpleName() +
					" in: " + scopeType.getSimpleName() +
					" using wiring mode: " + wiringMode.toString());
		}
		return beanManager.register(name, type, scopeType, wiringMode);
	}

	/**
	 * Removes bean and returns definition of removed bean.
	 * Removes references of existing old bean type, just in case. Existing type may be still in use
	 * by some other bean name, so the references will be re-created if needed.
	 */
	public BeanDefinition removeBean(String name) {
		BeanDefinition bd = beanManager.beans.remove(name);
		if (bd == null) {
			return null;
		}
		// remove references
		ctorResolver.ctors.remove(bd.type);
		propertyResolver.properties.remove(bd.type);
		methodResolver.methodRefs.remove(bd.type);
		initMethodResolver.initMethods.remove(bd.type);
		bd.scopeRemove();

		return bd;
	}

	/**
	 * Returns total number of registered beans.
	 */
	public int getTotalBeans() {
		return beanManager.beans.size();
	}

	/**
	 * Return total number of used scopes.
	 */
	public int getTotalScopes() {
		return beanManager.scopes.size();
	}

	/**
	 * Returns iterator over all registered beans.
	 */
	public Iterator<BeanDefinition> beansIterator() {
		return beanManager.beans.values().iterator();
	}

	// ---------------------------------------------------------------- scopes

	/**
	 * Registers new scope. It is not necessary to manually register scopes,
	 * since they become registered on first scope resolving.
	 * However, it is possible to pre-register some scopes, or to replace one scope
	 * type with another. This may be important for testing purposes when
	 * using container-depended scopes. 
	 */
	public void registerScope(Class<? extends Scope> scopeType, Scope scope) {
		beanManager.registerScope(scopeType, scope);
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

	public PropertyInjectionPoint[] resolvePropertyInjectionPoint(Class type) {
		return propertyResolver.resolve(type);
	}

	public PropertyInjectionPoint definePropertyInjectionPoint(Class type, String property, String reference) {
		if (reference == null) {
			reference = property;
		}
		ClassDescriptor cd = ClassIntrospector.lookup(type);
		Field field = cd.getField(property, true);
		if (field == null) {
			throw new PetiteException("Property '" + type.getName() + '#' + property + "' doesn't exist");
		}
		return new PropertyInjectionPoint(field, reference, true);
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

	public String[] resolveBeanParams(String name, boolean resolveReferenceParams) {
		return paramResolver.resolve(name, resolveReferenceParams);
	}
}
