// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.proxetta.fixtures.inv;

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