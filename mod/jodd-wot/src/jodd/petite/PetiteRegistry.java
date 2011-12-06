// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.petite.scope.Scope;
import jodd.props.Props;

import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;

/**
 * Petite container layer that provides various mods for registering all kind
 * of user stuff.
 */
public abstract class PetiteRegistry extends PetiteBeans {

	protected PetiteRegistry(PetiteConfig petiteConfig) {
		super(petiteConfig);
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


	// ---------------------------------------------------------------- sets

	/**
	 * Registers set injection point.
	 */
	public void registerSetInjectionPoint(String beanName, String property) {
		registerPetiteSetInjectionPoint(beanName, property);
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

	// ---------------------------------------------------------------- remove

	/**
	 * Removes all petite beans of provided type. Type is not resolved for name!
	 * Instead, all beans are iterated and only beans with equal types are removed.
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
	 * Removes bean definition from the container, including all references.
	 * @see #removeBean(Class)
	 */
	public void removeBean(String name) {
		removeBeanDefinition(name);
	}

	// ---------------------------------------------------------------- params

	/**
	 * Defines many parameters at once. 
	 */
	public void defineParameters(Map<Object, Object> properties) {
		for (Map.Entry<Object, Object> entry : properties.entrySet()) {
			defineParameter(entry.getKey().toString(), entry.getValue());
		}
	}

	/**
	 * Defines many parameters at once from {@link Props}.
	 */
	public void defineParameters(Props props) {
		defineParameters(props.extractProperties());
	}

}
