// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.petite.resolver.CtorResolver;
import jodd.petite.resolver.InitMethodResolver;
import jodd.petite.resolver.MethodResolver;
import jodd.petite.resolver.PropertyResolver;
import jodd.petite.resolver.SetResolver;

/**
 * Simply holds all resolvers instances.
 */
public class PetiteResolvers {

	protected CtorResolver ctorResolver;
	protected PropertyResolver propertyResolver;
	protected MethodResolver methodResolver;
	protected SetResolver setResolver;
	protected InitMethodResolver initMethodResolver;

	public PetiteResolvers(InjectionPointFactory injectionPointFactory) {
		ctorResolver = new CtorResolver(injectionPointFactory);
		propertyResolver = new PropertyResolver(injectionPointFactory);
		methodResolver = new MethodResolver(injectionPointFactory);
		setResolver = new SetResolver(injectionPointFactory);
		initMethodResolver = new InitMethodResolver();
	}

	// ---------------------------------------------------------------- delegates

	/**
	 * Resolves constructor injection point.
	 */
	public CtorInjectionPoint resolveCtorInjectionPoint(Class type) {
		return ctorResolver.resolve(type, true);
	}

	/**
	 * Resolves property injection points.
	 */
	public PropertyInjectionPoint[] resolvePropertyInjectionPoint(Class type, boolean autowire) {
		return propertyResolver.resolve(type, autowire);
	}

	/**
	 * Resolves method injection points.
	 */
	public MethodInjectionPoint[] resolveMethodInjectionPoint(Class type) {
		return methodResolver.resolve(type);
	}

	/**
	 * Resolves set injection points.
	 */
	public SetInjectionPoint[] resolveSetInjectionPoint(Class type, boolean autowire) {
		return setResolver.resolve(type, autowire);
	}

	/**
	 * Resolves init method points.
	 */
	public InitMethodPoint[] resolveInitMethodPoint(Object bean) {
		return initMethodResolver.resolve(bean);
	}

}