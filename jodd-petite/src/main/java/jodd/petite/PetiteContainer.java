// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.Jodd;
import jodd.bean.BeanUtil;
import jodd.petite.config.PetiteConfigurator;
import jodd.petite.meta.InitMethodInvocationStrategy;
import jodd.petite.scope.SingletonScope;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jodd.typeconverter.Convert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Petite IOC container. Consist of following layers:
 * <ul>
 * <li>PetiteContainer - top layer that provides business usage methods
 * <li>{@link PetiteRegistry} - beans storage methods
 * <li>{@link PetiteBeans} - base layer for storing beans in scopes
 */
public class PetiteContainer extends PetiteRegistry {

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

		if (Jodd.isProxettaLoaded()) {
			scopedProxyManager = new ScopedProxyManager();
		} else {
			scopedProxyManager = null;
		}

		if (log.isDebugEnabled()) {
			log.debug("Petite container created.");

			if (Jodd.isProxettaLoaded()) {
				log.debug("Petite proxy features enabled.");
			} else {
				log.debug("Petite proxy features not available.");
			}
		}
	}

	/**
	 * Configures this instance of container.
	 */
	public void configure(PetiteConfigurator... petiteConfigurators) {
		for (PetiteConfigurator petiteConfigurator : petiteConfigurators) {
			petiteConfigurator.configure(this);
		}
	}

	// ---------------------------------------------------------------- core

	/**
	 * Creates new bean instance and performs constructor injection.
	 */
	protected Object newBeanInstance(BeanDefinition def, Map<String, Object> acquiredBeans) {
		if (def.ctor == null) {
			def.ctor = resolveCtorInjectionPoint(def.type);
		}

		// other ctors
		if (def.name != null) {
			acquiredBeans.put(def.name, Void.TYPE);     // puts a dummy marker for cyclic dependency check
		}

		int paramNo = def.ctor.references.length;
		Object[] args = new Object[paramNo];

		// wiring
		if (def.wiringMode != WiringMode.NONE) {
			for (int i = 0; i < paramNo; i++) {
				args[i] = getBean(def.ctor.references[i], acquiredBeans);
				if (args[i] == null) {
					if ((def.wiringMode == WiringMode.STRICT)) {
						throw new PetiteException(
								"Wiring constructor failed. References '" + Convert.toString(def.ctor.references[i]) +
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
			throw new PetiteException("Unable to create new bean instance '" + def.type.getName() + "' using constructor: '" + def.ctor.constructor + "'.", ex);
		}

		if (def.name != null) {
			acquiredBeans.put(def.name, bean);
		}
		return bean;
	}

	/**
	 * Wires beans.
	 * @param bean target bean
	 * @param def bean definition
	 * @param acquiredBeans set of acquired beans
	 */
	protected void wireBean(Object bean, BeanDefinition def, Map<String, Object> acquiredBeans) {
		if (def.wiringMode == WiringMode.NONE) {
			return;
		}
		wireFields(bean, def, acquiredBeans);
		wireMethods(bean, def, acquiredBeans);
	}

	/**
	 * Wires fields.
	 */
	protected void wireFields(Object bean, BeanDefinition def, Map<String, Object> acquiredBeans) {
		if (def.properties == null) {
			def.properties = resolvePropertyInjectionPoint(def.type, def.wiringMode == WiringMode.AUTOWIRE);
		}

		boolean mixing = petiteConfig.wireScopedProxy || petiteConfig.detectMixedScopes;

		for (PropertyInjectionPoint pip : def.properties) {
			String[] refName = pip.reference;

			Object value = null;

			if (mixing) {
				BeanDefinition refBeanDefinition = lookupBeanDefinition(refName);

				if (refBeanDefinition != null) {
					value = scopedProxyManager.lookupValue(this, def, refBeanDefinition);
				}
			}

			if (value == null) {
				value = getBean(refName, acquiredBeans);
			}

			if (value == null) {
				if ((def.wiringMode == WiringMode.STRICT)) {
					throw new PetiteException("Wiring failed. Beans references: '" +
							Convert.toString(refName) + "' not found for property '"+ def.type.getName() + '#' + pip.field.getName() + "'.");
				}
				continue;
			}
			BeanUtil.setDeclaredProperty(bean, pip.field.getName(), value);
		}

		// sets
		if (def.sets == null) {
			def.sets = resolveCollectionInjectionPoint(def.type, def.wiringMode == WiringMode.AUTOWIRE);
		}
		for (SetInjectionPoint sip : def.sets) {

			String[] beanNames = resolveBeanNamesForType(sip.targetClass);

			Collection beans = sip.createSet(beanNames.length);

			for (String beanName : beanNames) {
				if (beanName.equals(def.name) == false) {
					Object value = getBean(beanName, acquiredBeans);
					beans.add(value);
				}
			}

			BeanUtil.setDeclaredProperty(bean, sip.field.getName(), beans);
		}
	}

	/**
	 * Wires methods.
	 */
	protected void wireMethods(Object bean, BeanDefinition def, Map<String, Object> acquiredBeans) {
		if (def.methods == null) {
			def.methods = resolveMethodInjectionPoint(def.type);
		}
		for (MethodInjectionPoint methodRef : def.methods) {
			String[][] refNames = methodRef.references;
			Object[] args = new Object[refNames.length];
			for (int i = 0; i < refNames.length; i++) {
				String[] refName = refNames[i];
				Object value = null;

				boolean mixing = petiteConfig.wireScopedProxy || petiteConfig.detectMixedScopes;

				if (mixing) {
					BeanDefinition refBeanDefinition = lookupBeanDefinition(refName);

					if (refBeanDefinition != null) {
						value = scopedProxyManager.lookupValue(this, def, refBeanDefinition);
					}
				}

				if (value == null) {
					value = getBean(refName, acquiredBeans);
				}

				args[i] = value;
				if (value == null) {
					if ((def.wiringMode == WiringMode.STRICT)) {
						throw new PetiteException("Wiring failed. Beans references: '" +
								Convert.toString(refName) + "' not found for method '" + def.type.getName() + '#' + methodRef.method.getName() + "()'.");
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

	/**
	 * Invokes all init methods.
	 */
	protected void invokeInitMethods(Object bean, BeanDefinition def, InitMethodInvocationStrategy invocationStrategy) {
		if (def.initMethods == null) {
			def.initMethods = resolveInitMethods(bean);
		}
		for (InitMethodPoint initMethod : def.initMethods) {
			if (invocationStrategy != initMethod.invocationStrategy) {
				continue;
			}
			try {
				initMethod.method.invoke(bean);
			} catch (Exception ex) {
				throw new PetiteException("Unable to invoke init method: " + initMethod, ex);
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
				BeanUtil.setDeclaredProperty(bean, destination, value);
			} catch (Exception ex) {
				throw new PetiteException("Unable to set parameter: '" + param + "' to bean '" + def.name + "'.", ex);
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
	 * Returns Petite bean instance.
	 * Petite container will find the bean in corresponding scope and all its dependencies,
	 * either by constructor or property injection. When using constructor injection, cyclic dependencies
	 * can not be prevented, but at least they are detected.
	 *
	 * @see PetiteContainer#createBean(Class)
	 */
	public Object getBean(String name) {
		return getBean(name, new HashMap<String, Object>());
	}

	/**
	 * Returns Petite bean instance named as one of the provided names.
	 */
	protected Object getBean(String[] names, Map<String, Object> acquiredBeans) {
		for (String name : names) {
			if (name == null) {
				continue;
			}
			Object bean = getBean(name, acquiredBeans);
			if (bean != null) {
				return bean;
			}
		}
		return null;
	}

	/**
	 * Returns Petite bean instance.
	 * @see PetiteContainer#createBean(Class)
	 */
	protected Object getBean(String name, Map<String, Object> acquiredBeans) {

		// First check if bean is already acquired within this call.
		// This prevents cyclic dependencies problem. It is expected than single
		// object tree path contains less elements, therefore, this search is faster
		// then the next one.
		Object bean = acquiredBeans.get(name);
		if (bean != null) {
			if (bean == Void.TYPE) {
				throw new PetiteException("Cycle dependencies on constructor injection detected!");
			}
			return bean;
		}

		// Lookup for registered bean definition.
		BeanDefinition def = lookupBeanDefinition(name);
		if (def == null) {
			return null;
		}

		// Find the bean in its scope
		bean = def.scopeLookup();
		if (bean == null) {
			// Create new bean in the scope
			bean = newBeanInstance(def, acquiredBeans);
			wireBeanInjectParamsAndInvokeInitMethods(def, bean, acquiredBeans);
			def.scopeRegister(bean);
		}
		return bean;
	}

	/**
	 * Wires bean, injects parameters and invokes init methods.
	 */
	protected void wireBeanInjectParamsAndInvokeInitMethods(BeanDefinition def, Object bean, Map<String, Object> acquiredBeans) {
		invokeInitMethods(bean, def, InitMethodInvocationStrategy.POST_CONSTRUCT);
		wireBean(bean, def, acquiredBeans);
		invokeInitMethods(bean, def, InitMethodInvocationStrategy.POST_DEFINE);
		injectParams(bean, def);
		invokeInitMethods(bean, def, InitMethodInvocationStrategy.POST_INITIALIZE);
	}

	// ---------------------------------------------------------------- wire

	/**
	 * Wires provided bean with the container using default wiring mode.
	 * Bean is <b>not</b> registered.
	 */
	public void wire(Object bean) {
		wire(bean, null);
	}

	/**
	 * Wires provided bean with the container and optionally invokes init methods.
	 * Bean is <b>not</b> registered.
	 */
	public void wire(Object bean, WiringMode wiringMode) {
		wiringMode = petiteConfig.resolveWiringMode(wiringMode);
		BeanDefinition def = new BeanDefinition(null, bean.getClass(), null, wiringMode);
		Map<String, Object> acquiredBeans = new HashMap<String, Object>();
		wireBeanInjectParamsAndInvokeInitMethods(def, bean, acquiredBeans);
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
		Map<String, Object> acquiredBeans = new HashMap<String, Object>();
		Object bean = newBeanInstance(def, acquiredBeans);
		wireBeanInjectParamsAndInvokeInitMethods(def, bean, acquiredBeans);
		return (E) bean;
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
		registerBean(name, bean.getClass(), SingletonScope.class, wiringMode);
		BeanDefinition def = lookupExistingBeanDefinition(name);
		Map<String, Object> acquiredBeans = new HashMap<String, Object>();
		acquiredBeans.put(name, bean);
		wireBeanInjectParamsAndInvokeInitMethods(def, bean, acquiredBeans);
		def.scopeRegister(bean);
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
			BeanUtil.setDeclaredProperty(bean, name.substring(ndx + 1), value);
		} catch (Exception ex) {
			throw new PetiteException("Unable to set bean property: " + name, ex);
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
			return BeanUtil.getDeclaredProperty(bean, name.substring(ndx + 1));
		} catch (Exception ex) {
			throw new PetiteException("Unable to set bean property: " + name, ex);
		}
	}

}