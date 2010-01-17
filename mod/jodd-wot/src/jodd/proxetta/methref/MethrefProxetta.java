// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.methref;

import jodd.io.FileUtil;
import jodd.proxetta.MethodInfo;
import jodd.proxetta.Proxetta;
import jodd.proxetta.ProxyAspect;
import jodd.proxetta.pointcuts.AllMethodsPointcut;

import java.io.IOException;

/**
 * Methref Proxetta builder and holder and facade.
 */
public class MethrefProxetta {

	protected final Proxetta proxetta;

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
		proxetta = Proxetta.withAspects(aspectAll, aspectStr);
	}

	/**
	 * Simply delegates to {@link jodd.proxetta.Proxetta#defineProxy(Class)}.
	 */
	public Class defineProxy(Class target) {
		byte[] klazz = proxetta.createProxy(target);
		try {
			FileUtil.writeBytes("d:\\temp\\temp\\X.class", klazz);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return proxetta.defineProxy(target);
	}
}
