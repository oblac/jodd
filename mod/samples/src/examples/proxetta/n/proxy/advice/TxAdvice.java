// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package examples.proxetta.n.proxy.advice;

import jodd.proxetta.ProxyAdvice;
import jodd.proxetta.ProxyTarget;
import static jodd.proxetta.ProxyTarget.targetClass;
import static jodd.proxetta.ProxyTarget.targetMethodName;

public class TxAdvice implements ProxyAdvice {

	public Object execute() throws Exception {
/*
		System.out.println("TX ");
		Class type = targetClass();
		String methodName = targetMethodName();
*/
/*
		try {
			System.out.println("TX start");
			Object result = ProxyTarget.invoke();
			System.out.println("TX end -> " + result);
			return result;
		} catch (Exception ex) {
			System.out.println("------> TX ex " + ex);
			throw ex;
		}
*/
		System.out.println("TX start");
		Object result = ProxyTarget.invoke();
		System.out.println("TX end ->");
		System.out.println("TX end -> " + result);
//		return result;
		return result;
	}

}
