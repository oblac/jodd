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

package jodd.proxetta;

import java.lang.reflect.Field;

/**
 * Proxetta utilities.
 */
public class ProxettaUtil {

	/**
	 * Returns target class if proxetta applied on given class.
	 * If not, returns given class as result.
	 */
	public static Class getTargetClass(Class proxy) {
		String name = proxy.getName();

		if (name.endsWith(JoddProxetta.proxyClassNameSuffix)) {
			return proxy.getSuperclass();
		}

		if (name.endsWith(JoddProxetta.wrapperClassNameSuffix)) {
			return getTargetWrapperType(proxy);
		}

		return proxy;
	}


	/**
	 * Injects some target instance into {@link jodd.proxetta.impl.WrapperProxetta wrapper} proxy
	 * in given {@link jodd.proxetta.impl.WrapperProxettaBuilder#setTargetFieldName(String) target field name}.
	 */
	public static void injectTargetIntoWrapper(Object target, Object wrapper, String targetFieldName) {
		try {
			Field field = wrapper.getClass().getField(targetFieldName);
			field.setAccessible(true);
			field.set(wrapper, target);
		} catch (Exception ex) {
			throw new ProxettaException(ex);
		}
	}

	/**
	 * Injects target instance into proxy using default target field name.
	 * @see #injectTargetIntoWrapper(Object, Object, String)
	 */
	public static void injectTargetIntoWrapper(Object target, Object wrapper) {
		injectTargetIntoWrapper(target, wrapper, JoddProxetta.wrapperTargetFieldName);
	}

	/**
	 * Returns wrapper target type.
	 */
	public static Class getTargetWrapperType(Class wrapperClass) {
		Field field;
		try {
			field = wrapperClass.getField(JoddProxetta.wrapperTargetFieldName);
		} catch (NoSuchFieldException nsfex) {
			throw new ProxettaException(nsfex);
		}
		return field.getType();
	}

}