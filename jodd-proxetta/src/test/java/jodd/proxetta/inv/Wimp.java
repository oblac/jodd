// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.inv;

import jodd.paramo.MethodParameter;
import jodd.paramo.Paramo;
import jodd.proxetta.ProxyTarget;

import java.lang.reflect.Method;

public class Wimp {

	public int foo() {
		Object[] arguments = ProxyTarget.createArgumentsArray();
		return arguments.length;
	}

	public String aaa(int Welcome, String To, Object Jodd) {
		String methodName = ProxyTarget.targetMethodName();
		Class[] argTypes = ProxyTarget.createArgumentsClassArray();
		Class targetClass = ProxyTarget.targetClass();

		Method m = null;
		try {
			m = targetClass.getDeclaredMethod(methodName, argTypes);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}

		MethodParameter[] methodParameters = Paramo.resolveParameters(m);

		Class c = ProxyTarget.argumentType(1);
		Object val = ProxyTarget.argument(1);

		return c.getName() + val.toString() +
				methodParameters[0].getName() + methodParameters[1].getName() + methodParameters[2].getName();
	}

}