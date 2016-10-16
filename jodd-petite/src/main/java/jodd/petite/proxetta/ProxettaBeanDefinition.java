package jodd.petite.proxetta;

import jodd.petite.BeanDefinition;
import jodd.petite.WiringMode;
import jodd.petite.scope.Scope;
import jodd.proxetta.ProxyAspect;

/**
 * Enhanced version of {@link BeanDefinition} that keeps data about original
 * target class and applied proxy aspects.
 */
public class ProxettaBeanDefinition extends BeanDefinition {

	public final ProxyAspect[] proxyAspects;
	public final Class originalTarget;

	public ProxettaBeanDefinition(
			String name, Class type, Scope scope, WiringMode wiringMode, Class originalTarget, ProxyAspect[] proxyAspects) {
		super(name, type, scope, wiringMode);
		this.originalTarget = originalTarget;
		this.proxyAspects = proxyAspects;
	}

}
