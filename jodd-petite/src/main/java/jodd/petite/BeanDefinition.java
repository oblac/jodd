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

import jodd.petite.scope.Scope;
import jodd.util.ArraysUtil;

/**
 * Petite bean definition and cache. Consist of bean data that defines a bean
 * and cache, that might not be initialized (if <code>null</code>).
 * To initialize cache, get the bean instance from container.
 */
public class BeanDefinition {

	public BeanDefinition(String name, Class type, Scope scope, WiringMode wiringMode) {
		this.name = name;
		this.type = type;
		this.scope = scope;
		this.wiringMode = wiringMode;
	}

	// finals
	protected final String name;		// bean name
	protected final Class type;			// bean type
	protected final Scope scope;  		// bean scope, may be null for beans that are not stored in scope but just wired
	protected final WiringMode wiringMode;	// wiring mode

	// cache
	protected CtorInjectionPoint ctor;
	protected PropertyInjectionPoint[] properties;
	protected SetInjectionPoint[] sets;
	protected MethodInjectionPoint[] methods;
	protected InitMethodPoint[] initMethods;
	protected DestroyMethodPoint[] destroyMethods;
	protected String[] params;

	// ---------------------------------------------------------------- definition getters

	/**
	 * Returns bean name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns bean type.
	 */
	public Class getType() {
		return type;
	}

	/**
	 * Returns beans scope type.
	 */
	public Class<? extends Scope> getScope() {
		if (scope == null) {
			return null;
		}
		return scope.getClass();
	}

	/**
	 * Returns wiring mode.
	 */
	public WiringMode getWiringMode() {
		return wiringMode;
	}

	// ---------------------------------------------------------------- cache getters

	/**
	 * Returns constructor injection point.
	 */
	public CtorInjectionPoint getCtorInjectionPoint() {
		return ctor;
	}

	/**
	 * Returns property injection points.
	 */
	public PropertyInjectionPoint[] getPropertyInjectionPoints() {
		return properties;
	}

	/**
	 * Returns set injection points.
	 */
	public SetInjectionPoint[] getSetInjectionPoints() {
		return sets;
	}

	/**
	 * Returns method injection points.
	 */
	public MethodInjectionPoint[] getMethodInjectionPoints() {
		return methods;
	}

	/**
	 * Returns init method points.
	 */
	public InitMethodPoint[] getInitMethodPoints() {
		return initMethods;
	}

	/**
	 * Returns destroy method points.
	 */
	public DestroyMethodPoint[] getDestroyMethodPoints() {
		return destroyMethods;
	}

	/**
	 * Returns parameters.
	 */
	public String[] getParams() {
		return params;
	}

	// ---------------------------------------------------------------- scope delegates

	/**
	 * Delegates to {@link jodd.petite.scope.Scope#lookup(String)}. 
	 */
	protected Object scopeLookup() {
		if (scope == null) {
			throw new PetiteException("Scope not defined");
		}
		return scope.lookup(name);
	}

	/**
	 * Delegates to {@link jodd.petite.scope.Scope#register(jodd.petite.BeanDefinition, Object)}.
	 */
	protected void scopeRegister(Object object) {
		if (scope != null) {
			scope.register(this, object);
		}
	}

	/**
	 * Delegates to {@link jodd.petite.scope.Scope#remove(String)}. 
	 */
	protected void scopeRemove() {
		scope.remove(name);
	}

	// ---------------------------------------------------------------- appends

	/**
	 * Adds property injection point.
	 */
	protected void addPropertyInjectionPoint(PropertyInjectionPoint pip) {
		if (properties == null) {
			properties = new PropertyInjectionPoint[1];
			properties[0] = pip;
		} else {
			properties = ArraysUtil.append(properties, pip);
		}
	}

	/**
	 * Adds set injection point.
	 */
	protected void addSetInjectionPoint(SetInjectionPoint sip) {
		if (sets == null) {
			sets = new SetInjectionPoint[1];
			sets[0] = sip;
		} else {
			sets = ArraysUtil.append(sets, sip);
		}
	}

	/**
	 * Adds method injection point.
	 */
	protected void addMethodInjectionPoint(MethodInjectionPoint mip) {
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
	protected void addInitMethodPoints(InitMethodPoint[] methods) {
		if (initMethods == null) {
			initMethods = methods;
		} else {
			initMethods = ArraysUtil.join(initMethods, methods);
		}
	}

	/**
	 * Adds destroy methods.
	 */
	protected void addDestroyMethodPoints(DestroyMethodPoint[] methods) {
		if (destroyMethods == null) {
			destroyMethods = methods;
		} else {
			destroyMethods = ArraysUtil.join(destroyMethods, methods);
		}
	}

	// ---------------------------------------------------------------- toString

	@Override
	public String toString() {
		return "BeanDefinition{" +
				"name='" + name + '\'' +
				", type=" + type +
				", scope=" + scope +
				", wiring= " + wiringMode +
				'}';
	}
}
