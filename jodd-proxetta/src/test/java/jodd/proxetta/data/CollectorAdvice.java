// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.data;

import jodd.proxetta.ProxyAdvice;
import jodd.proxetta.ProxyTarget;

import java.util.ArrayList;
import java.util.List;

public class CollectorAdvice implements ProxyAdvice {

	protected List<String> methods = new ArrayList<String>();

	public Object execute() throws Exception {
		addMethod(ProxyTarget.targetMethodName());
		return ProxyTarget.invoke();
	}

	private void addMethod(String methodName) {
		methods.add(methodName);
	}
}
