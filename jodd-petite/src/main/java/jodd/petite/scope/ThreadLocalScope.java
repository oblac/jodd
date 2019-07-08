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
 * Thread local Petite bean scope. Holds beans in thread local scopes.
 * Be careful with this scope, if you do not have control on threads!
 * For example, app servers may have a thread pools, so threads may not
 * finish when expected. ThreadLocalScope can not invoke destroy methods.
 */
public class ThreadLocalScope implements Scope {

	private final PetiteContainer pc;

	public ThreadLocalScope(final PetiteContainer pc) {
		this.pc = pc;
	}


	protected static ThreadLocal<Map<String, BeanData>> context = new ThreadLocal<Map<String, BeanData>>() {
		@Override
		protected synchronized Map<String, BeanData> initialValue() {
			return new HashMap<>();
		}
	};

	@Override
	public Object lookup(final String name) {
		Map<String, BeanData> threadLocalMap = context.get();
		BeanData beanData = threadLocalMap.get(name);
		if (beanData == null) {
			return null;
		}
		return beanData.bean();
	}

	@Override
	public void register(final BeanDefinition beanDefinition, final Object bean) {
		Map<String, BeanData> threadLocalMap = context.get();
		threadLocalMap.put(beanDefinition.name(), new BeanData(pc, beanDefinition, bean));
	}

	@Override
	public void remove(final String name) {
		Map<String, BeanData> threadLocalMap = context.get();
		threadLocalMap.remove(name);
	}

	/**
	 * Defines allowed referenced scopes that can be injected into the
	 * thread-local scoped bean.
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

		if (refScopeType == ThreadLocalScope.class) {
			return true;
		}

		return false;
	}

	@Override
	public void shutdown() {
	}

}