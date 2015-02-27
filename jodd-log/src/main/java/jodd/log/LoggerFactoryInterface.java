// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.log;

/**
 * Adapter for various logger implementations.
 */
public interface LoggerFactoryInterface {

	/**
	 * Returns {@link jodd.log.Logger} for given name.
	 */
	Logger getLogger(String name);

}