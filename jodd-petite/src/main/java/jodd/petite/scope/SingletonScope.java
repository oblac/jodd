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

package jodd.petite.scope;

import jodd.petite.BeanData;
import jodd.petite.BeanDefinition;
import jodd.petite.PetiteUtil;

import java.util.Map;
import java.util.HashMap;

/**
 * Singleton scope pools all bean instances so they will be created only once in
 * the container context.
 */
public class SingletonScope implements Scope {

	protected Map<String, BeanData> instances = new HashMap<>();

	public Object lookup(String name) {
		BeanData beanData = instances.get(name);
		if (beanData == null) {
			return null;
		}
		return beanData.getBean();
	}

	public void register(BeanDefinition beanDefinition, Object bean) {
		BeanData beanData = new BeanData(beanDefinition, bean);
		instances.put(beanDefinition.getName(), beanData);
	}

	public void remove(String name) {
		instances.remove(name);
	}

	/**
	 * Allows only singleton scoped beans to be injected into the target singleton bean.
	 */
	public boolean accept(Scope referenceScope) {
		return (referenceScope.getClass() == SingletonScope.class);
	}

	/**
	 * Iterate all beans and invokes registered destroy methods.
	 */
	public void shutdown() {
		for (BeanData beanData : instances.values()) {
			PetiteUtil.callDestroyMethods(beanData);
		}
		instances.clear();
	}
}
