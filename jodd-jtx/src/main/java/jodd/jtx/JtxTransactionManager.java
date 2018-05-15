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

package jodd.jtx;

import jodd.cache.TypeCache;
import jodd.log.Logger;
import jodd.log.LoggerFactory;

import java.util.ArrayList;

import static jodd.jtx.JtxIsolationLevel.ISOLATION_DEFAULT;
import static jodd.jtx.JtxStatus.STATUS_ACTIVE;

/**
 * {@link JtxTransaction} manager is responsible for handling transaction
 * propagation and resource managers. Holds various JTX configuration data.
 * <p>
 * Note that transactions are hold inside a thread-local transaction stack.
 * Therefore, if one transaction is created after the other during the
 * same thread, the second transaction will be aware that it is 'after'
 * the first one.
 */
public class JtxTransactionManager {

	private static final Logger log = LoggerFactory.getLogger(JtxTransactionManager.class);

	protected int maxResourcesPerTransaction;
	protected boolean oneResourceManager;
	protected boolean validateExistingTransaction;
	protected boolean ignoreScope;
	protected TypeCache<JtxResourceManager> resourceManagers;

	protected final ThreadLocal<ArrayList<JtxTransaction>> txStack = new ThreadLocal<>();

	/**
	 * Creates new transaction manager.
	 */
	public JtxTransactionManager() {
		this.maxResourcesPerTransaction = -1;
		this.resourceManagers = TypeCache.createDefault();
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
	public void setMaxResourcesPerTransaction(final int maxResourcesPerTransaction) {
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
	public void setValidateExistingTransaction(final boolean validateExistingTransaction) {
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
	public void setSingleResourceManager(final boolean oneResourceManager) {
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
	public void setIgnoreScope(final boolean ignoreScope) {
		this.ignoreScope = ignoreScope;
	}

	// ---------------------------------------------------------------- count

	/**
	 * Returns total number of transactions associated with current thread.
	 */
	public int totalThreadTransactions() {
		ArrayList<JtxTransaction> txList = txStack.get();
		if (txList == null) {
			return 0;
		}
		return txList.size();
	}

	/**
	 * Returns total number of transactions of the specified status associated with current thread.
	 */
	public int totalThreadTransactionsWithStatus(final JtxStatus status) {
		ArrayList<JtxTransaction> txlist = txStack.get();
		if (txlist == null) {
			return 0;
		}
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
	public boolean isAssociatedWithThread(final JtxTransaction tx) {
		ArrayList<JtxTransaction> txList = txStack.get();
		if (txList == null) {
			return false;
		}
		return txList.contains(tx);
	}

	// ---------------------------------------------------------------- thread work

	/**
	 * Removes transaction association with current thread.
	 * Transaction should be properly handled (committed or rolledback)
	 * before removing from current thread.
	 * Also removes thread list from this thread.
	 */
	protected boolean removeTransaction(final JtxTransaction tx) {
		ArrayList<JtxTransaction> txList = txStack.get();
		if (txList == null) {
			return false;
		}

		boolean removed = txList.remove(tx);
		if (removed) {
			totalTransactions--;
		}

		if (txList.isEmpty()) {
			txStack.remove();
		}

		return removed;
	}


	/**
	 * Returns last transaction associated with current thread or
	 * <code>null</code> when thread has no associated transactions created
	 * by this transaction manager.
	 */
	public JtxTransaction getTransaction() {
		ArrayList<JtxTransaction> txlist = txStack.get();
		if (txlist == null) {
			return null;
		}
		if (txlist.isEmpty()) {
			return null;
		}
		return txlist.get(txlist.size() - 1);	// get last
	}

	/**
	 * Associate transaction to current thread.
	 */
	protected void associateTransaction(final JtxTransaction tx) {
		totalTransactions++;
		ArrayList<JtxTransaction> txList = txStack.get();
		if (txList == null) {
			txList = new ArrayList<>();
			txStack.set(txList);
		}
		txList.add(tx);	// add last
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
	protected JtxTransaction createNewTransaction(final JtxTransactionMode tm, final Object scope, final boolean active) {
		return new JtxTransaction(this, tm, scope, active);
	}


	// ---------------------------------------------------------------- propagation

	public JtxTransaction requestTransaction(final JtxTransactionMode mode) {
		return requestTransaction(mode, null);
	}

	/**
	 * Requests transaction with specified {@link JtxTransactionMode mode}.
	 * Depending on propagation behavior, it will return either <b>existing</b> or <b>new</b> transaction.
	 * Only one transaction can be opened over one scope.
	 * The exception may be thrown indicating propagation mismatch.
	 */
	public JtxTransaction requestTransaction(final JtxTransactionMode mode, final Object scope) {
		if (log.isDebugEnabled()) {
			log.debug("Requesting TX " + mode.toString());
		}
		JtxTransaction currentTx = getTransaction();
		if (!isNewTxScope(currentTx, scope)) {
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
		throw new JtxException("Invalid TX propagation value: " + mode.getPropagationBehavior().value());
	}

	/**
	 * Returns <code>true</code> if scope is specified and it is different then of existing transaction.
	 */
	protected boolean isNewTxScope(final JtxTransaction currentTx, final Object destScope) {
		if (ignoreScope) {
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
	protected void continueTx(final JtxTransaction sourceTx, final JtxTransactionMode destMode) {
		if (!validateExistingTransaction) {
			return;
		}
		JtxTransactionMode sourceMode = sourceTx.getTransactionMode();
		JtxIsolationLevel destIsolationLevel = destMode.getIsolationLevel();
		if (destIsolationLevel != ISOLATION_DEFAULT) {
			JtxIsolationLevel currentIsolationLevel = sourceMode.getIsolationLevel();
			if (currentIsolationLevel != destIsolationLevel) {
				throw new JtxException("Participating TX specifies isolation level: " + destIsolationLevel +
						" which is incompatible with existing TX: " + currentIsolationLevel);
			}
		}
		if ((!destMode.isReadOnly()) && (sourceMode.isReadOnly())) {
			throw new JtxException("Participating TX is not marked as read-only, but existing TX is");
		}
	}


	/**
	 * Propagation: REQUIRED
	 * <pre>{@code
	 * None -> T2
	 * T1   -> T1 (cont.)
	 * }</pre>
	 */
	protected JtxTransaction propRequired(JtxTransaction currentTx, final JtxTransactionMode mode, final Object scope) {
		if ((currentTx == null) || (currentTx.isNoTransaction())) {
			currentTx = createNewTransaction(mode, scope, true);
		} else {
			continueTx(currentTx, mode);
		}
		return currentTx;
	}

	/**
	 * Propagation: REQUIRES_NEW
	 * <pre>{@code
	 * None -> T2
	 * T1   -> T2
	 * }</pre>
	 */
	@SuppressWarnings({"UnusedDeclaration"})
	protected JtxTransaction propRequiresNew(final JtxTransaction currentTx, final JtxTransactionMode mode, final Object scope) {
		return createNewTransaction(mode, scope, true);
	}

	/**
	 * Propagation: SUPPORTS
	 * <pre>{@code
	 * None -> None
	 * T1   -> T1 (cont.)
	 * }</pre>
	 */
	protected JtxTransaction propSupports(JtxTransaction currentTx, final JtxTransactionMode mode, final Object scope) {
		if ((currentTx != null) && (!currentTx.isNoTransaction())) {
			continueTx(currentTx, mode);
		}
		if (currentTx == null) {
			currentTx = createNewTransaction(mode, scope, false);
		}
		return currentTx;
	}

	/**
	 * Propagation: MANDATORY
	 * <pre>{@code
	 * None -> Error
	 * T1   -> T1 (cont.)
	 * }</pre>
	 */
	@SuppressWarnings({"UnusedDeclaration"})
	protected JtxTransaction propMandatory(final JtxTransaction currentTx, final JtxTransactionMode mode, final Object scope) {
		if ((currentTx == null) || (currentTx.isNoTransaction())) {
			throw new JtxException("No existing TX found for TX marked with propagation 'mandatory'");
		}
		continueTx(currentTx, mode);
		return currentTx;
	}

	/**
	 * Propagation: NOT_SUPPORTED
	 * <pre>{@code
	 * None -> None
	 * T1   -> None
	 * }</pre>
	 */
	protected JtxTransaction propNotSupported(final JtxTransaction currentTx, final JtxTransactionMode mode, final Object scope) {
		if (currentTx == null) {
			return createNewTransaction(mode, scope, false);
		}
		if (currentTx.isNoTransaction()) {
			return currentTx;
		}
		return createNewTransaction(mode, scope, false);
	}

	/**
	 * Propagation: NEVER
	 * <pre>{@code
	 * None -> None
	 * T1   -> Error
	 * }</pre>
	 */
	protected JtxTransaction propNever(JtxTransaction currentTx, final JtxTransactionMode mode, final Object scope) {
		if ((currentTx != null) && (!currentTx.isNoTransaction())) {
			throw new JtxException("Existing TX found for TX marked with propagation 'never'");
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
	public void registerResourceManager(final JtxResourceManager resourceManager) {
		if ((oneResourceManager) && (!resourceManagers.isEmpty())) {
			throw new JtxException("TX manager allows only one resource manager");
		}
		this.resourceManagers.put(resourceManager.getResourceType(), resourceManager);
	}

	/**
	 * Lookups resource manager for provided type. Throws an exception if provider doesn't exists.
	 */
	protected <E> JtxResourceManager<E> lookupResourceManager(final Class<E> resourceType) {
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
		this.resourceManagers.forEachValue(resourceManager -> {
			try {
				resourceManager.close();
			} catch (Exception ex) {
				// ignore
			}
		});
		resourceManagers.clear();
	}

}

