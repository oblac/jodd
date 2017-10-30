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
import jodd.introspector.Setter;
import jodd.log.Logger;
import jodd.log.LoggerFactory;
import jodd.petite.meta.InitMethodInvocationStrategy;
import jodd.petite.scope.Scope;
import jodd.petite.scope.SingletonScope;
import jodd.typeconverter.Converter;

import java.util.Collection;

/**
 * Petite IOC container.
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
	public PetiteContainer(PetiteConfig config) {
		super(config);

		scopedProxyManager = new ScopedProxyManager();

		if (log.isDebugEnabled()) {
			log.debug("Petite container created");
		}
	}

	// ---------------------------------------------------------------- core

	/**
	 * Creates new bean instance and performs constructor injection.
	 */
	protected Object newBeanInstance(BeanDefinition def) {
		if (def.ctor == null) {
			def.ctor = petiteResolvers.resolveCtorInjectionPoint(def.type);
		}

		int paramNo = def.ctor.references.length;
		Object[] args = new Object[paramNo];

		// wiring
		if (def.wiringMode != WiringMode.NONE) {
			for (int i = 0; i < paramNo; i++) {
				args[i] = getBean(def.ctor.references[i]);
				if (args[i] == null) {
					if ((def.wiringMode == WiringMode.STRICT)) {
						throw new PetiteException(
								"Wiring constructor failed. References '" + Converter.get().toString(def.ctor.references[i]) +
								"' not found for constructor: " + def.ctor.constructor);
					}
				}
			}
		}

		// create instance
		Object bean;
		try {
			bean = def.ctor.constructor.newInstance(args);
		} catch (Exception ex) {
			throw new PetiteException("Failed to create new bean instance '" + def.type.getName() + "' using constructor: " + def.ctor.constructor, ex);
		}

		return bean;
	}

	/**
	 * Wires beans.
	 * @param bean target bean
	 * @param def bean definition
	 */
	protected void wireBean(Object bean, BeanDefinition def) {
		if (def.wiringMode == WiringMode.NONE) {
			return;
		}
		wireProperties(bean, def);
		wireMethods(bean, def);
	}

	/**
	 * Wires properties.
	 */
	protected void wireProperties(Object bean, BeanDefinition def) {
		if (def.properties == null) {
			def.properties = petiteResolvers.resolvePropertyInjectionPoint(def.type, def.wiringMode == WiringMode.AUTOWIRE);
		}

		boolean mixing = petiteConfig.wireScopedProxy || petiteConfig.detectMixedScopes;

		for (PropertyInjectionPoint pip : def.properties) {
			String[] refNames = pip.references;

			Object value = null;

			if (mixing) {
				BeanDefinition refBeanDefinition = lookupBeanDefinitions(refNames);

				if (refBeanDefinition != null) {
					value = scopedProxyManager.lookupValue(this, def, refBeanDefinition);
				}
			}

			if (value == null) {
				value = getBean(refNames);
			}

			if (value == null) {
				if ((def.wiringMode == WiringMode.STRICT)) {
					throw new PetiteException("Wiring failed. Beans references: '" +
							Converter.get().toString(refNames) + "' not found for property: "+ def.type.getName() +
							'#' + pip.propertyDescriptor.getName());
				}
				continue;
			}

			// BeanUtil.setDeclaredProperty(bean, pip.propertyDescriptor.getName(), value);

			Setter setter = pip.propertyDescriptor.getSetter(true);
			try {
				setter.invokeSetter(bean, value);
			}
			catch (Exception ex) {
				throw new PetiteException("Wiring failed", ex);
			}
		}

		// sets
		if (def.sets == null) {
			def.sets = petiteResolvers.resolveSetInjectionPoint(def.type, def.wiringMode == WiringMode.AUTOWIRE);
		}
		for (SetInjectionPoint sip : def.sets) {

			String[] beanNames = resolveBeanNamesForType(sip.targetClass);

			Collection beans = sip.createSet(beanNames.length);

			for (String beanName : beanNames) {
				if (!beanName.equals(def.name)) {
					Object value = getBean(beanName);
					beans.add(value);
				}
			}

			//BeanUtil.setDeclaredProperty(bean, sip.field.getName(), beans);

			Setter setter = sip.propertyDescriptor.getSetter(true);
			try {
				setter.invokeSetter(bean, beans);
			}
			catch (Exception ex) {
				throw new PetiteException("Wiring failed", ex);
			}
		}
	}

	/**
	 * Wires methods.
	 */
	protected void wireMethods(Object bean, BeanDefinition def) {
		if (def.methods == null) {
			def.methods = petiteResolvers.resolveMethodInjectionPoint(def.type);
		}
		for (MethodInjectionPoint methodRef : def.methods) {
			String[][] refNames = methodRef.references;
			Object[] args = new Object[refNames.length];
			for (int i = 0; i < refNames.length; i++) {
				String[] refName = refNames[i];
				Object value = null;

				boolean mixing = petiteConfig.wireScopedProxy || petiteConfig.detectMixedScopes;

				if (mixing) {
					BeanDefinition refBeanDefinition = lookupBeanDefinitions(refName);

					if (refBeanDefinition != null) {
						value = scopedProxyManager.lookupValue(this, def, refBeanDefinition);
					}
				}

				if (value == null) {
					value = getBean(refName);
				}

				args[i] = value;
				if (value == null) {
					if ((def.wiringMode == WiringMode.STRICT)) {
						throw new PetiteException("Wiring failed. Beans references: '" +
								Converter.get().toString(refName) + "' not found for method: " + def.type.getName() + '#' + methodRef.method.getName());
					}
				}
			}

			try {
				methodRef.method.invoke(bean, args);
			} catch (Exception ex) {
				throw new PetiteException(ex);
			}

		}
	}

	protected void resolveInitAndDestroyMethods(Object bean, BeanDefinition def) {
		if (def.initMethods == null) {
			def.initMethods = petiteResolvers.resolveInitMethodPoint(bean);
		}
		if (def.destroyMethods == null) {
			def.destroyMethods = petiteResolvers.resolveDestroyMethodPoint(bean);
		}
	}

	/**
	 * Invokes all init methods, if they exist. Also resolves destroy methods.
	 */
	protected void invokeInitMethods(Object bean, BeanDefinition def, InitMethodInvocationStrategy invocationStrategy) {
		for (InitMethodPoint initMethod : def.initMethods) {
			if (invocationStrategy != initMethod.invocationStrategy) {
				continue;
			}
			try {
				initMethod.method.invoke(bean);
			} catch (Exception ex) {
				throw new PetiteException("Invalid init method: " + initMethod, ex);
			}
		}
	}

	/**
	 * Injects all parameters.
	 */
	protected void injectParams(Object bean, BeanDefinition def) {
		if (def.name == null) {
			return;
		}

		if (def.params == null) {
			def.params = resolveBeanParams(def.name, petiteConfig.getResolveReferenceParameters());
		}
		int len = def.name.length() + 1;
		for (String param : def.params) {
			Object value = getParameter(param);
			String destination = param.substring(len);
			try {
				BeanUtil.declared.setProperty(bean, destination, value);
			} catch (Exception ex) {
				throw new PetiteException("Unable to set parameter: '" + param + "' to bean: " + def.name, ex);
			}
		}
	}


	// ---------------------------------------------------------------- get beans

	/**
	 * Returns Petite bean instance. Bean name will be resolved from provided type.
	 */
	@SuppressWarnings({"unchecked"})
	public <T> T getBean(Class<T> type) {
		String name = resolveBeanName(type);
		return (T) getBean(name);
	}

	/**
	 * Returns Petite bean instance named as one of the provided names.
	 */
	protected Object getBean(String[] names) {
		for (String name : names) {
			if (name == null) {
				continue;
			}
			Object bean = getBean(name);
			if (bean != null) {
				return bean;
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
	public <T> T getBean(String name) {

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
			bean = newBeanInstance(def);
			registerBeanAndWireAndInjectParamsAndInvokeInitMethods(def, bean);
		}

		return (T) bean;
	}

	/**
	 * Wires bean, injects parameters and invokes init methods.
	 * Such a loooong name :)
	 */
	protected void registerBeanAndWireAndInjectParamsAndInvokeInitMethods(BeanDefinition def, Object bean) {
		resolveInitAndDestroyMethods(bean, def);
		def.scopeRegister(bean);
		invokeInitMethods(bean, def, InitMethodInvocationStrategy.POST_CONSTRUCT);
		wireBean(bean, def);
		invokeInitMethods(bean, def, InitMethodInvocationStrategy.POST_DEFINE);
		injectParams(bean, def);
		invokeInitMethods(bean, def, InitMethodInvocationStrategy.POST_INITIALIZE);
	}

	// ---------------------------------------------------------------- wire

	/**
	 * Wires provided bean with the container using default wiring mode.
	 * Bean is <b>not</b> registered withing container.
	 */
	public void wire(Object bean) {
		wire(bean, null);
	}

	/**
	 * Wires provided bean with the container and optionally invokes init methods.
	 * Bean is <b>not</b> registered withing container.
	 */
	public void wire(Object bean, WiringMode wiringMode) {
		wiringMode = petiteConfig.resolveWiringMode(wiringMode);
		BeanDefinition def = new BeanDefinition(null, bean.getClass(), null, wiringMode);
		registerBeanAndWireAndInjectParamsAndInvokeInitMethods(def, bean);
	}

	// ---------------------------------------------------------------- create

	/**
	 * Creates and wires a bean within the container using default wiring mode and default init methods flag.
	 * Bean is <b>not</b> registered.
	 */
	public <E> E createBean(Class<E> type) {
		return createBean(type, null);
	}

	/**
	 * Creates and wires a bean within the container and optionally invokes init methods. However, bean is
	 * <b>not</b> registered.
	 */
	@SuppressWarnings({"unchecked"})
	public <E> E createBean(Class<E> type, WiringMode wiringMode) {
		wiringMode = petiteConfig.resolveWiringMode(wiringMode);
		BeanDefinition def = new BeanDefinition(null, type, null, wiringMode);
		Object bean = newBeanInstance(def);
		registerBeanAndWireAndInjectParamsAndInvokeInitMethods(def, bean);
		return (E) bean;
	}

	// ---------------------------------------------------------------- providers

	/**
	 * Invokes provider to get a bean.
	 */
	protected Object invokeProvider(ProviderDefinition provider) {
		if (provider.method != null) {

			Object bean;
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
	public void addBean(String name, Object bean) {
		addBean(name, bean, null);
	}

	/**
	 * Adds object instance to the container as singleton bean.
	 */
	public void addBean(String name, Object bean, WiringMode wiringMode) {
		wiringMode = petiteConfig.resolveWiringMode(wiringMode);
		registerPetiteBean(bean.getClass(), name, SingletonScope.class, wiringMode, false);
		BeanDefinition def = lookupExistingBeanDefinition(name);
		registerBeanAndWireAndInjectParamsAndInvokeInitMethods(def, bean);
	}

	/**
	 * Adds self instance to the container so internal beans may fetch
	 * container for further usage. No wiring is used and no init methods are invoked.
	 */
	public void addSelf(String name) {
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
	public void setBeanProperty(String name, Object value) {
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
	public Object getBeanProperty(String name) {
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

	// ---------------------------------------------------------------- shutdown

	/**
	 * Shutdowns container. After container is down, it can't be used anymore.
	 */
	public void shutdown() {
		for (Scope scope : scopes.values()) {
			scope.shutdown();
		}

		beans.clear();
		beansAlt.clear();
		scopes.clear();
		providers.clear();
		beanCollections.clear();

	}

}