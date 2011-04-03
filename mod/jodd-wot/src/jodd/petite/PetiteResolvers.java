// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.petite.resolver.CtorResolver;
import jodd.petite.resolver.InitMethodResolver;
import jodd.petite.resolver.MethodResolver;
import jodd.petite.resolver.ParamResolver;
import jodd.petite.resolver.PropertyResolver;

/**
 * Simply holds all resolvers instances.
 */
public class PetiteResolvers {

	protected CtorResolver ctorResolver;
	protected PropertyResolver propertyResolver;
	protected MethodResolver methodResolver;
	protected InitMethodResolver initMethodResolver;
	protected ParamResolver paramResolver;

	public PetiteResolvers() {
		ctorResolver = new CtorResolver();
		propertyResolver = new PropertyResolver();
		methodResolver = new MethodResolver();
		initMethodResolver = new InitMethodResolver();
		paramResolver = new ParamResolver();
	}

	// ---------------------------------------------------------------- access

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
