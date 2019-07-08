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
import jodd.petite.PetiteContainer;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton scope pools all bean instances so they will be created only once in
 * the container context.
 */
public class SingletonScope implements Scope {

	private final PetiteContainer pc;

	public SingletonScope(final PetiteContainer pc) {
		this.pc = pc;
	}


	protected Map<String, BeanData> instances = new HashMap<>();

	@Override
	public Object lookup(final String name) {
		BeanData beanData = instances.get(name);
		if (beanData == null) {
			return null;
		}
		return beanData.bean();
	}

	@Override
	public void register(final BeanDefinition beanDefinition, final Object bean) {
		instances.put(beanDefinition.name(), new BeanData(pc, beanDefinition, bean));
	}

	@Override
	public void remove(final String name) {
		instances.remove(name);
	}

	/**
	 * Allows only singleton scoped beans to be injected into the target singleton bean.
	 */
	@Override
	public boolean accept(final Scope referenceScope) {
		Class<? extends Scope> refScopeType = referenceScope.getClass();

		if (refScopeType == ProtoScope.class) {
			return true;
		}

		if (refScopeType == SingletonScope.class) {
			return true;
		}

		return false;
	}

	/**
	 * Iterate all beans and invokes registered destroy methods.
	 */
	@Override
	public void shutdown() {
		for (final BeanData beanData : instances.values()) {
			beanData.callDestroyMethods();
		}
		instances.clear();
	}
}
