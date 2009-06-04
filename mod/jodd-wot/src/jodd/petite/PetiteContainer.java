// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.bean.BeanUtil;
import jodd.petite.config.PetiteConfigurator;
import jodd.petite.manager.PetiteManager;
import jodd.petite.scope.SingletonScope;

import java.util.HashMap;
import java.util.Map;

/**
 * Petite IOC container. Generally, it is composed of two parts: {@link PetiteManager manager} and
 * container. Manager deals with binding information regarding registration and configuration.
 * Container itself is used during runtime for acquiring bean instances.
 */
public class PetiteContainer extends PetiteContainerRegistry {

	public static final String PETITE_CONTAINER_REF_NAME = "petiteContainer";

	public PetiteContainer() {
		this(new PetiteManager(), new PetiteConfig());
	}

	public PetiteContainer(PetiteManager pm, PetiteConfig pcfg) {
		super(pm, pcfg);
	}

	/**
	 * Configures this instance of container.
	 */
	public void confgure(PetiteConfigurator... petiteConfigurators) {
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
			def.ctor = petiteManager.resolveCtorInjectionPoint(def.type);
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
						throw new PetiteException("Wiring constructor failed. Reference '" + def.ctor.references[i] + "' not found for constructor '" + def.ctor.constructor + "'.");
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
			def.properties = petiteManager.resolvePropertyInjectionPoint(def.type);
		}
		for (PropertyInjectionPoint pip : def.properties) {
			if ((def.wiringMode != WiringMode.AUTOWIRE) && (pip.hasAnnotation == false)) {
				continue;
			}
			String refName = pip.reference;

			Object value = getBean(refName, acquiredBeans);
			if (value == null) {
				if ((def.wiringMode == WiringMode.STRICT)) {
					throw new PetiteException("Wiring failed. Reference '" + refName + "' not found for property '"+ def.type.getName() + '#' + pip.field.getName() + "'.");
				}
				continue;
			}
			BeanUtil.setDeclaredProperty(bean, pip.field.getName(), value);
		}
	}

	/**
	 * Wires methods.
	 */
	protected void wireMethods(Object bean, BeanDefinition def, Map<String, Object> acquiredBeans) {
		if (def.methods == null) {
			def.methods = petiteManager.resolveMethodInjectionPoint(def.type);
		}
		for (MethodInjectionPoint methodRef : def.methods) {
			String[] refNames = methodRef.references;
			Object[] args = new Object[refNames.length];
			for (int i = 0; i < refNames.length; i++) {
				String refName = refNames[i];
				args[i] = getBean(refName, acquiredBeans);
				if (args[i] == null) {
					if ((def.wiringMode == WiringMode.STRICT)) {
						throw new PetiteException("Wiring failed. Reference '" + refName + "' not found for method '" + def.type.getName() + '#' + methodRef.method.getName() + "()'.");
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
	protected void invokeInitMethods(Object bean, BeanDefinition def) {
		if (def.initMethods == null) {
			def.initMethods = petiteManager.resolveInitMethods(bean);
		}
		for (InitMethodPoint initMethod : def.initMethods) {
			try {
				initMethod.method.invoke(bean);
			} catch (Exception ex) {
				throw new PetiteException("Unable to invoke init method: " + initMethod, ex);
			}
		}
	}


	// ---------------------------------------------------------------- get beans

	/**
	 * Returns Petite bean instance. Bean name will be resolved from provided type.
	 */
	@SuppressWarnings({"unchecked"})
	public <T> T getBean(Class<T> type) {
		String name = PetiteUtil.resolveBeanName(type);
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
	 * Returns petite bean instance.
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
		BeanDefinition def = petiteManager.lookupBeanDefinition(name);
		if (def == null) {
			return null;
		}

		// Find the bean in its scope
		bean = def.scopeLookup();
		if (bean == null) {
			// Create new bean in the scope
			bean = newBeanInstance(def, acquiredBeans);
			wireBean(bean, def, acquiredBeans);
			invokeInitMethods(bean, def);
			def.scopeRegister(bean);
		}
		return bean;
	}

	// ---------------------------------------------------------------- wire

	/**
	 * Wires provided bean with the container.
	 */
	public PetiteContainer wire(Object bean) {
		return wire(bean, null, false);
	}

	public PetiteContainer wire(Object bean, boolean init) {
		return wire(bean, null, init);
	}

	public PetiteContainer wire(Object bean, WiringMode wiringMode) {
		return wire(bean, wiringMode, false);
	}

	/**
	 * Wires provided bean with the container and optionally invokes init methods.
	 */
	public PetiteContainer wire(Object bean, WiringMode wiringMode, boolean init) {
		BeanDefinition def = new BeanDefinition(null, bean.getClass(), null, petiteConfig.resolveWiringMode(wiringMode));
		wireBean(bean, def, new HashMap<String, Object>());
		if (init) {
			invokeInitMethods(bean,  def);
		}
		return this;
	}

	// ---------------------------------------------------------------- create

	/**
	 * Creates and wires a bean with the container. However, bean is
	 * <b>not</b> registered.
	 */
	public <E> E createBean(Class<E> type) {
		return createBean(type, null);
	}

	/**
	 * Creates and wires a bean with the container. However, bean is
	 * <b>not</b> registered.
	 */
	@SuppressWarnings({"unchecked"})
	public <E> E createBean(Class<E> type, WiringMode wiringMode) {
		wiringMode = petiteConfig.resolveWiringMode(wiringMode);
		Map<String, Object> acquiredBeans = new HashMap<String, Object>();
		BeanDefinition def = new BeanDefinition(null, type, null, wiringMode);
		Object bean = newBeanInstance(def, acquiredBeans);
		wireBean(bean, def, acquiredBeans);
		invokeInitMethods(bean, def);
		return (E) bean;
	}

	

	// ---------------------------------------------------------------- add

	/**
	 * Adds object instance to the container as singleton.
	 */
	public PetiteContainer addBean(String name, Object object) {
		registerBean(name, null, SingletonScope.class, WiringMode.NONE);
		BeanDefinition beanDefinition = lookupExistingBeanDefinition(name);
		beanDefinition.scopeRegister(object);
		return this;
	}

	/**
	 * Adds self instance to the container so internal beans may fetch
	 * container for further usage.
	 */
	public PetiteContainer addSelf(String name) {
		return addBean(name, this);
	}

	/**
	 * Adds self instance to the container so internal beans may fetch
	 * container for further usage.
	 */
	public PetiteContainer addSelf() {
		return addBean(PETITE_CONTAINER_REF_NAME, this);
	}


	// ---------------------------------------------------------------- property

	/**
	 * Sets petite bean property.
	 */
	public void setBeanProperty(String name, Object value) {
		int ndx = name.indexOf('.');
		if (ndx == -1) {
			throw new PetiteException("Only bean name is specified, missing property name: '" + name + "'.");
		}
		String beanName = name.substring(0, ndx);
		Object bean = getBean(beanName);
		if (bean == null) {
			throw new PetiteException("Bean doesn't exist: '" + name + "'.");
		}
		try {
			BeanUtil.setDeclaredProperty(bean, name.substring(ndx + 1), value);
		} catch (Exception ex) {
			throw new PetiteException("Unable to set bean property: '" + name);
		}
	}

	/**
	 * Returns petite bean proerty value.
	 */
	public Object getBeanProperty(String name) {
		int ndx = name.indexOf('.');
		if (ndx == -1) {
			throw new PetiteException("Only bean name is specified, missing property name: '" + name + "'.");
		}
		String beanName = name.substring(0, ndx);
		Object bean = getBean(beanName);
		if (bean == null) {
			throw new PetiteException("Bean doesn't exist: '" + name + "'.");
		}
		try {
			return BeanUtil.getDeclaredProperty(bean, name.substring(ndx + 1));
		} catch (Exception ex) {
			throw new PetiteException("Unable to set bean property: '" + name);
		}
	}

}
