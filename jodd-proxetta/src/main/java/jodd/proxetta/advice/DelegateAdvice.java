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

package jodd.proxetta.advice;

import jodd.proxetta.ProxyAdvice;
import jodd.proxetta.ProxyTarget;

import java.lang.reflect.Method;

/**
 * Delegates calls to target using reflection. Offers
 * separation between between points where class is loaded
 * and class is used.
 * <p>
 * Allows usage of classes loaded by different class loader.
 * For example, if you have an instance of class loaded by
 * <b>parent-last</b> class loader, delegate allows
 * to still call it using plain java. Under the hood,
 * each method will actually invoke target method
 * using reflection.
 */
public class DelegateAdvice implements ProxyAdvice {

	/**
	 * Target object.
	 */
	public Object _target;

	/**
	 * Looks up for method in target object and invokes it using reflection.
	 */
	public Object execute() throws Exception {
		String methodName = ProxyTarget.targetMethodName();
		Class[] argTypes = ProxyTarget.createArgumentsClassArray();
		Object[] args = ProxyTarget.createArgumentsArray();

		// lookup method on target object class (and not #targetClass!()
		Class type = _target.getClass();
		Method method = type.getMethod(methodName, argTypes);

		// remember context classloader
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

		Object result;
		try {
			// change class loader
			Thread.currentThread().setContextClassLoader(type.getClassLoader());

			// invoke
			result = method.invoke(_target, args);
		}
		finally {
			// return context classloader
			Thread.currentThread().setContextClassLoader(contextClassLoader);

		}

		return ProxyTarget.returnValue(result);
	}
}