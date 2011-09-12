// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.log.Log;
import jodd.petite.scope.DefaultScope;
import jodd.petite.scope.Scope;
import jodd.util.ReflectUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Base layer of {@link PetiteContainer Petite Container}.
 * Holds beans and scopes definitions.
 */
public abstract class PetiteBeans {

	private static final Log log = Log.getLogger(PetiteBeans.class);

	/**
	 * Map of all beans definitions.
	 */
	protected final Map<String, BeanDefinition> beans = new HashMap<String, BeanDefinition>();

	/**
	 * Map of all bean scopes.
	 */
	protected final Map<Class<? extends Scope>, Scope> scopes = new HashMap<Class<? extends Scope>, Scope>();

	/**
	 * Map of all bean collections.
	 */
	protected final Map<Class, String[]> beanCollections = new HashMap<Class, String[]>();

	/**
	 * {@link PetiteConfig Petite configuration}.
	 */
	protected final PetiteConfig petiteConfig;

	/**
	 * {@link InjectionPointFactory Injection point factory}.
	 */
	protected final InjectionPointFactory injectionPointFactory;

	/**
	 * {@link PetiteResolvers Petite resolvers}.
	 */
	protected final PetiteResolvers petiteResolvers;

	protected PetiteBeans(PetiteConfig petiteConfig) {
		this.petiteConfig = petiteConfig;
		this.injectionPointFactory = new InjectionPointFactory(petiteConfig);
		this.petiteResolvers = new PetiteResolvers(injectionPointFactory);
	}

	/**
	 * Returns Petite resolvers.
	 */
	public PetiteResolvers getResolvers() {
		return petiteResolvers;
	}

	/**
	 * Returns {@link PetiteConfig Petite configuration}.
	 * All changes on config should be applied <b>before</b>
	 * beans registration process starts.
	 */
	public PetiteConfig getConfig() {
		return petiteConfig;
	}

	// ---------------------------------------------------------------- scopes

	/**
	 * Resolves scope from scope type.
	 */
	protected Scope resolveScope(Class<? extends Scope> scopeType) {
		Scope scope = scopes.get(scopeType);
		if (scope == null) {
			try {
				scope = scopeType.newInstance();
				registerScope(scopeType, scope);
				scopes.put(scopeType, scope);
			} catch (Exception ex) {
				throw new PetiteException("Unable to create Petite scope: '" + scopeType.getName(), ex);
			}
		}
		return scope;
	}

	/**
	 * Registers new scope. It is not necessary to manually register scopes,
	 * since they become registered on first scope resolving.
	 * However, it is possible to pre-register some scopes, or to replace one scope
	 * type with another. This may be important for testing purposes when
	 * using container-depended scopes.
	 */
	public void registerScope(Class<? extends Scope> scopeType, Scope scope) {
		scopes.put(scopeType, scope);
	}

	// ---------------------------------------------------------------- lookup beans

	/**
	 * Lookups for {@link BeanDefinition bean definition}.
	 * Returns <code>null</code> if bean name doesn't exist.
	 */
	protected BeanDefinition lookupBeanDefinition(String name) {
		return beans.get(name);
	}

	/**
	 * Lookups for existing bean. Throws exception if bean is not found.
	 */
	protected BeanDefinition lookupExistingBeanDefinition(String name) {
		BeanDefinition beanDefinition = lookupBeanDefinition(name);
		if (beanDefinition == null) {
			throw new PetiteException("Bean: '" + name + "' not registered.");
		}
		return beanDefinition;
	}

	/**
	 * Returns <code>true</code> if bean name is registered.
	 */
	public boolean isBeanNameRegistered(String name) {
		return lookupBeanDefinition(name) != null;
	}

	/**
	 * Resolves bean's name from bean annotation or type name. May be used for resolving bean name
	 * of base type during registration of bean subclass.
	 */
	public String resolveBeanName(Class type) {
		return PetiteUtil.resolveBeanName(type, petiteConfig.getUseFullTypeNames());
	}

	// ---------------------------------------------------------------- register beans

	/**
	 * Single point of bean registration. The following rules are applied:
	 * <li>if name is missing, it will be resolved from the class (name or annotation)
	 * <li>if wiring mode is missing, it will be resolved from the class (annotation or default one)
	 * <li>if scope type is missing, it will be resolved from the class (annotation or default one)
	 */
	protected BeanDefinition registerPetiteBean(String name, Class type, Class<? extends Scope> scopeType, WiringMode wiringMode) {
		if (name == null) {
			name = PetiteUtil.resolveBeanName(type, petiteConfig.getUseFullTypeNames());
		}
		if (wiringMode == null) {
			wiringMode = PetiteUtil.resolveBeanWiringMode(type);
		}
		if (wiringMode == WiringMode.DEFAULT) {
			wiringMode = petiteConfig.getDefaultWiringMode();
		}
		if (scopeType == null) {
			scopeType = PetiteUtil.resolveBeanScopeType(type);
		}
		if (scopeType == DefaultScope.class) {
			scopeType = petiteConfig.getDefaultScope();
		}
		BeanDefinition existing = removeBeanDefinition(name);
		if (existing != null) {
			if (petiteConfig.getDetectDuplicatedBeanNames()) {
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

		// registering

		// check if type is valid
		if ((type != null) && (type.isInterface() == true)) {
			throw new PetiteException("Unable to register interface '" + type.getName() + "'.");
		}
		// register
		Scope scope = resolveScope(scopeType);
		BeanDefinition beanDefinition = new BeanDefinition(name, type, scope, wiringMode);
		beans.put(name, beanDefinition);
		return beanDefinition;
	}

	/**
	 * Single point of bean definition.
	 */
	protected void definePetiteBean(String name, Class type, Class<? extends Scope> scopeType, WiringMode wiringMode) {
		BeanDefinition def = registerPetiteBean(name, type, scopeType, wiringMode);
		def.ctor = resolveCtorInjectionPoint(type);
		def.properties = PropertyInjectionPoint.EMPTY;
		def.methods = MethodInjectionPoint.EMPTY;
		def.initMethods = InitMethodPoint.EMPTY;
	}

	/**
	 * Removes bean and returns definition of removed bean.
	 * All resolvers references are deleted, too.
	 * Returns bean definition of removed bean or <code>null</code>.
	 */
	protected BeanDefinition removeBeanDefinition(String name) {
		BeanDefinition bd = beans.remove(name);
		if (bd == null) {
			return null;
		}
		petiteResolvers.getCtorResolver().remove(bd.type);
		petiteResolvers.getPropertyResolver().remove(bd.type);
		petiteResolvers.getMethodResolver().remove(bd.type);
		petiteResolvers.getInitMethodResolver().remove(bd.type);
		bd.scopeRemove();
		return bd;
	}


	// ---------------------------------------------------------------- bean collections

	/**
	 * Resolve bean names for give type.
	 */
	protected String[] resolveBeanNamesForType(Class type) {
		String[] beanNames = beanCollections.get(type);
		if (beanNames != null) {
			return beanNames;
		}

		ArrayList<String> list = new ArrayList<String>();

		for (Map.Entry<String, BeanDefinition> entry : beans.entrySet()) {
			BeanDefinition beanDefinition = entry.getValue();

			if (ReflectUtil.isSubclass(beanDefinition.type, type)) {
				String beanName = entry.getKey();
				list.add(beanName);
			}
		}

		if (list.isEmpty()) {
			beanNames = new String[0];
		} else {
			beanNames = list.toArray(new String[list.size()]);
		}

		beanCollections.put(type, beanNames);
		return beanNames;
	}

	// ---------------------------------------------------------------- injection points

	/**
	 * Single point of constructor injection point registration.
	 */
	protected void registerPetiteCtorInjectionPoint(String beanName, Class[] paramTypes, String[] references) {
		BeanDefinition beanDefinition = lookupExistingBeanDefinition(beanName);
		String[][] ref = PetiteUtil.convertRefToReferences(references);
		beanDefinition.ctor = defineCtorInjectionPoint(beanDefinition.type, paramTypes, ref);
	}

	private CtorInjectionPoint defineCtorInjectionPoint(Class type, Class[] paramTypes, String[][] references) {
		ClassDescriptor cd = ClassIntrospector.lookup(type);
		Constructor constructor = null;
		if (paramTypes == null) {
			Constructor[] ctors = cd.getAllCtors(true);
			if (ctors.length > 0) {
				if (ctors.length > 1) {
					throw new PetiteException(ctors.length + " suitable constructor found as injection point for: '" + type.getName() + "'.");
				}
				constructor = ctors[0];
			}
		} else {
			constructor = cd.getCtor(paramTypes, true);
		}
		if (constructor == null) {
			throw new PetiteException("Constructor '" + type.getName() + "()' not found.");
		}
		return injectionPointFactory.createCtorInjectionPoint(constructor, references);
	}

	/**
	 * Single point of property injection point registration.
	 */
	protected void registerPetitePropertyInjectionPoint(String beanName, String property, String reference) {
		BeanDefinition beanDefinition = lookupExistingBeanDefinition(beanName);
		PropertyInjectionPoint pip = definePropertyInjectionPoint(
				beanDefinition.type,
				property,
				reference == null ? null : new String[] {reference});
		beanDefinition.addPropertyInjectionPoint(pip);
	}

	private PropertyInjectionPoint definePropertyInjectionPoint(Class type, String property, String[] references) {
		ClassDescriptor cd = ClassIntrospector.lookup(type);
		Field field = cd.getField(property, true);
		if (field == null) {
			throw new PetiteException("Property '" + type.getName() + '#' + property + "' doesn't exist");
		}
		return injectionPointFactory.createPropertyInjectionPoint(field, references);
	}

	/**
	 * Single point of method injection point registration.
	 */
	protected void registerPetiteMethodInjectionPoint(String beanName, String methodName, Class[] arguments, String[] references) {
		BeanDefinition beanDefinition = lookupExistingBeanDefinition(beanName);
		String[][] ref = PetiteUtil.convertRefToReferences(references);
		MethodInjectionPoint mip = defineMethodInjectionPoint(beanDefinition.type, methodName, arguments, ref);
		beanDefinition.addMethodInjectionPoint(mip);
	}

	private MethodInjectionPoint defineMethodInjectionPoint(Class type, String methodName, Class[] paramTypes, String[][] references) {
		ClassDescriptor cd = ClassIntrospector.lookup(type);
		Method method = null;
		if (paramTypes == null) {
			Method[] methods = cd.getAllMethods(methodName, true);
			if (methods.length > 0) {
				if (methods.length > 1) {
					throw new PetiteException(methods.length + " suitable methods found as injection points for '" + type.getName() + '#' + methodName + "()'.");
				}
				method = methods[0];
			}
		} else {
			method = cd.getMethod(methodName, paramTypes, true);
		}
		if (method == null) {
			throw new PetiteException("Method '" + type.getName() + '#' + methodName + "()' not found.");
		}
		return injectionPointFactory.createMethodInjectionPoint(method, references);
	}

	/**
	 * Single point of init method registration.
	 */
	protected void registerPetiteInitMethods(String beanName, String[] beforeMethodNames, String[] afterMethodNames) {
		BeanDefinition beanDefinition = lookupExistingBeanDefinition(beanName);
		InitMethodPoint[] methods = defineInitMethods(beanDefinition.type, beforeMethodNames, afterMethodNames);
		beanDefinition.addInitMethodPoints(methods);
	}

	private InitMethodPoint[] defineInitMethods(Class type, String[] beforeMethodNames, String[] afterMethodNames) {
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

	// ---------------------------------------------------------------- statistics

	/**
	 * Returns total number of registered beans.
	 */
	public int getTotalBeans() {
		return beans.size();
	}

	/**
	 * Returns total number of used scopes.
	 */
	public int getTotalScopes() {
		return scopes.size();
	}

	/**
	 * Returns iterator over all registered beans.
	 */
	public Iterator<BeanDefinition> beansIterator() {
		return beans.values().iterator();
	}

	// ---------------------------------------------------------------- resolvers

	protected CtorInjectionPoint resolveCtorInjectionPoint(Class type) {
		return petiteResolvers.getCtorResolver().resolve(type);
	}

	protected CtorInjectionPoint resolveDefaultCtorInjectionPoint(Class type) {
		return petiteResolvers.getCtorResolver().resolveDefault(type);
	}

	protected PropertyInjectionPoint[] resolvePropertyInjectionPoint(Class type, boolean autowire) {
		return petiteResolvers.getPropertyResolver().resolve(type, autowire);
	}

	protected SetInjectionPoint[] resolveCollectionInjectionPoint(Class type, boolean autowire) {
		return petiteResolvers.getSetResolver().resolve(type, autowire);
	}

	protected MethodInjectionPoint[] resolveMethodInjectionPoint(Class type) {
		return petiteResolvers.getMethodResolver().resolve(type);
	}

	protected InitMethodPoint[] resolveInitMethods(Object bean) {
		return petiteResolvers.getInitMethodResolver().resolve(bean);
	}

	// ---------------------------------------------------------------- params

	/**
	 * Defines new parameter. Parameters with same name will be replaced.
	 */
	public void defineParameter(String name, Object value) {
		petiteResolvers.getParamResolver().put(name, value);
	}

	/**
	 * Returns defined parameter.
	 */
	public Object getParameter(String name) {
		return petiteResolvers.getParamResolver().get(name);
	}

	/**
	 * Prepares list of all bean parameters and optionally resolves inner references.
	 */
	protected String[] resolveBeanParams(String name, boolean resolveReferenceParams) {
		return petiteResolvers.getParamResolver().resolve(name, resolveReferenceParams);
	}

}
