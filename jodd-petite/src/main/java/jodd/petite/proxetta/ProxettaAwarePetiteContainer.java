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

package jodd.petite.proxetta;

import jodd.petite.BeanDefinition;
import jodd.petite.PetiteConfig;
import jodd.petite.PetiteContainer;
import jodd.petite.WiringMode;
import jodd.petite.scope.Scope;
import jodd.proxetta.Proxetta;
import jodd.proxetta.ProxettaFactory;
import jodd.proxetta.ProxyAspect;

import java.util.function.Consumer;

/**
 * Proxetta-aware Petite container that applies proxies on bean registration.
 */
public class ProxettaAwarePetiteContainer extends PetiteContainer {

	protected final Proxetta<?, ProxyAspect> proxetta;

	public ProxettaAwarePetiteContainer(final Proxetta<?, ProxyAspect> proxetta) {
		this.proxetta = proxetta;
	}
	public ProxettaAwarePetiteContainer(final Proxetta<?, ProxyAspect> proxetta, final PetiteConfig petiteConfig) {
		super(petiteConfig);
		this.proxetta = proxetta;
	}

	/**
	 * Applies proxetta on bean class before bean registration.
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected <T> BeanDefinition<T> createBeanDefinitionForRegistration(
			final String name,
			Class<T> type,
			final Scope scope,
			final WiringMode wiringMode,
			final Consumer<T> consumer)
	{
		if (proxetta != null) {
			final Class originalType = type;

			final ProxettaFactory builder = proxetta.proxy();

			builder.setTarget(type);

			type = builder.define();

			return new ProxettaBeanDefinition(
				name,
				type,
				scope,
				wiringMode,
				originalType,
				proxetta.getAspects(new ProxyAspect[0]),
				consumer);
		}

		return super.createBeanDefinitionForRegistration(name, type, scope, wiringMode, consumer);
	}


}