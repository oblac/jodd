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

import jodd.bean.BeanUtil;
import jodd.log.Logger;
import jodd.log.LoggerFactory;
import jodd.petite.def.BeanReferences;
import jodd.petite.def.MethodInjectionPoint;
import jodd.petite.def.ProviderDefinition;
import jodd.petite.meta.InitMethodInvocationStrategy;
import jodd.petite.scope.Scope;
import jodd.petite.scope.SingletonScope;

import java.lang.reflect.Method;

/**
 * Petite IOC container.
 * @see PetiteRegistry for fluent java registration of Petite beans.
 */
public class PetiteContainer extends PetiteBeans {

	private static final Logger log = LoggerFactory.getLogger(PetiteContainer.class);

	/**
	 * Petite container reference name.
	 * Used when container itself is added as its bean.
	 * @see #addSelf()
	 * @see #addSelf(String)
	 */
	public static final String PETITE_CONTAINER_REF_NAME = "petiteContainer";

	protected final ScopedProxyManager scopedProxyManager;

	/**
	 * Creates new Petite container using {@link PetiteConfig default configuration}.
	 */
	public PetiteContainer() {
		this(new PetiteConfig());
	}

	/**
	 * Creates new Petite container using {@link PetiteContainer provided configuration}.
	 */
	public PetiteContainer(final PetiteConfig config) {
		super(config);

		scopedProxyManager = new ScopedProxyManager();

		if (log.isDebugEnabled()) {
			log.debug("Petite container created");
		}
	}

	// ---------------------------------------------------------------- core

	protected Object lookupMixingScopedBean(final BeanDefinition def, final BeanReferences refNames) {
		final boolean mixing = petiteConfig.wireScopedProxy || petiteConfig.detectMixedScopes;

		Object value = null;

		if (mixing) {
			final BeanDefinition refBeanDefinition = lookupBeanDefinitions(refNames);

			if (refBeanDefinition != null) {
				value = scopedProxyManager.lookupValue(PetiteContainer.this, def, refBeanDefinition);
			}
		}

		if (value == null) {
			value = PetiteContainer.this.getBean(refNames);
		}

		return value;
	}

	// ---------------------------------------------------------------- get beans

	/**
	 * Returns Petite bean instance. Bean name will be resolved from provided type.
	 */
	@SuppressWarnings({"unchecked"})
	public <T> T getBean(final Class<T> type) {
		String name = resolveBeanName(type);
		return (T) getBean(name);
	}

	/**
	 * Returns Petite bean instance named as one of the provided names.
	 * Returns {@code null} if bean is not found.
	 */
	protected Object getBean(final BeanReferences beanReferences) {
		final int total = beanReferences.size();

		for (int i = 0; i < total; i++) {
			String name = beanReferences.name(i);

			if (name != null) {
				Object bean = getBean(name);
				if (bean != null) {
					return bean;
				}
			}
		}
		return null;
	}

	/**
	 * Returns Petite bean instance.
	 * Petite container will find the bean in corresponding scope and all its dependencies,
	 * either by constructor or property injection. When using constructor injection, cyclic dependencies
	 * can not be prevented, but at least they are detected.
	 *
	 * @see PetiteContainer#createBean(Class)
	 */
	public <T> T getBean(final String name) {

		// Lookup for registered bean definition.
		BeanDefinition def = lookupBeanDefinition(name);

		if (def == null) {

			// try provider
			ProviderDefinition providerDefinition = providers.get(name);

			if (providerDefinition != null) {
				return (T) invokeProvider(providerDefinition);
			}
			return null;
		}

		// Find the bean in its scope
		Object bean = def.scopeLookup();

		if (bean == null) {
			// Create new bean in the scope
			initBeanDefinition(def);
			final BeanData beanData = new BeanData(this, def);
			registerBeanAndWireAndInjectParamsAndInvokeInitMethods(beanData);
			bean = beanData.bean();
		}

		return (T) bean;
	}

	/**
	 * Resolves and initializes bean definition. May be called multiple times.
	 */
	protected void initBeanDefinition(final BeanDefinition def) {
		// init methods
		if (def.initMethods == null) {
			def.initMethods = petiteResolvers.resolveInitMethodPoint(def.type);
		}
		// destroy methods
		if (def.destroyMethods == null) {
			def.destroyMethods = petiteResolvers.resolveDestroyMethodPoint(def.type);
		}
		// properties
		if (def.properties == null) {
			def.properties = petiteResolvers.resolvePropertyInjectionPoint(def.type, def.wiringMode == WiringMode.AUTOWIRE);
		}
		// methods
		if (def.methods == null) {
			def.methods = petiteResolvers.resolveMethodInjectionPoint(def.type);
		}
		// ctors
		if (def.ctor == null) {
			def.ctor = petiteResolvers.resolveCtorInjectionPoint(def.type);
		}
		// values
		if (def.values == null) {
			def.values = paramManager.resolveParamInjectionPoints(def.type);
		}
		// sets
		if (def.sets == null) {
			def.sets = petiteResolvers.resolveSetInjectionPoint(def.type, def.wiringMode == WiringMode.AUTOWIRE);
		}
		// params
		if (def.params == null) {
			def.params = paramManager.filterParametersForBeanName(def.name, petiteConfig.getResolveReferenceParameters());
		}
	}

	/**
	 * Wires bean, injects parameters and invokes init methods.
	 * Such a loooong name :)
	 */
	protected void registerBeanAndWireAndInjectParamsAndInvokeInitMethods(final BeanData beanData) {
		initBeanDefinition(beanData.definition());

		beanData.scopeRegister();
		beanData.invokeInitMethods(InitMethodInvocationStrategy.POST_CONSTRUCT);
		beanData.wireBean();
		beanData.invokeInitMethods(InitMethodInvocationStrategy.POST_DEFINE);
		beanData.injectParams(paramManager, petiteConfig.isImplicitParamInjection());
		beanData.invokeInitMethods(InitMethodInvocationStrategy.POST_INITIALIZE);
		beanData.invokeConsumerIfRegistered();
	}

	// ---------------------------------------------------------------- wire

	/**
	 * Wires provided bean with the container using default wiring mode.
	 * Bean is <b>not</b> registered withing container.
	 */
	public void wire(final Object bean) {
		wire(bean, null);
	}

	/**
	 * Wires provided bean with the container and optionally invokes init methods.
	 * Bean is <b>not</b> registered withing container.
	 */
	public void wire(final Object bean, final WiringMode wiringMode) {
		final WiringMode finalWiringMode = petiteConfig.resolveWiringMode(wiringMode);

		final BeanDefinition def = externalsCache.get(
			bean.getClass(), () -> {
				final BeanDefinition beanDefinition = createBeandDefinitionForExternalBeans(bean.getClass(), finalWiringMode);
				initBeanDefinition(beanDefinition);
				return beanDefinition;
			});

		registerBeanAndWireAndInjectParamsAndInvokeInitMethods(new BeanData(this, def, bean));
	}

	/**
	 * Invokes the method of some bean with the container, when its parameters requires to be injected into.
	 * The bean is <b>not</b> registered within container.
	 */
	public <T> T invokeMethod(final Object bean, final Method method) {
		final WiringMode wiringMode = petiteConfig.resolveWiringMode(null);

		final BeanDefinition def = externalsCache.get(
			bean.getClass(), () -> {
				final BeanDefinition beanDefinition = createBeandDefinitionForExternalBeans(bean.getClass(), wiringMode);
				initBeanDefinition(beanDefinition);
				return beanDefinition;
			});

		final BeanData beanData = new BeanData(this, def, bean);

		for (MethodInjectionPoint methodInjectionPoint : def.methods) {
			if (methodInjectionPoint.method.equals(method)) {
				return (T) beanData.invokeMethodInjectionPoint(methodInjectionPoint);
			}
		}
		try {
			return (T) method.invoke(bean);
		} catch (Exception e) {
			throw new PetiteException(e);
		}
	}

	// ---------------------------------------------------------------- create

	/**
	 * Creates and wires a bean within the container using default wiring mode and default init methods flag.
	 * Bean is <b>not</b> registered.
	 */
	public <E> E createBean(final Class<E> type) {
		return createBean(type, null);
	}

	/**
	 * Creates and wires a bean within the container and optionally invokes init methods. However, bean is
	 * <b>not</b> registered.
	 */
	@SuppressWarnings({"unchecked"})
	public <E> E createBean(final Class<E> type, final WiringMode wiringMode) {
		final WiringMode finalWiringMode = petiteConfig.resolveWiringMode(wiringMode);

		final BeanDefinition def = externalsCache.get(
			type, () -> {
				final BeanDefinition beanDefinition = createBeandDefinitionForExternalBeans(type, finalWiringMode);
				initBeanDefinition(beanDefinition);
				return beanDefinition;
			});

		final BeanData<E> beanData = new BeanData(this, def);
		registerBeanAndWireAndInjectParamsAndInvokeInitMethods(beanData);
		return beanData.bean();
	}

	// ---------------------------------------------------------------- providers

	/**
	 * Invokes provider to get a bean.
	 */
	protected Object invokeProvider(final ProviderDefinition provider) {
		if (provider.method != null) {

			final Object bean;
			if (provider.beanName != null) {
				// instance factory method
				bean = getBean(provider.beanName);
			} else {
				// static factory method
				bean = null;
			}
			try {
				return provider.method.invoke(bean);
			} catch (Exception ex) {
				throw new PetiteException("Invalid provider method: " + provider.method.getName(), ex);
			}
		}

		throw new PetiteException("Invalid provider");
	}

	// ---------------------------------------------------------------- add

	/**
	 * Adds object instance to the container as singleton bean using default
	 * wiring mode and default init method flag.
	 */
	public void addBean(final String name, final Object bean) {
		addBean(name, bean, null);
	}

	/**
	 * Adds object instance to the container as singleton bean.
	 */
	public void addBean(final String name, final Object bean, WiringMode wiringMode) {
		wiringMode = petiteConfig.resolveWiringMode(wiringMode);
		registerPetiteBean(bean.getClass(), name, SingletonScope.class, wiringMode, false, null);
		BeanDefinition def = lookupExistingBeanDefinition(name);
		registerBeanAndWireAndInjectParamsAndInvokeInitMethods(new BeanData(this, def, bean));
	}

	/**
	 * Adds self instance to the container so internal beans may fetch
	 * container for further usage. No wiring is used and no init methods are invoked.
	 */
	public void addSelf(final String name) {
		addBean(name, this, WiringMode.NONE);
	}

	/**
	 * Adds self instance to the container so internal beans may fetch
	 * container for further usage. No wiring is used and no init methods are invoked.
	 */
	public void addSelf() {
		addBean(PETITE_CONTAINER_REF_NAME, this, WiringMode.NONE);
	}

	// ---------------------------------------------------------------- property

	/**
	 * Sets petite bean property.
	 */
	public void setBeanProperty(final String name, final Object value) {
		Object bean = null;
		int ndx = name.length();

		while (true) {
			ndx = name.lastIndexOf('.', ndx);
			if (ndx == -1) {
				break;
			}

			String beanName = name.substring(0, ndx);
			bean = getBean(beanName);
			if (bean != null) {
				break;
			}
			ndx--;
		}

		if (bean == null) {
			throw new PetiteException("Invalid bean property: " + name);
		}

		try {
			BeanUtil.declared.setProperty(bean, name.substring(ndx + 1), value);
		} catch (Exception ex) {
			throw new PetiteException("Invalid bean property: " + name, ex);
		}
	}

	/**
	 * Returns petite bean property value.
	 */
	public Object getBeanProperty(final String name) {
		int ndx = name.indexOf('.');
		if (ndx == -1) {
			throw new PetiteException("Only bean name is specified, missing property name: " + name);
		}
		String beanName = name.substring(0, ndx);
		Object bean = getBean(beanName);
		if (bean == null) {
			throw new PetiteException("Bean doesn't exist: " + name);
		}
		try {
			return BeanUtil.declared.getProperty(bean, name.substring(ndx + 1));
		} catch (Exception ex) {
			throw new PetiteException("Invalid bean property: " + name, ex);
		}
	}


	// ---------------------------------------------------------------- registry

	/**
	 * Creates {@link PetiteRegistry} helper tool for this container.
	 */
	public PetiteRegistry createContainerRegistry() {
		return PetiteRegistry.of(this);
	}


	// ---------------------------------------------------------------- shutdown

	/**
	 * Shutdowns container. After container is down, it can't be used anymore.
	 */
	public void shutdown() {
		scopes.forEachValue(Scope::shutdown);


		externalsCache.clear();
		beans.clear();
		beansAlt.clear();
		scopes.clear();
		providers.clear();
		beanCollections.clear();
	}

}