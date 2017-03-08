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

package jodd.petite;

import jodd.proxetta.ProxyAdvice;
import jodd.proxetta.ProxyTarget;

import java.lang.reflect.Method;

/**
 * Scoped proxy advice. For each wrapped method,
 * it will lookup for the bean from the container
 * and will delegate the method call to it.
 */
public class ScopedProxyAdvice implements ProxyAdvice {

	/**
	 * Petite container.
	 */
	public PetiteContainer petiteContainer;
	/**
	 * Bean name for lookup.
	 */
	public String name;

	public Object execute() throws Exception {
		Object target = petiteContainer.getBean(name);

		// collect data about target method call

		String methodName = ProxyTarget.targetMethodName();

		Class[] methodArgumentTypes = ProxyTarget.createArgumentsClassArray();

		Object[] methodArguments = ProxyTarget.createArgumentsArray();

		// delegate method call to target

		Method targetMethod = target.getClass().getMethod(methodName, methodArgumentTypes);

		Object result = targetMethod.invoke(target, methodArguments);

		// return target result

		return ProxyTarget.returnValue(result);
	}
}
