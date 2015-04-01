// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.pathref;

import jodd.proxetta.ProxyAspect;
import jodd.proxetta.impl.ProxyProxetta;
import jodd.proxetta.impl.ProxyProxettaBuilder;
import jodd.proxetta.pointcuts.AllMethodsPointcut;

/**
 * Methref Proxetta builder and holder and facade.
 */
public class PathrefProxetta {

	protected final ProxyProxetta proxetta;

	public static final String PATHREF_CLASSNAME_SUFFIX = "$Pathref";

	public PathrefProxetta() {
		ProxyAspect aspects = new ProxyAspect(PathrefAdvice.class, new AllMethodsPointcut());

		proxetta = ProxyProxetta.withAspects(aspects);

		proxetta.setClassNameSuffix(PATHREF_CLASSNAME_SUFFIX);
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