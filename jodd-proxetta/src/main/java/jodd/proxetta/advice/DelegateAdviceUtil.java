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

import jodd.proxetta.MethodInfo;
import jodd.proxetta.ProxettaException;
import jodd.proxetta.ProxyAspect;
import jodd.proxetta.impl.ProxyProxetta;
import jodd.proxetta.pointcuts.ProxyPointcutSupport;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Utilities for applying {@link DelegateAdvice} to target.
 */
public class DelegateAdviceUtil {

	private static HashMap<Class, Class> advicesMap = new HashMap<>();

	/**
	 * Proxy Proxetta, applied on all public methods of the target class.
	 */
	private static final ProxyProxetta PROXY_PROXETTA =
			ProxyProxetta.withAspects(
				new ProxyAspect(DelegateAdvice.class, new ProxyPointcutSupport() {
					public boolean apply(MethodInfo methodInfo) {
						return methodInfo.isPublicMethod();
					}
				}));

	/**
	 * Applies advice on given target class and returns proxy instance.
	 */
	public static <T> T applyAdvice(Class<T> targetClass) {
		Class adviceClass = advicesMap.get(targetClass);

		if (adviceClass == null) {
			// advice not yet created

			adviceClass = PROXY_PROXETTA.builder(targetClass).define();

			advicesMap.put(targetClass, adviceClass);
		}

		// create new advice instance and injects target instance to it

		try {
			Object advice = adviceClass.newInstance();

			Field field = adviceClass.getField("$___target$0");

			field.set(advice, targetClass);

			return (T) advice;
		} catch (Exception ex) {
			throw new ProxettaException(ex);
		}
	}

	/**
	 * Injects target into proxy.
	 */
	public static void injectTargetIntoProxy(Object proxy, Object target) {
		Class proxyClass = proxy.getClass();

		try {
			Field field = proxyClass.getField("$___target$0");

			field.set(proxy, target);
		} catch (Exception ex) {
			throw new ProxettaException(ex);
		}
	}
}