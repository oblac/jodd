// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package examples.proxetta.n.proxy;

import jodd.proxetta.MethodSignature;
import jodd.proxetta.AnnotationData;
import jodd.proxetta.ProxyPointcut;

import java.util.List;

import examples.proxetta.n.proxy.advice.TxAdvice;

public class TxPointcut implements ProxyPointcut {

	public boolean apply(MethodSignature msign) {
		List<AnnotationData> anns = msign.getAnnotations();
		String txProxyName = TxAdvice.class.getName();
		for (AnnotationData a : anns) {
			if (a.declaration.equals(txProxyName)) {
				return true;
			}
		}
		return false;
	}
}
