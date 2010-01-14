// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

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
	 * {@link JtxTransactionMode#isNotTransactional() propagation behavior}.
	 */
	E beginTransaction(JtxTransactionMode jtxMode);

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
