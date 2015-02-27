// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.inv;

import jodd.paramo.MethodParameter;
import jodd.paramo.Paramo;
import jodd.proxetta.ProxyTarget;
import jodd.proxetta.ProxyTargetInfo;

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

	public String ccc(int Welcome, String to, long Jodd, Object Framework) {
		ProxyTargetInfo pti = ProxyTarget.info();

		return doit(pti);
	}

	protected String doit(ProxyTargetInfo pti) {
		return ">" + pti.argumentCount +':' + pti.returnType.getSimpleName() +
				':' + pti.argumentsClasses.length + pti.argumentsClasses[2].getSimpleName() +
				':' + pti.arguments.length + pti.arguments[1] +
				':' + pti.targetMethodName +
				':' + pti.targetClass.getSimpleName();
	}

}