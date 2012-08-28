// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.methref;

import jodd.proxetta.MethodInfo;
import jodd.proxetta.ProxyAspect;
import jodd.proxetta.asm.ProxettaProxyCreator;
import jodd.proxetta.impl.ProxyProxetta;
import jodd.proxetta.pointcuts.AllMethodsPointcut;

/**
 * Methref Proxetta builder and holder and facade.
 */
public class MethrefProxetta {

	protected final ProxyProxetta proxetta;

	public static final String METHREF_CLASSNAME_SUFFIX = "$Methref";

	public MethrefProxetta() {
		ProxyAspect aspectAll = new ProxyAspect(MethrefAdvice.class, new AllMethodsPointcut() {
			@Override
			public boolean apply(MethodInfo methodInfo) {
				if (methodInfo.getReturnType().equals(String.class.getName())) {
					return false;
				}
				return super.apply(methodInfo);
			}
		});
		ProxyAspect aspectStr = new ProxyAspect(MethrefStringAdvice.class, new AllMethodsPointcut() {
			@Override
			public boolean apply(MethodInfo methodInfo) {
				if (methodInfo.getReturnType().equals(String.class.getName()) == false) {
					return false;
				}
				return super.apply(methodInfo);
			}
		});

		proxetta = ProxyProxetta.withAspects(aspectAll, aspectStr);
		proxetta.setClassNameSuffix(METHREF_CLASSNAME_SUFFIX);
	}

	/**
	 * Simply delegates to {@link jodd.proxetta.Proxetta#defineProxy(Class)}.
	 */
	public Class defineProxy(Class target) {
		ProxettaProxyCreator ppc =  proxetta.builder();
		ppc.setTarget(target);
		return ppc.define();
	}
}
