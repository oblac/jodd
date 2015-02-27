// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.methref;

import jodd.proxetta.ProxyAspect;
import jodd.proxetta.impl.ProxyProxettaBuilder;
import jodd.proxetta.impl.ProxyProxetta;
import jodd.proxetta.pointcuts.AllMethodsPointcut;

/**
 * Methref Proxetta builder and holder and facade.
 */
public class MethrefProxetta {

	protected final ProxyProxetta proxetta;

	public static final String METHREF_CLASSNAME_SUFFIX = "$Methref";

	public MethrefProxetta() {
		ProxyAspect aspects = new ProxyAspect(MethrefAdvice.class, new AllMethodsPointcut());

		proxetta = ProxyProxetta.withAspects(aspects);

		proxetta.setClassNameSuffix(METHREF_CLASSNAME_SUFFIX);
	}

	/**
	 * Generates new class.
	 */
	public Class defineProxy(Class target) {
		ProxyProxettaBuilder builder = proxetta.builder();
		builder.setTarget(target);
		return builder.define();
	}

}