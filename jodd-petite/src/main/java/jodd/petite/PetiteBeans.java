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

package jodd.petite;

import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.introspector.CtorDescriptor;
import jodd.introspector.MethodDescriptor;
import jodd.introspector.PropertyDescriptor;
import jodd.petite.meta.InitMethodInvocationStrategy;
import jodd.petite.scope.Scope;
import jodd.petite.scope.SingletonScope;
import jodd.props.Props;
import jodd.util.ClassUtil;
import jodd.util.StringPool;
import jodd.log.Logger;
import jodd.log.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Base layer of {@link PetiteContainer Petite Container}.
 * Holds beans and scopes definitions.
 */
public abstract class PetiteBeans {

	private static final Logger log = LoggerFactory.getLogger(PetiteBeans.class);

	/**
	 * Map of all beans definitions.
	 */
	protected final Map<String, BeanDefinition> beans = new HashMap<>();

	/**
	 * Map of alternative beans names.
	 */
	protected final Map<String, BeanDefinition> beansAlt = new HashMap<>();

	/**
	 * Map of all bean scopes.
	 */
	protected final Map<Class<? extends Scope>, Scope> scopes = new HashMap<>();

	/**
	 * Map of all providers.
	 */
	protected final Map<String, ProviderDefinition> providers = new HashMap<>();

	/**
	 * Map of all bean collections.
	 */
	protected final Map<Class, String[]> beanCollections = new HashMap<>();

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

	/**
	 * {@link ParamManager Parameters manager}.
	 */
	protected final ParamManager paramManager;

	protected PetiteBeans(PetiteConfig petiteConfig) {
		this.petiteConfig = petiteConfig;
		this.injectionPointFactory = new InjectionPointFactory(petiteConfig);
		this.petiteResolvers = new PetiteResolvers(injectionPointFactory);
		this.paramManager = new ParamManager();
	}

	/**
	 * Returns parameter manager.
	 */
	public ParamManager getParamManager() {
		return paramManager;
	}

	/**
	 * Returns {@link PetiteConfig Petite configuration}.
	 * All changes on config should be done <b>before</b>
	 * beans registration process starts.
	 */
	public PetiteConfig getConfig() {
		return petiteConfig;
	}

	// ---------------------------------------------------------------- scopes

	/**
	 * Resolves and registers scope from a scope type.
	 */
	@SuppressWarnings("unchecked")
	public <S extends Scope> S resolveScope(Class<S> scopeType) {
		S scope = (S) scopes.get(scopeType);
		if (scope == null) {

			try {
				scope = PetiteUtil.newInstance(scopeType, (PetiteContainer) this);
			} catch (Exception ex) {
				throw new PetiteException("Invalid Petite scope: " + scopeType.getName(), ex);
			}

			registerScope(scopeType, scope);
			scopes.put(scopeType, scope);
		}
		return scope;
	}

	/**
	 * Registers new scope. It is not necessary to manually register scopes,
	 * since they become registered on first scope resolving.
	 * However, it is possible to pre-register some scopes, or to <i>replace</i> one scope
	 * type with another. Replacing may be important for testing purposes when
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
	public BeanDefinition lookupBeanDefinition(String name) {
		BeanDefinition beanDefinition = beans.get(name);

		// try alt bean names
		if (beanDefinition == null) {
			if (petiteConfig.isUseAltBeanNames()) {
				beanDefinition = beansAlt.get(name);
			}
		}

		return beanDefinition;
	}

	/**
	 * Lookups for first founded {@link BeanDefinition bean definition}.
	 * Returns <code>null</code> if none of the beans is found.
	 */
	protected BeanDefinition lookupBeanDefinitions(String... names) {
		for (String name : names) {
			BeanDefinition beanDefinition = lookupBeanDefinition(name);
			if (beanDefinition != null) {
				return beanDefinition;
			}
		}
		return null;
	}

	/**
	 * Lookups for existing {@link jodd.petite.BeanDefinition bean definition}.
	 * Throws exception if bean is not found.
	 */
	protected BeanDefinition lookupExistingBeanDefinition(String name) {
		BeanDefinition beanDefinition = lookupBeanDefinition(name);
		if (beanDefinition == null) {
			throw new PetiteException("Bean not found: " + name);
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
	 * Creates {@link jodd.petite.BeanDefinition} on
	 * {@link #registerPetiteBean(Class, String, Class, WiringMode, boolean) bean registration}.
	 * This is a hook for modifying the bean data, like passing proxifed class etc.
	 * By default returns new instance of {@link jodd.petite.BeanDefinition}.
	 */
	protected BeanDefinition createBeanDefinitionForRegistration(
			String name, Class type, Scope scope, WiringMode wiringMode) {

		return new BeanDefinition(name, type, scope, wiringMode);
	}

	/**
	 * Registers a bean using provided class that is annotated.
	 */
	public BeanDefinition registerPetiteBean(Class type) {
		return registerPetiteBean(type, null, null, null, false);
	}

	/**
	 * Registers or defines a bean.
	 *
	 * @param type bean type, must be specified
	 * @param name bean name, if <code>null</code> it will be resolved from the class (name or annotation)
	 * @param scopeType bean scope, if <code>null</code> it will be resolved from the class (annotation or default one)
	 * @param wiringMode wiring mode, if <code>null</code> it will be resolved from the class (annotation or default one)
	 * @param define when set to <code>true</code> bean will be defined - all injection points will be set to none
	 */
	public BeanDefinition registerPetiteBean(
			Class type, String name,
			Class<? extends Scope> scopeType,
			WiringMode wiringMode,
			boolean define) {

		if (name == null) {
			name = resolveBeanName(type);
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
		if (scopeType == null) {
			scopeType = SingletonScope.class;
		}

		// remove existing bean
		BeanDefinition existing = removeBean(name);
		if (existing != null) {
			if (petiteConfig.getDetectDuplicatedBeanNames()) {
				throw new PetiteException(
						"Duplicated bean name detected while registering class '" + type.getName() + "'. Petite bean class '" +
						existing.type.getName() + "' is already registered with the name: " + name);
			}
		}

		// check if type is valid
		if (type.isInterface()) {
			throw new PetiteException("PetiteBean can not be an interface: " + type.getName());
		}

		// registration
		if (log.isDebugEnabled()) {
			log.debug("Register bean " + name +
					" of type " + type.getSimpleName() +
					" in " + scopeType.getSimpleName() +
					" using wiring mode " + wiringMode.toString());
		}

		// register
		Scope scope = resolveScope(scopeType);
		BeanDefinition beanDefinition = createBeanDefinitionForRegistration(name, type, scope, wiringMode);

		registerBean(name, beanDefinition);

		// providers
		ProviderDefinition[] providerDefinitions = petiteResolvers.resolveProviderDefinitions(beanDefinition);

		if (providerDefinitions != null) {
			for (ProviderDefinition providerDefinition : providerDefinitions) {
				providers.put(providerDefinition.name, providerDefinition);
			}
		}

		// define
		if (define) {
			beanDefinition.ctor = petiteResolvers.resolveCtorInjectionPoint(beanDefinition.getType());
			beanDefinition.properties = PropertyInjectionPoint.EMPTY;
			beanDefinition.methods = MethodInjectionPoint.EMPTY;
			beanDefinition.initMethods = InitMethodPoint.EMPTY;
			beanDefinition.destroyMethods = DestroyMethodPoint.EMPTY;
		}

		// return
		return beanDefinition;
	}

	/**
	 * Registers bean definition by putting it in the beans map. If bean does
	 * not have petite name explicitly defined, alternative bean names
	 * will be registered.
	 */
	protected void registerBean(String name, BeanDefinition beanDefinition) {
		beans.put(name, beanDefinition);

		if (!petiteConfig.isUseAltBeanNames()) {
			return;
		}

		Class type = beanDefinition.getType();

		if (PetiteUtil.beanHasAnnotationName(type)) {
			return;
		}

		Class[] interfaces = ClassUtil.resolveAllInterfaces(type);

		for (Class anInterface : interfaces) {
			String altName = PetiteUtil.resolveBeanName(anInterface, petiteConfig.getUseFullTypeNames());

			if (name.equals(altName)) {
				continue;
			}

			if (beans.containsKey(altName)) {
				continue;
			}

			if (beansAlt.containsKey(altName)) {
				BeanDefinition existing = beansAlt.get(altName);

				if (existing != null) {
					beansAlt.put(altName, null);		// store null as value to mark that alt name is duplicate
				}
			}
			else {
				beansAlt.put(altName, beanDefinition);
			}
		}
	}

	/**
	 * Removes all petite beans of provided type. Bean name is not resolved from a type!
	 * Instead, all beans are iterated and only beans with equal types are removed.
	 * @see #removeBean(String)
	 */
	public void removeBean(Class type) {
		// collect bean names
		Set<String> beanNames = new HashSet<>();

		for (BeanDefinition def : beans.values()) {
			if (def.type.equals(type)) {
				beanNames.add(def.name);
			}
		}

		// remove collected bean names
		for (String beanName : beanNames) {
			removeBean(beanName);
		}
	}

	/**
	 * Removes bean and returns definition of removed bean.
	 * All resolvers references are deleted, too.
	 * Returns bean definition of removed bean or <code>null</code>.
	 */
	public BeanDefinition removeBean(String name) {
		BeanDefinition bd = beans.remove(name);
		if (bd == null) {
			return null;
		}
		bd.scopeRemove();
		return bd;
	}

	// ---------------------------------------------------------------- bean collections

	/**
	 * Resolves bean names for give type.
	 */
	protected String[] resolveBeanNamesForType(Class type) {
		String[] beanNames = beanCollections.get(type);
		if (beanNames != null) {
			return beanNames;
		}

		ArrayList<String> list = new ArrayList<>();

		for (Map.Entry<String, BeanDefinition> entry : beans.entrySet()) {
			BeanDefinition beanDefinition = entry.getValue();

			if (ClassUtil.isTypeOf(beanDefinition.type, type)) {
				String beanName = entry.getKey();
				list.add(beanName);
			}
		}

		if (list.isEmpty()) {
			beanNames = StringPool.EMPTY_ARRAY;
		} else {
			beanNames = list.toArray(new String[list.size()]);
		}

		beanCollections.put(type, beanNames);
		return beanNames;
	}

	// ---------------------------------------------------------------- injection points

	/**
	 * Registers constructor injection point.
	 *
	 * @param beanName bean name
	 * @param paramTypes constructor parameter types, may be <code>null</code>
	 * @param references references for arguments
	 */
	public void registerPetiteCtorInjectionPoint(String beanName, Class[] paramTypes, String[] references) {
		BeanDefinition beanDefinition = lookupExistingBeanDefinition(beanName);
		String[][] ref = PetiteUtil.convertRefToReferences(references);

		ClassDescriptor cd = ClassIntrospector.lookup(beanDefinition.type);
		Constructor constructor = null;

		if (paramTypes == null) {
			CtorDescriptor[] ctors = cd.getAllCtorDescriptors();
			if (ctors != null && ctors.length > 0) {
				if (ctors.length > 1) {
					throw new PetiteException(ctors.length + " suitable constructor found as injection point for: " + beanDefinition.type.getName());
				}
				constructor = ctors[0].getConstructor();
			}
		} else {
			CtorDescriptor ctorDescriptor = cd.getCtorDescriptor(paramTypes, true);

			if (ctorDescriptor != null) {
				constructor = ctorDescriptor.getConstructor();
			}
		}

		if (constructor == null) {
			throw new PetiteException("Constructor not found: " + beanDefinition.type.getName());
		}

		beanDefinition.ctor = injectionPointFactory.createCtorInjectionPoint(constructor, ref);
	}

	/**
	 * Registers property injection point.
	 *
	 * @param beanName bean name
	 * @param property property name
	 * @param reference explicit injection reference, may be <code>null</code>
	 */
	public void registerPetitePropertyInjectionPoint(String beanName, String property, String reference) {
		BeanDefinition beanDefinition = lookupExistingBeanDefinition(beanName);
		String[] references = reference == null ? null : new String[] {reference};

		ClassDescriptor cd = ClassIntrospector.lookup(beanDefinition.type);
		PropertyDescriptor propertyDescriptor = cd.getPropertyDescriptor(property, true);
		if (propertyDescriptor == null) {
			throw new PetiteException("Property not found: " + beanDefinition.type.getName() + '#' + property);
		}

		PropertyInjectionPoint pip =
				injectionPointFactory.createPropertyInjectionPoint(propertyDescriptor, references);

		beanDefinition.addPropertyInjectionPoint(pip);
	}

	/**
	 * Registers set injection point.
	 *
	 * @param beanName bean name
	 * @param property set property name
	 */
	public void registerPetiteSetInjectionPoint(String beanName, String property) {
		BeanDefinition beanDefinition = lookupExistingBeanDefinition(beanName);
		ClassDescriptor cd = ClassIntrospector.lookup(beanDefinition.type);

		PropertyDescriptor propertyDescriptor = cd.getPropertyDescriptor(property, true);

		if (propertyDescriptor == null) {
			throw new PetiteException("Property not found: " + beanDefinition.type.getName() + '#' + property);
		}

		SetInjectionPoint sip = injectionPointFactory.createSetInjectionPoint(propertyDescriptor);

		beanDefinition.addSetInjectionPoint(sip);
	}

	/**
	 * Registers method injection point.
	 *
	 * @param beanName bean name
	 * @param methodName method name
	 * @param arguments method arguments, may be <code>null</code>
	 * @param references injection references
	 */
	public void registerPetiteMethodInjectionPoint(String beanName, String methodName, Class[] arguments, String[] references) {
		BeanDefinition beanDefinition = lookupExistingBeanDefinition(beanName);
		String[][] ref = PetiteUtil.convertRefToReferences(references);
		ClassDescriptor cd = ClassIntrospector.lookup(beanDefinition.type);

		Method method = null;
		if (arguments == null) {
			MethodDescriptor[] methods = cd.getAllMethodDescriptors(methodName);
			if (methods != null && methods.length > 0) {
				if (methods.length > 1) {
					throw new PetiteException(methods.length + " suitable methods found as injection points for: " + beanDefinition.type.getName() + '#' + methodName);
				}
				method = methods[0].getMethod();
			}
		} else {
			MethodDescriptor md = cd.getMethodDescriptor(methodName, arguments, true);
			if (md != null) {
				method = md.getMethod();
			}
		}
		if (method == null) {
			throw new PetiteException("Method not found: " + beanDefinition.type.getName() + '#' + methodName);
		}
		MethodInjectionPoint mip = injectionPointFactory.createMethodInjectionPoint(method, ref);

		beanDefinition.addMethodInjectionPoint(mip);
	}

	/**
	 * Registers init method.
	 *
	 * @param beanName bean name
	 * @param invocationStrategy moment of invocation
	 * @param initMethodNames init method names
	 */
	public void registerPetiteInitMethods(String beanName, InitMethodInvocationStrategy invocationStrategy, String... initMethodNames) {
		BeanDefinition beanDefinition = lookupExistingBeanDefinition(beanName);

		ClassDescriptor cd = ClassIntrospector.lookup(beanDefinition.type);
		if (initMethodNames == null) {
			initMethodNames = StringPool.EMPTY_ARRAY;
		}

		int total = initMethodNames.length;
		InitMethodPoint[] initMethodPoints = new InitMethodPoint[total];

		int i;
		for (i = 0; i < initMethodNames.length; i++) {
			MethodDescriptor md = cd.getMethodDescriptor(initMethodNames[i], ClassUtil.EMPTY_CLASS_ARRAY, true);
			if (md == null) {
				throw new PetiteException("Init method not found: " + beanDefinition.type.getName() + '#' + initMethodNames[i]);
			}
			initMethodPoints[i] = new InitMethodPoint(md.getMethod(), i, invocationStrategy);
		}

		beanDefinition.addInitMethodPoints(initMethodPoints);
	}

	/**
	 * Registers destroy method.
	 *
	 * @param beanName bean name
	 * @param destroyMethodNames destroy method names
	 */
	public void registerPetiteDestroyMethods(String beanName, String... destroyMethodNames) {
		BeanDefinition beanDefinition = lookupExistingBeanDefinition(beanName);

		ClassDescriptor cd = ClassIntrospector.lookup(beanDefinition.type);
		if (destroyMethodNames == null) {
			destroyMethodNames = StringPool.EMPTY_ARRAY;
		}

		int total = destroyMethodNames.length;
		DestroyMethodPoint[] destroyMethodPoints = new DestroyMethodPoint[total];

		int i;
		for (i = 0; i < destroyMethodNames.length; i++) {
			MethodDescriptor md = cd.getMethodDescriptor(destroyMethodNames[i], ClassUtil.EMPTY_CLASS_ARRAY, true);
			if (md == null) {
				throw new PetiteException("Destroy method not found: " + beanDefinition.type.getName() + '#' + destroyMethodNames[i]);
			}
			destroyMethodPoints[i] = new DestroyMethodPoint(md.getMethod());
		}

		beanDefinition.addDestroyMethodPoints(destroyMethodPoints);
	}

	// ---------------------------------------------------------------- providers

	/**
	 * Registers instance method provider.
	 *
	 * @param providerName provider name
	 * @param beanName bean name
	 * @param methodName instance method name
	 * @param arguments method argument types
	 */
	public void registerPetiteProvider(String providerName, String beanName, String methodName, Class[] arguments) {
		BeanDefinition beanDefinition = lookupBeanDefinition(beanName);

		if (beanDefinition == null) {
			throw new PetiteException("Bean not found: " + beanName);
		}

		Class beanType = beanDefinition.type;

		ClassDescriptor cd = ClassIntrospector.lookup(beanType);
		MethodDescriptor md = cd.getMethodDescriptor(methodName, arguments, true);

		if (md == null) {
			throw new PetiteException("Provider method not found: " + methodName);
		}

		ProviderDefinition providerDefinition = new ProviderDefinition(providerName, beanName, md.getMethod());

		providers.put(providerName, providerDefinition);
	}

	/**
	 * Registers static method provider.
	 *
	 * @param providerName provider name
	 * @param type class type
	 * @param staticMethodName static method name
	 * @param arguments method argument types
	 */
	public void registerPetiteProvider(String providerName, Class type, String staticMethodName, Class[] arguments) {
		ClassDescriptor cd = ClassIntrospector.lookup(type);
		MethodDescriptor md = cd.getMethodDescriptor(staticMethodName, arguments, true);

		if (md == null) {
			throw new PetiteException("Provider method not found: " + staticMethodName);
		}

		ProviderDefinition providerDefinition = new ProviderDefinition(providerName, md.getMethod());

		providers.put(providerName, providerDefinition);
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
	 * Returns set of all bean names.
	 */
	public Set<String> getBeanNames() {
		return beans.keySet();
	}

	// ---------------------------------------------------------------- params

	/**
	 * Defines new parameter. Parameters with same name will be replaced.
	 */
	public void defineParameter(String name, Object value) {
		paramManager.put(name, value);
	}

	/**
	 * Returns defined parameter.
	 */
	public Object getParameter(String name) {
		return paramManager.get(name);
	}

	/**
	 * Prepares list of all bean parameters and optionally resolves inner references.
	 */
	protected String[] resolveBeanParams(String name, boolean resolveReferenceParams) {
		return paramManager.resolve(name, resolveReferenceParams);
	}

	/**
	 * Defines many parameters at once.
	 */
	public void defineParameters(Map<?, ?> properties) {
		for (Map.Entry<?, ?> entry : properties.entrySet()) {
			defineParameter(entry.getKey().toString(), entry.getValue());
		}
	}

	/**
	 * Defines many parameters at once from {@link jodd.props.Props}.
	 */
	public void defineParameters(Props props) {
		Map<?, ?> map = new HashMap<Object, Object>();
		props.extractProps(map);
		defineParameters(map);
	}

}