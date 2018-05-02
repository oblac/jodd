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

package jodd.madvoc.component;

import jodd.madvoc.MadvocException;
import jodd.madvoc.scope.MadvocScope;
import jodd.madvoc.scope.RequestScope;
import jodd.petite.PetiteContainer;
import jodd.petite.meta.PetiteInject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Scope resolver maintains scope instances and provides lookups.
 */
public class ScopeResolver {

	@PetiteInject
	protected PetiteContainer madpc;

	protected List<MadvocScope> allScopes = new ArrayList<>();

	/**
	 * Lookups the scope instance of given scope annotation.
	 * If instance does not exist, it will be created, cached and returned.
	 */
	@SuppressWarnings("unchecked")
	public <S extends MadvocScope> S defaultOrScopeType(final Class<S> scopeClass) {
		if (scopeClass == null) {
			return (S) getOrInitScope(RequestScope.class);
		}

		return (S) getOrInitScope(scopeClass);
	}

	/**
	 * Performs search for the scope class and returns it's instance.
	 */
	protected MadvocScope getOrInitScope(final Class<? extends MadvocScope> madvocScopeType) {
		for (final MadvocScope s : allScopes) {
			if (s.getClass().equals(madvocScopeType)) {
				return s;
			}
		}

		// new scope detected
		final MadvocScope newScope;
		try {
			newScope = madpc.createBean(madvocScopeType);
		} catch (Exception ex) {
			throw new MadvocException("Unable to create scope: " + madvocScopeType, ex);
		}

		allScopes.add(newScope);

		return newScope;
	}

	// ---------------------------------------------------------------- iteration

	/**
	 * Iterates over all scope instances.
	 */
	public void forEachScope(final Consumer<MadvocScope> madvocScopeConsumer) {
		allScopes.forEach(madvocScopeConsumer);
	}

	/**
	 * Finds a given scope and consumes it.
	 */
	public void forScope(final Class<? extends MadvocScope> scopeType, final Consumer<MadvocScope> madvocScopeConsumer) {
		final MadvocScope scope = getOrInitScope(scopeType);
		madvocScopeConsumer.accept(scope);
	}

}
