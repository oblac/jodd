// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

/**
 * Provider interface.
 */
public interface Provider<T> {

	/**
	 * Provides instance for use. Provider implementation
	 * chooses if it has to create new instance every time.
	 */
	T get();
}
