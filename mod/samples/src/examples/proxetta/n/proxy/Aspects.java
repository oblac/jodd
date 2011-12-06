// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package examples.proxetta.n.proxy;

import jodd.asm.AsmConst;
import jodd.proxetta.MethodInfo;
import jodd.proxetta.ProxyAspect;
import jodd.proxetta.ProxyPointcut;
import examples.proxetta.n.proxy.advice.TxAdvice;
import examples.proxetta.n.proxy.advice.JoAdvice;

/**
 * Proxy definitions holder.
 */
public class Aspects {

	private final ProxyAspect txProxy;
	private final ProxyAspect joProxy;

	public Aspects() {
		txProxy = createTxAspect();
		joProxy = createJoAspect();
	}

	private ProxyAspect createTxAspect() {
		return new ProxyAspect(TxAdvice.class,
				new ProxyPointcut() {
					public boolean apply(MethodInfo msign) {
						return msign.getClassname().endsWith("Service") && msign.getMethodName().startsWith("z");
					}
				});
	}

	private ProxyAspect createJoAspect() {
		return new ProxyAspect(JoAdvice.class,
				new ProxyPointcut() {
					public boolean apply(MethodInfo msign) {
						System.out.println("$$$$$$$$$$ " + msign);
						System.out.println("rrrrrrrrrr " + (msign.getReturnOpcodeType() == AsmConst.TYPE_VOID));
						return msign.getClassname().endsWith("Service");
					}
				});
	}


	public ProxyAspect getTxAspect() {
		return txProxy;
	}
	public ProxyAspect getJoAspect() {
		return joProxy;
	}
}
