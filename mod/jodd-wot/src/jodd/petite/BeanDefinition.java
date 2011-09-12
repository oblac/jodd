// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.petite.scope.Scope;
import jodd.util.ArraysUtil;

/**
 * Petite bean definition, for internal use only.
 */
public class BeanDefinition {

	public BeanDefinition(String name, Class type, Scope scope, WiringMode wiringMode) {
		this.name = name;
		this.type = type;
		this.scope = scope;
		this.wiringMode = wiringMode;
	}

	// finals
	public final String name;			// bean name
	public final Class type;			// bean type
	protected final Scope scope;  		// bean scope
	public final WiringMode wiringMode;	// wiring mode

	// cache
	public CtorInjectionPoint ctor;
	public PropertyInjectionPoint[] properties;
	public CollectionInjectionPoint[] collections;
	public MethodInjectionPoint[] methods;
	public InitMethodPoint[] initMethods;
	public String[] params;

	// ---------------------------------------------------------------- scope delegates

	/**
	 * Delegates to {@link jodd.petite.scope.Scope#lookup(String)}. 
	 */
	public Object scopeLookup() {
		return scope.lookup(name);
	}

	/**
	 * Delegates to {@link jodd.petite.scope.Scope#register(String, Object)}.
	 */
	public void scopeRegister(Object object) {
		scope.register(name, object);
	}

	/**
	 * Delegates to {@link jodd.petite.scope.Scope#remove(String)}. 
	 */
	public void scopeRemove() {
		scope.remove(name);
	}


	// ---------------------------------------------------------------- appends

	/**
	 * Adds property injection point.
	 */
	public void addPropertyInjectionPoint(PropertyInjectionPoint pip) {
		if (properties == null) {
			properties = new PropertyInjectionPoint[1];
			properties[0] = pip;
		} else {
			properties = ArraysUtil.append(properties, pip);
		}
	}

	/**
	 * Adds method injection point.
	 */
	public void addMethodInjectionPoint(MethodInjectionPoint mip) {
		if (methods == null) {
			methods = new MethodInjectionPoint[1];
			methods[0] = mip;
		} else {
			methods = ArraysUtil.append(methods, mip);
		}
	}

	/**
	 * Adds init methods.
	 */
	public void addInitMethodPoints(InitMethodPoint[] methods) {
		if (initMethods == null) {
			initMethods = methods;
		} else {
			initMethods = ArraysUtil.join(initMethods, methods);
		}
	}

	// ---------------------------------------------------------------- toString

	@Override
	public String toString() {
		return "BeanDefinition{" +
				"name='" + name + '\'' +
				", type=" + type +
				", scope=" + scope +
				'}';
	}
}
