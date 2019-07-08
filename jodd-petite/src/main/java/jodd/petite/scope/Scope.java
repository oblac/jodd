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
 * Petite container bean scope. Scopes actually represents wrapper over none, one or many internal
 * bean pools. Which pool is used depends on scopes behaviour and external data.
 * <p>
 * Scopes are instantiated once on their first usage and stored within one container.
 */
public interface Scope {

	/**
	 * Lookups for bean name. Returns <code>null</code> if bean is not
	 * found or yet registered.
	 */
	Object lookup(String name);

	/**
	 * Registers the bean within the current scope.
	 * Usually registers it by its name from {@link jodd.petite.BeanDefinition}.
	 * Also it may register destroy methods of a bean within this scope.
	 */
	void register(BeanDefinition beanDefinition, Object bean);

	/**
	 * Removes the bean from the scope entirely. Destroy methods are <b>not</b>
	 * called as it is assumed that bean is destroyed manually.
	 */
	void remove(String name);

	/**
	 * Returns <code>true</code> if a bean of referenced scope can be
	 * injected into target bean of this scope. Otherwise, returns
	 * <code>false</code>, which may be a sign for scoped proxy to be
	 * injected.
	 * <p>
	 * In general, injection of 'shorter' reference scopes
	 * into the 'longer' target scopes should not be accepted.
	 * In other words, if reference scope is 'longer' or equal (same),
	 * this method should return <code>true</code>.
	 * <p>
	 * Helpful is to ask the following question: "May the reference scope
	 * bean be injected into bean of this scope?".
	 */
	boolean accept(Scope referenceScope);

	/**
	 * Shutdowns the scope by removing all beans and calling
	 * destroy methods.
	 */
	void shutdown();

}