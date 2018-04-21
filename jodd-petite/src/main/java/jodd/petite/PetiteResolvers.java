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

import jodd.petite.def.CtorInjectionPoint;
import jodd.petite.def.DestroyMethodPoint;
import jodd.petite.def.InitMethodPoint;
import jodd.petite.def.MethodInjectionPoint;
import jodd.petite.def.PropertyInjectionPoint;
import jodd.petite.def.ProviderDefinition;
import jodd.petite.def.SetInjectionPoint;
import jodd.petite.resolver.CtorResolver;
import jodd.petite.resolver.DestroyMethodResolver;
import jodd.petite.resolver.InitMethodResolver;
import jodd.petite.resolver.MethodResolver;
import jodd.petite.resolver.PropertyResolver;
import jodd.petite.resolver.ProviderResolver;
import jodd.petite.resolver.ReferencesResolver;
import jodd.petite.resolver.SetResolver;

/**
 * Holds all resolvers instances and offers delegate methods.
 */
public class PetiteResolvers {

	protected final ReferencesResolver referencesResolver;
	protected final CtorResolver ctorResolver;
	protected final PropertyResolver propertyResolver;
	protected final MethodResolver methodResolver;
	protected final SetResolver setResolver;
	protected final InitMethodResolver initMethodResolver;
	protected final DestroyMethodResolver destroyMethodResolver;
	protected final ProviderResolver providerResolver;

	public PetiteResolvers(final ReferencesResolver referencesResolver) {
		this.referencesResolver = referencesResolver;
		this.ctorResolver = new CtorResolver(referencesResolver);
		this.methodResolver = new MethodResolver(referencesResolver);
		this.propertyResolver = new PropertyResolver(referencesResolver);
		this.setResolver = new SetResolver();
		this.initMethodResolver = new InitMethodResolver();
		this.destroyMethodResolver = new DestroyMethodResolver();
		this.providerResolver = new ProviderResolver();
	}

	// ---------------------------------------------------------------- delegates

	/**
	 * Resolves constructor injection point.
	 */
	public CtorInjectionPoint resolveCtorInjectionPoint(final Class type) {
		return ctorResolver.resolve(type, true);
	}

	/**
	 * Resolves property injection points.
	 */
	public PropertyInjectionPoint[] resolvePropertyInjectionPoint(final Class type, final boolean autowire) {
		return propertyResolver.resolve(type, autowire);
	}

	/**
	 * Resolves method injection points.
	 */
	public MethodInjectionPoint[] resolveMethodInjectionPoint(final Class type) {
		return methodResolver.resolve(type);
	}

	/**
	 * Resolves set injection points.
	 */
	public SetInjectionPoint[] resolveSetInjectionPoint(final Class type, final boolean autowire) {
		return setResolver.resolve(type, autowire);
	}

	/**
	 * Resolves init method points.
	 */
	public InitMethodPoint[] resolveInitMethodPoint(final Class type) {
		return initMethodResolver.resolve(type);
	}

	/**
	 * Resolves destroy method points.
	 */
	public DestroyMethodPoint[] resolveDestroyMethodPoint(final Class type) {
		return destroyMethodResolver.resolve(type);
	}

	/**
	 * Resolves provider definition defined in a bean.
	 */
	public ProviderDefinition[] resolveProviderDefinitions(final Class type, final String name) {
		return providerResolver.resolve(type, name);
	}

}