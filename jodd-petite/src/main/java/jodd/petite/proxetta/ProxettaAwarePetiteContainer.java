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
	public BeanDefinition registerPetiteBean(Class type, String name, Class<? extends Scope> scopeType, WiringMode wiringMode, boolean define) {
		if (proxetta != null) {
			if (name == null) {
				name = resolveBeanName(type);
			}

			ProxyProxettaBuilder builder = proxetta.builder();
			builder.setTarget(type);

			type = builder.define();
		}
		return super.registerPetiteBean(type, name, scopeType, wiringMode, define);
	}
}
