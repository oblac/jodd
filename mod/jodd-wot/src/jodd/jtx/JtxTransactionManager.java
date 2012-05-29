// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.jtx;

import static jodd.jtx.JtxIsolationLevel.*;
import static jodd.jtx.JtxStatus.STATUS_ACTIVE;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;

import jodd.log.Log;

/**
 * {@link JtxTransaction} manager is responsible for handling transaction
 * propagation and resource managers. It also holds various configuration data. .
 */
public class JtxTransactionManager {

	private static final Log log = Log.getLogger(JtxTransactionManager.class);

	protected int maxResourcesPerTransaction;
	protected boolean oneResourceManager;
	protected boolean validateExistingTransaction;
	protected boolean ignoreScope;
	protected Map<Class, JtxResourceManager> resourceManagers;

	protected final ThreadLocal<LinkedList<JtxTransaction>> txStack = new ThreadLocal<LinkedList<JtxTransaction>>() {
		@Override
		protected synchronized LinkedList<JtxTransaction> initialValue() {
			return new LinkedList<JtxTransaction>();
		}
	};

	/**
	 * Creates new transaction manager.
	 */
	public JtxTransactionManager() {
		this.maxResourcesPerTransaction = -1;
		this.resourceManagers = new HashMap<Class, JtxResourceManager>();
	}

	// ---------------------------------------------------------------- config

	/**
	 * Returns max number of resources per transaction.
	 */
	public int getMaxResourcesPerTransaction() {
		return maxResourcesPerTransaction;
	}

	/**
	 * Sets max number of resources per transaction.
	 */
	public void setMaxResourcesPerTransaction(int maxResourcesPerTransaction) {
		this.maxResourcesPerTransaction = maxResourcesPerTransaction;
	}

	/**
	 * Returns whether existing transactions should be validated before participating in them.
	 */
	public boolean isValidateExistingTransaction() {
		return validateExistingTransaction;
	}

	/**
	 * Sets whether existing transactions should be validated before participating
	 * in them.
	 * <p>When participating in an existing transaction (e.g. with
	 * PROPAGATION_REQUIRES or PROPAGATION_SUPPORTS encountering an existing
	 * transaction), this outer transaction's characteristics will apply even
	 * to the inner transaction scope. Validation will detect incompatible
	 * isolation level and read-only settings on the inner transaction definition
	 * and reject participation accordingly through throwing a corresponding exception.
	 */
	public void setValidateExistingTransaction(boolean validateExistingTransaction) {
		this.validateExistingTransaction = validateExistingTransaction;
	}

	/**
	 * Returns <code>true</code> if this transaction manager works with just one resource.
	 */
	public boolean isSingleResourceManager() {
		return oneResourceManager;
	}
	/**
	 * Specifies if transaction manager works with just one resource.
	 */
	public void setSingleResourceManager(boolean oneResourceManager) {
		this.oneResourceManager = oneResourceManager;
	}

	/**
	 * Returns if transaction scope should be ignored.
	 */
	public boolean isIgnoreScope() {
		return ignoreScope;
	}

	/**
	 * Sets if transaction scope should be ignored. If ignored,
	 * there may be more then one transaction in one scope.
	 * Scopes may be ignored if set to <code>null</code>
	 */
	public void setIgnoreScope(boolean ignoreScope) {
		this.ignoreScope = ignoreScope;
	}

	// ---------------------------------------------------------------- count

	/**
	 * Returns total number of transactions associated with current thread.
	 */
	public int totalThreadTransactions() {
		return txStack.get().size();
	}

	/**
	 * Returns total number of transactions of the specified status associated with current thread.
	 */
	public int totalThreadTransactionsWithStatus(JtxStatus status) {
		LinkedList<JtxTransaction> txlist = txStack.get();
		int count = 0;
		for (JtxTransaction tx : txlist) {
			if (tx.getStatus() == status) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Returns total number of active transactions associated with current thread.
	 */
	public int totalActiveThreadTransactions() {
		return totalThreadTransactionsWithStatus(STATUS_ACTIVE);
	}

	/**
	 * Returns <code>true</code> if provided transaction
	 * is associated with current thread.
	 */
	public boolean isAssociatedWithThread(JtxTransaction tx) {
		return txStack.get().contains(tx);
	}

	// ---------------------------------------------------------------- thread work

	/**
	 * Removes transaction association with current thread.
	 * Transaction should be properly handled (committed or rolledback)
	 * before removing from current thread. 
	 */
	protected boolean removeTransaction(JtxTransaction tx) {
		totalTransactions--;
		return txStack.get().remove(tx);
	}


	/**
	 * Returns last transaction associated with current thread or
	 * <code>null</code> when thread has no associated transactions created
	 * by this transaction manager.
	 */
	public JtxTransaction getTransaction() {
		LinkedList<JtxTransaction> txlist = txStack.get();
		if (txlist.isEmpty() == true) {
			return null;
		}
		return txlist.getLast();
	}

	/**
	 * Associate transaction to current thread.
	 */
	protected void associateTransaction(JtxTransaction tx) {
		totalTransactions++;
		txStack.get().addLast(tx);
	}

	protected int totalTransactions;

	/**
	 * Returns total number of transactions issued by this transaction manager.
	 */
	public int totalTransactions() {
		return totalTransactions;
	}

	// ---------------------------------------------------------------- create

	/**
	 * Creates new {@link jodd.jtx.JtxTransaction} instance.
	 * Custom implementations of manager may override this method for
	 * creating custom transaction instances.
	 */
	protected JtxTransaction createNewTransaction(JtxTransactionMode tm, Object scope, boolean active) {
		return new JtxTransaction(this, tm, scope, active);
	}


	// ---------------------------------------------------------------- propagation

	public JtxTransaction requestTransaction(JtxTransactionMode mode) {
		return requestTransaction(mode, null);
	}

	/**
	 * Requests transaction with specified {@link JtxTransactionMode mode}.
	 * Depending on propagation behavior, it will return either <b>existing<b> or <b>new</b> transaction.
	 * Only one transaction can be opened over one scope.
	 * The exception may be thrown indicating propagation mismatch.
	 */
	public JtxTransaction requestTransaction(JtxTransactionMode mode, Object scope) {
		if (log.isDebugEnabled()) {
			log.debug("Requesting TX " + mode.toString());
		}
		JtxTransaction currentTx = getTransaction();
		if (isNewTxScope(currentTx, scope) == false) {
			return currentTx;
		}
		switch (mode.getPropagationBehavior()) {
			case PROPAGATION_REQUIRED: return propRequired(currentTx, mode, scope);
			case PROPAGATION_SUPPORTS: return propSupports(currentTx, mode, scope);
			case PROPAGATION_MANDATORY: return propMandatory(currentTx, mode, scope);
			case PROPAGATION_REQUIRES_NEW: return propRequiresNew(currentTx, mode, scope);
			case PROPAGATION_NOT_SUPPORTED: return propNotSupported(currentTx, mode, scope);
			case PROPAGATION_NEVER: return propNever(currentTx, mode, scope);
		}
		throw new JtxException("Invalid transaction propagation value (" + mode.getPropagationBehavior().value() + ')');
	}

	/**
	 * Returns <code>true</code> if scope is specified and it is different then of existing transaction.
	 */
	protected boolean isNewTxScope(JtxTransaction currentTx, Object destScope) {
		if (ignoreScope == true) {
			return true;
		}
		if (currentTx == null) {
			return true;
		}
		if (destScope == null) {
			return true;
		}
		if (currentTx.getScope() == null) {
			return true;
		}
		return !destScope.equals(currentTx.getScope());
	}

	/**
	 * Check if propagation of a transaction is possible, due to source and destination transaction modes.
	 * @see #setValidateExistingTransaction(boolean) 
	 */
	protected void continueTx(JtxTransaction sourceTx, JtxTransactionMode destMode) {
		if (validateExistingTransaction == false) {
			return;
		}
		JtxTransactionMode sourceMode = sourceTx.getTransactionMode();
		JtxIsolationLevel destIsolationLevel = destMode.getIsolationLevel();
		if (destIsolationLevel != ISOLATION_DEFAULT) {
			JtxIsolationLevel currentIsolationLevel = sourceMode.getIsolationLevel();
			if (currentIsolationLevel != destIsolationLevel) {
				throw new JtxException("Participating transaction specifies isolation level '" + destIsolationLevel +
						"' which is incompatible with existing transaction: '" + currentIsolationLevel + "'.");
			}
		}
		if ((destMode.isReadOnly() == false) && (sourceMode.isReadOnly())) {
			throw new JtxException("Participating transaction is not marked as read-only, but existing transaction is.");
		}
	}


	/**
	 * Propagation: REQUIRED
	 * <pre>
	 * None -> T2
	 * T1   -> T1 (cont.)
	 * </pre>
	 */
	protected JtxTransaction propRequired(JtxTransaction currentTx, JtxTransactionMode mode, Object scope) {
		if ((currentTx == null) || (currentTx.isNoTransaction() == true)) {
			currentTx = createNewTransaction(mode, scope, true);
		} else {
			continueTx(currentTx, mode);
		}
		return currentTx;
	}

	/**
	 * Propagation: REQUIRES_NEW
	 * <pre>
	 * None -> T2
	 * T1   -> T2
	 * </pre>
	 */
	@SuppressWarnings({"UnusedDeclaration"})
	protected JtxTransaction propRequiresNew(JtxTransaction currentTx, JtxTransactionMode mode, Object scope) {
		return createNewTransaction(mode, scope, true);
	}

	/**
	 * Propagation: SUPPORTS
	 * <pre>
	 * None -> None
	 * T1   -> T1 (cont.)
	 * </pre>
	 */
	protected JtxTransaction propSupports(JtxTransaction currentTx, JtxTransactionMode mode, Object scope) {
		if ((currentTx != null) && (currentTx.isNoTransaction() != true)) {
			continueTx(currentTx, mode);
		}
		if (currentTx == null) {
			currentTx = createNewTransaction(mode, scope, false);
		}
		return currentTx;
	}

	/**
	 * Propagation: MANDATORY
	 * <pre>
	 * None -> Error
	 * T1   -> T1 (cont.)
	 * </pre>
	 */
	@SuppressWarnings({"UnusedDeclaration"})
	protected JtxTransaction propMandatory(JtxTransaction currentTx, JtxTransactionMode mode, Object scope) {
		if ((currentTx == null) || (currentTx.isNoTransaction() == true)) {
			throw new JtxException("No existing transaction found for transaction marked with propagation 'mandatory'.");
		}
		continueTx(currentTx, mode);
		return currentTx;
	}

	/**
	 * Propagation: NOT_SUPPORTED
	 * <pre>
	 * None -> None
	 * T1   -> None
	 * </pre>
	 */
	protected JtxTransaction propNotSupported(JtxTransaction currentTx, JtxTransactionMode mode, Object scope) {
		if (currentTx == null) {
			return createNewTransaction(mode, scope, false);
		}
		if (currentTx.isNoTransaction() == true) {
			return currentTx;
		}
		return createNewTransaction(mode, scope, false);
	}

	/**
	 * Propagation: NEVER
	 * <pre>
	 * None -> None
	 * T1   -> Error
	 * </pre>
	 */
	protected JtxTransaction propNever(JtxTransaction currentTx, JtxTransactionMode mode, Object scope) {
		if ((currentTx != null) && (currentTx.isNoTransaction() == false)) {
			throw new JtxException("Existing transaction found for transaction marked with propagation 'never'.");
		}
		if (currentTx == null) {
			currentTx = createNewTransaction(mode, scope, false);
		}
		return currentTx;
	}

	// ---------------------------------------------------------------- resources

	/**
	 * Registers new {@link JtxResourceManager resource manager}.
	 */
	public void registerResourceManager(JtxResourceManager resourceManager) {
		if ((oneResourceManager == true) && (resourceManagers.isEmpty() == false)) {
			throw new JtxException("Transaction manager allows only one resource manager.");
		}
		this.resourceManagers.put(resourceManager.getResourceType(), resourceManager);
	}

	/**
	 * Lookups resource manager for provided type. Throws an exception if provider doesn't exists.
	 */
	protected <E> JtxResourceManager<E> lookupResourceManager(Class<E> resourceType) {
		//noinspection unchecked
		JtxResourceManager<E> resourceManager = this.resourceManagers.get(resourceType);
		if (resourceManager == null) {
			throw new JtxException("No registered resource manager for resource type: " + resourceType.getSimpleName());
		}
		return resourceManager;
	}


	// ---------------------------------------------------------------- close

	/**
	 * Closes transaction manager. All registered {@link JtxResourceManager}
	 * will be closed.
	 */
	public void close() {
		for (JtxResourceManager resourceManager : this.resourceManagers.values()) {
			try {
				resourceManager.close();
			} catch (Exception ex) {
				// ignore
			}
		}
		resourceManagers.clear();
	}

}

