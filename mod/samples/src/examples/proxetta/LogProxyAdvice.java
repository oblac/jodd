// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package examples.proxetta;

import jodd.proxetta.ProxyAdvice;
import jodd.proxetta.ProxyTarget;

import java.lang.reflect.Method;

import examples.proxetta.log.Log;

public class LogProxyAdvice implements ProxyAdvice {

	static int count;

	public static int getCount() {
		return count++;
	}

	public Object execute() {
		int totalArgs = ProxyTarget.argumentsCount();
		Class target = ProxyTarget.targetClass();
		String methodName = ProxyTarget.targetMethodName();
		String methodSignature = ProxyTarget.targetMethodSignature();
		String methodDescription = ProxyTarget.targetMethodDescription();
		Class[] args = ProxyTarget.createArgumentsClassArray();
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + target.getSimpleName() + '#' + methodName + ':' + totalArgs + " ... " + methodSignature + " | " + methodDescription);
		for (Class arg : args) {
			System.out.println("\t\t" + arg.getSimpleName());
		}
		Class returnType = ProxyTarget.returnType();
		System.out.println("\t\t\tr:" + returnType);
		System.out.println("### count:" + getCount());

		// read annotation parameters
		try {
			Method m = target.getMethod(methodName, args);
			Log log = m.getAnnotation(Log.class);
			if (log != null) {
				System.out.println("***" + log.value());
				System.out.println("***" + log.broj());
			}
		} catch (NoSuchMethodException e) {
			System.out.println(e);
		}

/*      Do not use this!
		if (returnType == null) {
			ProxyTarget.invoke();
		} else {
			Object result = ProxyTarget.invokeAndGetResult();
		}
*/

		Object result = ProxyTarget.invoke();
		System.out.println("!!!" + result);


		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<" + target.getSimpleName() + '#' + methodName);
		return result;
	}
}
