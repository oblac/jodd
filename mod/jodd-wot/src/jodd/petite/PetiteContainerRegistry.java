// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.petite.scope.Scope;
import jodd.petite.manager.PetiteManager;

import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;

/**
 * Petite register base contains registration and configuration stuff.
 */
public abstract class PetiteContainerRegistry {

	protected final PetiteManager petiteManager;
	protected final PetiteConfig petiteConfig;

	protected PetiteContainerRegistry(PetiteManager petiteManager, PetiteConfig petiteConfig) {
		this.petiteManager = petiteManager;
		this.petiteConfig = petiteConfig;
	}

	/**
	 * Returns Petite manager.
	 */
	public PetiteManager getManager() {
		return petiteManager;
	}

	/**
	 * Returns Petite config.
	 */
	public PetiteConfig getConfig() {
		return petiteConfig;
	}

	// ---------------------------------------------------------------- bean

	/**
	 * Registers Petite bean class.
	 */
	public void registerBean(Class type) {
		registerPetiteBean(null, type, null, null);
	}

	/**
	 * Registers Petite bean class within specified scope.
	 */
	public void registerBean(Class type, Class<? extends Scope> scopeType) {
		registerPetiteBean(null, type, scopeType, null);
	}

	/**
	 * Registers Petite bean class within specified scope and with specified auto-wire behavior.
	 */
	public void registerBean(Class type, Class<? extends Scope> scopeType, WiringMode wiringMode) {
		registerPetiteBean(null, type, scopeType, wiringMode);
	}

	/**
	 * Registers Petite bean class with specified name.
	 */
	public void registerBean(String name, Class type) {
		registerPetiteBean(name, type, null, null);
	}

	/**
	 * Registers Petite bean class with specified name within specified scope.
	 */
	public void registerBean(String name, Class type, Class<? extends Scope> scopeType) {
		registerPetiteBean(name, type, scopeType, null);
	}

	/**
	 * Registers Petite bean class with specified name within specified scope and with specified auto-wire behavior.
	 * Full registration.
	 */
	public void registerBean(String name, Class type, Class<? extends Scope> scopeType, WiringMode wiringMode) {
		registerPetiteBean(name, type, scopeType, wiringMode);
	}

	/**
	 * Single point of bean registration.
	 */
	protected BeanDefinition registerPetiteBean(String name, Class type, Class<? extends Scope> scopeType, WiringMode wiringMode) {
		return petiteManager.registerBean(name, type, scopeType, wiringMode, petiteConfig);
	}


	/**
	 * Lookup for bean definition. Returns <code>null</code> if bean name doesn't exist.
	 */
	public BeanDefinition lookupBeanDefinition(String name) {
		return petiteManager.lookupBeanDefinition(name);
	}

	/**
	 * Lookups for existing bean. Throws exception if bean is not found.
	 */
	public BeanDefinition lookupExistingBeanDefinition(String name) {
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

	// ---------------------------------------------------------------- define

	public void defineBean(Class type) {
		definePetiteBean(null, type, null, null);
	}

	public void defineBean(Class type, Class<? extends Scope> scopeType) {
		definePetiteBean(null, type, scopeType, null);
	}

	public void defineBean(Class type, Class<? extends Scope> scopeType, WiringMode wiringMode) {
		definePetiteBean(null, type, scopeType, wiringMode);
	}

	public void defineBean(String name, Class type) {
		definePetiteBean(name, type, null, null);
	}

	public void defineBean(String name, Class type, Class<? extends Scope> scopeType) {
		definePetiteBean(name, type, scopeType, null);
	}

	public void defineBean(String name, Class type, Class<? extends Scope> scopeType, WiringMode wiringMode) {
		definePetiteBean(name, type, scopeType, wiringMode);
	}

	protected void definePetiteBean(String name, Class type, Class<? extends Scope> scopeType, WiringMode wiringMode) {
		BeanDefinition def = registerPetiteBean(name, type, scopeType, wiringMode);
		def.ctor = petiteManager.resolveCtorInjectionPoint(type);
		def.properties = PropertyInjectionPoint.EMPTY;
		def.methods = MethodInjectionPoint.EMPTY;
		def.initMethods = InitMethodPoint.EMPTY;
	}


	// ---------------------------------------------------------------- ctor

	/**
	 * Registers constructor injection point.
	 */
	public void registerCtorInjectionPoint(String beanName) {
		registerPetiteCtorInjectionPoint(beanName, null, null);
	}

	/**
	 * Registers constructor injection point.
	 */
	public void registerCtorInjectionPoint(String beanName, Class[] paramTypes) {
		registerPetiteCtorInjectionPoint(beanName, paramTypes, null);
	}
	
	/**
	 * Registers constructor injection point.
	 */
	public void registerCtorInjectionPoint(String beanName, String... references) {
		registerPetiteCtorInjectionPoint(beanName, null, references);
	}

	/**
	 * Registers constructor injection point.
	 */
	public void registerCtorInjectionPoint(String beanName, Class[] paramTypes, String... references) {
		registerPetiteCtorInjectionPoint(beanName, paramTypes, references);
	}

	/**
	 * Single point of constructor injection point registration.
	 */
	protected void registerPetiteCtorInjectionPoint(String beanName, Class[] paramTypes, String[] references) {
		BeanDefinition beanDefinition = lookupExistingBeanDefinition(beanName);
		beanDefinition.ctor = petiteManager.defineCtorInjectionPoint(beanDefinition.type, paramTypes, references);
	}

	// ---------------------------------------------------------------- property


	/**
	 * Registers property injection point.
	 */
	public void registerPropertyInjectionPoint(String beanName, String property) {
		registerPetitePropertyInjectionPoint(beanName, property, null);
	}

	/**
	 * Registers property injection point.
	 */
	public void registerPropertyInjectionPoint(String beanName, String property, String reference) {
		registerPetitePropertyInjectionPoint(beanName, property, reference);
	}

	/**
	 * Single point of property injection point registration.
	 */
	protected void registerPetitePropertyInjectionPoint(String beanName, String property, String reference) {
		BeanDefinition beanDefinition = lookupExistingBeanDefinition(beanName);
		PropertyInjectionPoint pip = petiteManager.definePropertyInjectionPoint(beanDefinition.type, property, reference);
		beanDefinition.addPropertyInjectionPoint(pip);
	}

	// ---------------------------------------------------------------- method

	/**
	 * Registers method injection point.
	 */
	public void registerMethodInjectionPoint(String beanName, String methodName) {
		registerPetiteMethodInjectionPoint(beanName, methodName, null, null);
	}

	/**
	 * Registers method injection point.
	 */
	public void registerMethodInjectionPoint(String beanName, String methodName, String... references) {
		registerPetiteMethodInjectionPoint(beanName, methodName, null, references);
	}

	/**
	 * Registers method injection point.
	 */
	public void registerMethodInjectionPoint(String beanName, String methodName, Class[] arguments) {
		registerPetiteMethodInjectionPoint(beanName, methodName, arguments, null);
	}

	/**
	 * Registers method injection point.
	 */
	public void registerMethodInjectionPoint(String beanName, String methodName, Class[] arguments, String[] references) {
		registerPetiteMethodInjectionPoint(beanName, methodName, arguments, references);
	}

	/**
	 * Single point of method injection point registration.
	 */
	protected void registerPetiteMethodInjectionPoint(String beanName, String methodName, Class[] arguments, String[] references) {
		BeanDefinition beanDefinition = lookupExistingBeanDefinition(beanName);
		MethodInjectionPoint mip = petiteManager.defineMethodInjectionPoint(beanDefinition.type, methodName, arguments, references);
		beanDefinition.addMethodInjectionPoint(mip);
	}


	// ---------------------------------------------------------------- initialization methods

	/**
	 * Manually registers init methods.
	 */
	public void registerInitMethods(String beanName, String... methodNames) {
		registerPetiteInitMethods(beanName, null, methodNames);
	}
	/**
	 * Manually registers init methods.
	 */
	public void registerInitMethods(String beanName, String[] beforeMethodNames, String[] afterMethodNames) {
		registerPetiteInitMethods(beanName, beforeMethodNames, afterMethodNames);
	}

	/**
	 * Single point of init method registration.
	 */
	protected void registerPetiteInitMethods(String beanName, String[] beforeMethodNames, String[] afterMethodNames) {
		BeanDefinition beanDefinition = lookupExistingBeanDefinition(beanName);
		InitMethodPoint[] methods = petiteManager.defineInitMethods(beanDefinition.type, beforeMethodNames, afterMethodNames);
		beanDefinition.addInitMethodPoints(methods);
	}




	// ---------------------------------------------------------------- remove

	/**
	 * Removes all petite beans of provided type.
	 * @see #removeBean(String)
	 */
	public void removeBean(Class type) {
		// collect bean names
		Set<String> beanNames = new HashSet<String>();
		Iterator<BeanDefinition> it = beansIterator();
		while (it.hasNext()) {
			BeanDefinition def = it.next();
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
	 * Removes bean definition from the container.
	 * @see #removeBean(Class)
	 */
	public void removeBean(String name) {
		petiteManager.removeBean(name);
	}



	// ---------------------------------------------------------------- params

	/**
	 * Defines new parameter. Parameter with same name will be replaced.
	 */
	public void defineParameter(String name, Object value) {
		petiteManager.defineParameter(name, value);
	}

	/**
	 * Defines many parameters at once. 
	 */
	public void defineParameters(Map<Object, Object> properties) {
		for (Map.Entry<Object, Object> entry : properties.entrySet()) {
			defineParameter(entry.getKey().toString(), entry.getValue());
		}
	}

	
	// ---------------------------------------------------------------- stats and misc

	/**
	 * Returns total number of registered beans.
	 */
	public int getTotalBeans() {
		return petiteManager.getTotalBeans();
	}

	/**
	 * Returns total number of used scopes.
	 */
	public int getTotalScopes() {
		return petiteManager.getTotalScopes();
	}

	/**
	 * Returns iterator over all bean definitions.
	 */
	public Iterator<BeanDefinition> beansIterator() {
		return petiteManager.beansIterator();
	}

}
