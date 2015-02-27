// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

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
	 */
	boolean accept(Scope referenceScope);

	/**
	 * Shutdowns the scope by removing all beans and calling
	 * destroy methods.
	 */
	void shutdown();

}