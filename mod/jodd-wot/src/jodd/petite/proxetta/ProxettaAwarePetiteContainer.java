// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.proxetta;

import jodd.petite.BeanDefinition;
import jodd.petite.PetiteContainer;
import jodd.petite.WiringMode;
import jodd.petite.scope.Scope;
import jodd.proxetta.Proxetta;

/**
 * Proxetta-aware Petite container that applies proxies on bean registration.
 */
public class ProxettaAwarePetiteContainer extends PetiteContainer {

	protected final Proxetta proxetta;

	public ProxettaAwarePetiteContainer() {
		this(null);
	}
	public ProxettaAwarePetiteContainer(Proxetta proxetta) {
		this.proxetta = proxetta;
	}

	/**
	 * Applies proxetta on bean class before bean registration.
	 */
	@Override
	protected BeanDefinition registerPetiteBean(String name, Class type, Class<? extends Scope> scopeType, WiringMode wiringMode) {
		if (proxetta != null) {
			if (name == null) {
				name = resolveBeanName(type);
			}
			type = proxetta.defineProxy(type);
		}
		return super.registerPetiteBean(name, type, scopeType, wiringMode);
	}
}
