// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.proxetta;

import jodd.petite.BeanDefinition;
import jodd.petite.PetiteContainer;
import jodd.petite.WiringMode;
import jodd.petite.scope.Scope;
import jodd.proxetta.impl.ProxyProxettaBuilder;
import jodd.proxetta.impl.ProxyProxetta;

/**
 * Proxetta-aware Petite container that applies proxies on bean registration.
 */
public class ProxettaAwarePetiteContainer extends PetiteContainer {

	protected final ProxyProxetta proxetta;

	public ProxettaAwarePetiteContainer() {
		this(null);
	}
	public ProxettaAwarePetiteContainer(ProxyProxetta proxetta) {
		this.proxetta = proxetta;
	}

	/**
	 * Applies proxetta on bean class before bean registration.
	 */
	@Override
	protected BeanDefinition createBeanDefinitionForRegistration(String name, Class type, Scope scope, WiringMode wiringMode) {
		if (proxetta != null) {
			ProxyProxettaBuilder builder = proxetta.builder();

			builder.setTarget(type);

			type = builder.define();
		}

		return super.createBeanDefinitionForRegistration(name, type, scope, wiringMode);
	}
}