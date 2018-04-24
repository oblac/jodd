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
import jodd.madvoc.meta.Scope;
import jodd.madvoc.scope.ApplicationScope;
import jodd.madvoc.scope.BodyScope;
import jodd.madvoc.scope.CookieScope;
import jodd.madvoc.scope.MadvocContextScope;
import jodd.madvoc.scope.MadvocScope;
import jodd.madvoc.scope.RequestScope;
import jodd.madvoc.scope.ServletContextScope;
import jodd.madvoc.scope.SessionScope;
import jodd.petite.PetiteContainer;
import jodd.petite.meta.PetiteInject;
import jodd.util.ClassLoaderUtil;
import jodd.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Scope resolver maintains scope instances and provides lookups.
 */
public class ScopeResolver {

	public static final String SCOPE_SUFFIX = "Scope";
	@PetiteInject
	protected PetiteContainer madpc;

	protected List<MadvocScope> allScopes = new ArrayList<>();
	protected Map<String, Class<? extends MadvocScope>> scopeNames = new HashMap<>();

	public ScopeResolver() {
		Arrays.stream(new Class[]{
			ApplicationScope.class,
			BodyScope.class,
			CookieScope.class,
			RequestScope.class,
			MadvocContextScope.class,
			RequestScope.class,
			ServletContextScope.class,
			SessionScope.class,
		})
			.forEach(this::registerScope);
	}

	/**
	 * Lookups the scope instance of given scope annotation.
	 * If instance does not exist, it will be created, cached and returned.
	 * @see #defaultOrScopeType(String)
	 */
	public MadvocScope defaultOrScopeType(final Scope scope) {
		if (scope == null) {
			return getOrInitScope(RequestScope.class);
		}
		return defaultOrScopeType(scope.value());
	}

	/**
	 * Lookups the scope instance for given scope name.
	 * @see #defaultOrScopeType(Scope)
	 */
	public MadvocScope defaultOrScopeType(final String scope) {
		Class<? extends MadvocScope> scopeClass = scopeNames.get(scope);

		if (scopeClass == null) {
			// scope not found, try to find it
			try {
				scopeClass = ClassLoaderUtil.loadClass(scope);
			} catch (ClassNotFoundException ignore) {
				throw new MadvocException("Unknown scope: " + scope);
			}
		}

		return getOrInitScope(scopeClass);
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

	// ---------------------------------------------------------------- registration

	/**
	 * Registers new scope type. It will be registered under following names:
	 * <ul>
	 *     <li>full class name (w/o 'Scope' suffix)</li>
	 *     <li>short class name (w/o 'Scope' suffix)</li>
	 * </ul>
	 */
	public void registerScope(final Class<? extends MadvocScope> scopeType) {
		String name = scopeType.getSimpleName();
		if (name.endsWith(SCOPE_SUFFIX)) {
			name = StringUtil.cutSuffix(name, SCOPE_SUFFIX);
		}
		registerScope(name, scopeType);

		name = scopeType.getName();
		if (name.endsWith(SCOPE_SUFFIX)) {
			name = StringUtil.cutSuffix(name, SCOPE_SUFFIX);
		}
		registerScope(name, scopeType);
	}

	/**
	 * Registers scope type for given name, previous scope is removed.
	 */
	public void registerScope(final String name, final Class<? extends MadvocScope> scopeType) {
		Class<? extends MadvocScope> previousType = scopeNames.put(name, scopeType);

		// remove previous scope
		allScopes = allScopes.stream()
			.filter(scope -> !scope.getClass().equals(previousType))
			.collect(Collectors.toList());
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
