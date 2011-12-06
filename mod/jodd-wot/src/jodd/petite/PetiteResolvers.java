// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.petite.resolver.CtorResolver;
import jodd.petite.resolver.InitMethodResolver;
import jodd.petite.resolver.MethodResolver;
import jodd.petite.resolver.ParamResolver;
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
	protected ParamResolver paramResolver;

	public PetiteResolvers(InjectionPointFactory injectionPointFactory) {
		ctorResolver = new CtorResolver(injectionPointFactory);
		propertyResolver = new PropertyResolver(injectionPointFactory);
		methodResolver = new MethodResolver(injectionPointFactory);
		setResolver = new SetResolver(injectionPointFactory);
		initMethodResolver = new InitMethodResolver();
		paramResolver = new ParamResolver();
	}

	// ---------------------------------------------------------------- access

	public SetResolver getSetResolver() {
		return setResolver;
	}

	public void setSetResolver(SetResolver setResolver) {
		this.setResolver = setResolver;
	}

	public CtorResolver getCtorResolver() {
		return ctorResolver;
	}

	public void setCtorResolver(CtorResolver ctorResolver) {
		this.ctorResolver = ctorResolver;
	}

	public PropertyResolver getPropertyResolver() {
		return propertyResolver;
	}

	public void setPropertyResolver(PropertyResolver propertyResolver) {
		this.propertyResolver = propertyResolver;
	}

	public MethodResolver getMethodResolver() {
		return methodResolver;
	}

	public void setMethodResolver(MethodResolver methodResolver) {
		this.methodResolver = methodResolver;
	}

	public InitMethodResolver getInitMethodResolver() {
		return initMethodResolver;
	}

	public void setInitMethodResolver(InitMethodResolver initMethodResolver) {
		this.initMethodResolver = initMethodResolver;
	}

	public ParamResolver getParamResolver() {
		return paramResolver;
	}

	public void setParamResolver(ParamResolver paramResolver) {
		this.paramResolver = paramResolver;
	}
}
