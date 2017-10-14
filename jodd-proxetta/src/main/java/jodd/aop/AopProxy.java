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

import jodd.util.ClassUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * Factory for proxies, created using Java own library.
 */
public class AopProxy {

	/**
	 * Creates a proxy of given target and the aspect.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T proxyOf(T target, Class<? extends Aspect> aspectClass) {
		final Aspect aspect;

		try {
			aspect = ClassUtil.newInstance(aspectClass, target);
		}
		catch (Exception e) {
			throw new IllegalArgumentException("Can't create new instance of aspect class", e);
		}

		return (T) newProxyInstance(target.getClass().getClassLoader(), aspect, target.getClass().getInterfaces());
	}

	/**
	 * Creates a proxy from given {@link Aspect}.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T proxyOf(Aspect aspect) {
		final Object target = aspect.getTarget();
		return (T) newProxyInstance(target.getClass().getClassLoader(), aspect, target.getClass().getInterfaces());
	}

	/**
	 * Simple wrapper for javas {@code newProxyInstance}.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newProxyInstance(ClassLoader classloader, InvocationHandler invocationHandler, Class<?>... interfaces) {
		if (interfaces.length == 0) {
			throw new IllegalArgumentException("No interfaces of target class found.");
		}
		return (T) Proxy.newProxyInstance(classloader, interfaces, invocationHandler);
	}

}
