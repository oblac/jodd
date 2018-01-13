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

import jodd.petite.BeanDefinition;

/**
 * Prototype scope doesn't pool any beans, so each time bean is requested,
 * a new instance will be created. Prototype scope does not call
 * destroy methods.
 */
public class ProtoScope implements Scope {

	/**
	 * Returns <code>null</code> as no bean instance is stored.
	 */
	@Override
	public Object lookup(final String name) {
		return null;
	}

	/**
	 * Does nothing, as bean instances are not stored.
	 */
	@Override
	public void register(final BeanDefinition beanDefinition, final Object bean) {
	}

	/**
	 * Does nothing.
	 */
	@Override
	public void remove(final String name) {
	}

	/**
	 * Allows all scopes to be injected into prototype scoped beans.
	 */
	@Override
	public boolean accept(final Scope referenceScope) {
		return true;
	}

	/**
	 * Does nothing.
	 */
	@Override
	public void shutdown() {
	}

}