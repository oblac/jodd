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
package jodd.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Base aspect class that holds the target instance.
 */
public abstract class Aspect implements InvocationHandler {

	private Object target;

	public Aspect(Object target) {
		this.target = target;
	}

	/**
	 * Returns target object.
	 */
	public final Object getTarget() {
		return this.target;
	}

	/**
	 * Runs before targets method. Returns {@code true} if target method
	 * should run.
	 */
	public abstract boolean before(Object target, Method method, Object[] args);

	/**
	 * Runs after targets method. Returns {@code true} if aspect method should
	 * return value, otherwise {@code null}.
	 */
	public abstract boolean after(Object target, Method method, Object[] args);

	/**
	 * Invoked after exception.
	 */
	public abstract boolean afterException(Object target, Method method, Object[] args, Throwable throwable);

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object result = null;

		if (before(target, method, args)) {
			try {
				result = method.invoke(target, args);
			}
			catch (InvocationTargetException e) {
				afterException(args, method, args, e.getTargetException());
			}
			catch (Exception ex) {
				throw ex;
			}
		}
		if (after(target, method, args)) {
			return result;
		}
		return null;
	}

}