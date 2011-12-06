// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.jtx;

/**
 * Responsible for managing transactions of the
 * resources of the same type under the control of the {@link JtxTransaction transaction}.
 * Resource manager has to be registered in the {@link JtxTransactionManager transaction manager}.
 */
public interface JtxResourceManager<E> {

	/**
	 * Returns associated resource type.
	 */
	Class<E> getResourceType();

	/**
	 * Creates new resource and begins new transaction if specified so by
	 * active flag, usually determined by propagation behavior.
	 * Propagation behavior and timeout may be handled by the Jtx framework,
	 * leaving resource manager to handle isolation and read only flag.
	 */
	E beginTransaction(JtxTransactionMode jtxMode, boolean active);

	/**
	 * Commits resource and closes it if committing was successful.
	 */
	void commitTransaction(E resource);

	/**
	 * Rollback resource and closes it. Resource is closed no matter if rolling back fails.
	 */
	void rollbackTransaction(E resource);

	/**
	 * Closes manager and free its resources.
	 */
	void close();
}
