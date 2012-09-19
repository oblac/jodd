// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.scope;

/**
 * Petite container bean scope. Scopes actually represents wrapper over none, one or many internal
 * bean pools. Which pool is used depends on scopes behaviour and external data.
 * <p>
 * Scopes are instantiated once on their first usage and stored within one container.
 */
public interface Scope {

	/**
	 * Lookups for bean name. It may happens that lookup is performed
	 * <b>before</b> the {@link #register(String, Object) registration},
	 * therefore it should returns <code>null</code> if object is not
	 * yet registered.
	 */
	Object lookup(String name);

	/**
	 * Registers the bean within the current scope.
	 */
	void register(String name, Object bean);

	/**
	 * Removes the bean from the scope entirely.
	 */
	void remove(String name);

}
