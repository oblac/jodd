// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.jtx;

import jodd.log.Log;

import static jodd.jtx.JtxStatus.*;
import static jodd.jtx.JtxTransactionMode.DEFAULT_TIMEOUT;

import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

/**
 * Transaction is an unit of work that is performed by one or more resources.
 * Created and controlled by {@link JtxTransactionManager transaction manager}.
 * <p>
 * Transaction is associated to a thread from where it was created.
 * Transaction may have more than one resource attached.
 * <p>
 * Committing and rolling back transaction actually performs commit/rollback on
 * all attached resources. Therefore, it may happens that committing of one resource
 * fails, what actually breaks the atomicity of the transaction. If transaction is
 * attached just to one resource, atomicity is saved.
 * <p>
 * Only one resource of some type may exists in the transaction. There is no way to have
 * two resources of the same type in one transaction. 
 * <p>
 * This class is responsible for transaction life-cycle. It can be consider that all
 * work is scoped to associated thread.
 */
public class JtxTransaction {

	private static final Log log = Log.getLogger(JtxTransaction.class);

	// ---------------------------------------------------------------- init

	protected final JtxTransactionManager txManager;
	protected final JtxTransactionMode mode;
	protected final Set<JtxResource> resources;
	protected final Object context;
	protected final long deadline;
	protected final boolean startAsActive;
	protected Throwable rollbackCause;
	protected JtxStatus status;

	/**
	 * Creates new transaction. Should be invoked by {@link jodd.jtx.JtxTransactionManager}.
	 * @param txManager jtx manager
	 * @param mode transaction mode
	 * @param context transaction context within transaction works
	 * @param active if <code>true</code> it is an active transaction, otherwise it's not
	 */
	public JtxTransaction(JtxTransactionManager txManager, JtxTransactionMode mode, Object context, boolean active) {
		this.txManager = txManager;
		this.mode = mode;
		this.context = context;
		this.resources = new HashSet<JtxResource>();
		this.deadline = mode.getTransactionTimeout() == DEFAULT_TIMEOUT ?
				DEFAULT_TIMEOUT :
				System.currentTimeMillis() + (mode.getTransactionTimeout() * 1000L);
		this.status = active ? STATUS_ACTIVE : STATUS_NO_TRANSACTION;
		this.startAsActive = active;
		txManager.associateTransaction(this);
		if (log.isDebugEnabled()) {
			log.debug("New JTX {status:" + this.status + ", mode:" + this.mode + '}');
		}
	}

	/**
	 * Returns transaction mode.
	 */
	public JtxTransactionMode getTransactionMode() {
		return mode;
	}

	/**
	 * Returns transaction manager that owns this transaction.
	 */
	public JtxTransactionManager getTransactionManager() {
		return txManager;
	}

	/**
	 * Returns transaction context if exist, or <code>null</code>.
	 */
	public Object getContext() {
		return context;
	}

	// ---------------------------------------------------------------- status

	/**
	 * Returns current transaction status.
	 */
	public JtxStatus getStatus() {
		return status;
	}

	/**
	 * Returns <code>true</code> if transaction started as active one.
	 * This value is never changed, while {@link #getStatus() status}
	 * changes during the execution.
	 */
	public boolean isStartAsActive() {
		return startAsActive;
	}

	/**
	 * Returns <code>true</code> if transaction is active.
	 * This status changes during the transaction flow.
	 */
	public boolean isActive() {
		return status == STATUS_ACTIVE;
	}

	/**
	 * Returns <code>true</code> if transaction is explicitly forbidden, i.e.
	 * session is in <b>auto-commit</b> mode.
	 */
	public boolean isNoTransaction() {
		return status == STATUS_NO_TRANSACTION;
	}

	/**
	 * Returns <code>true</code> if transaction and all its resources are committed successfully.
	 */
	public boolean isCommitted() {
		return status == STATUS_COMMITTED;
	}

	/**
	 * Returns <code>true</code> if transaction and all its resources are rolled-back successfully.
	 */
	public boolean isRolledback() {
		return status == STATUS_ROLLEDBACK;
	}

	/**
	 * Returns <code>true</code> if transaction is either committed or rolled back.
	 */
	public boolean isCompleted() {
		return status == STATUS_COMMITTED || status == STATUS_ROLLEDBACK;
	}

	// ---------------------------------------------------------------- rollback

	/**
	 * Modify the transaction associated with the target object such that the only possible outcome
	 * of the transaction is to roll back the transaction.
	 */
	public void setRollbackOnly() {
		setRollbackOnly(null);
	}

	/**
	 * Modify the transaction associated with the target object such that the only possible outcome
	 * of the transaction is to roll back the transaction.
	 */
	public void setRollbackOnly(Throwable th) {
		if (isNoTransaction() == false) {
			if ((status != STATUS_MARKED_ROLLBACK) && (status != STATUS_ACTIVE)) {
				throw new JtxException("There is no active transaction that can be marked as rollback only.");
			}
		}
		rollbackCause = th;
		status = STATUS_MARKED_ROLLBACK;
	}

	/**
	 * Returns <code>true</code> if transaction is marked as rollback only.
	 */
	public boolean isRollbackOnly() {
		return status == STATUS_MARKED_ROLLBACK;
	}

	// ---------------------------------------------------------------- timeout

	/**
	 * Sets the rollback-only if the deadline has been reached and throws an exception.
	 */
	protected void checkTimeout() {
		if (deadline == DEFAULT_TIMEOUT) {
			return;
		}
		if (this.deadline - System.currentTimeMillis() < 0) {
			setRollbackOnly();
			throw new JtxException("Transaction timed out, marked as rollback only.");
		}
	}

	// ---------------------------------------------------------------- core

	/**
	 * Commit and completes current transaction. Transaction is committed on all attached resources. After, resources
	 * are detached from the transaction. When this method completes,transaction is no longer
	 * associated with current thread.
	 */
	public void commit() {
		checkTimeout();
		commitOrRollback(true);
	}

	/**
	 * Roll back and completes current transaction. Transaction is rolled back on all attached resources.
	 * Resource are then detached from the transaction. When this method completes, transaction is no
	 * longer associated with current thread.
	 */
	public void rollback() {
		commitOrRollback(false);
	}

	/**
	 * Performs either commit or rollback on all transaction resources.
	 */
	protected void commitOrRollback(boolean doCommit) {
		if (log.isDebugEnabled()) {
			if (doCommit) {
				log.debug("Commit JTX");
			} else {
				log.debug("Rollback JTX");
			}
		}
		boolean forcedRollback = false;
		if (isNoTransaction() == false) {
			if (isRollbackOnly()) {
				if (doCommit == true) {
					doCommit = false;
					forcedRollback = true;
				}
			} else if (isActive() == false) {
				if (isCompleted()) {
					throw new JtxException("Transaction is already completed, commit or rollback should be called once per transaction.");
				}
				throw new JtxException("No active transaction to " + (doCommit ? "commit." : "rollback."));
			}
		}
		if (doCommit == true) {
			commitAllResources();
		} else {
			rollbackAllResources(forcedRollback);
		}
	}

	// ---------------------------------------------------------------- resources

	/**
	 * Commits all attached resources. On successful commit, resource will be closed
	 * and detached from this transaction. On exception, resource remains attached
	 * to transaction.
	 * <p>
	 * All resources will be committed, even if commit fails on some in that process.
	 * If there was at least one failed commit, its exception will be re-thrown after finishing
	 * committing all resources, and transaction will be marked as rollback only.
	 */
	protected void commitAllResources() throws JtxException {
		status = STATUS_COMMITTING;
		Exception lastException = null;
		Iterator<JtxResource> it = resources.iterator();
		while (it.hasNext()) {
			JtxResource resource = it.next();
			try {
				resource.commitTransaction();
				it.remove();
			} catch (Exception ex) {
				lastException = ex;
			}
		}
		if (lastException != null) {
			setRollbackOnly(lastException);
			throw new JtxException("Commit failed: one or more transaction resources couldn't commit a transaction.", lastException);
		}
		txManager.removeTransaction(this);
		status = STATUS_COMMITTED;
	}

	/**
	 * Rollbacks all attached resources. Resource will be closed. and detached from this transaction.
	 * If exception occurs, it will be rethrown at the end.
	 */
	protected void rollbackAllResources(boolean wasForced) {
		status = STATUS_ROLLING_BACK;
		Exception lastException = null;
		Iterator<JtxResource> it = resources.iterator();
		while (it.hasNext()) {
			JtxResource resource = it.next();
			try {
				resource.rollbackTransaction();
			} catch (Exception ex) {
				lastException = ex;
			} finally {
				it.remove();
			}
		}
		txManager.removeTransaction(this);
		status = STATUS_ROLLEDBACK;
		if (lastException != null) {
			status = STATUS_UNKNOWN;
			throw new JtxException("Rollback failed: one or more transaction resources couldn't rollback a transaction.", lastException);
		}
		if (wasForced) {
			throw new JtxException("Transaction rolled back because it has been marked as rollback-only.", rollbackCause);
		}
	}


	// ---------------------------------------------------------------- resources

	/**
	 * Requests a resource. If resource is not found, it will be created and new transaction will be started on it.
	 */
	public <E> E requestResource(Class<E> resourceType) {
		if (isCompleted()) {
			throw new JtxException("Transaction is already completed, resource are not available after commit or rollback.");
		}
		if (isRollbackOnly()) {
			throw new JtxException("Transaction is marked as rollback only, resource are not available.", rollbackCause);
		}
		if (!isNoTransaction() && !isActive()) {
			throw new JtxException("Resources are not available since transaction is not active.");
		}
		checkTimeout();
		E resource = lookupResource(resourceType);
		if (resource == null) {
			int maxResources = txManager.getMaxResourcesPerTransaction();
			if ((maxResources != -1) && (resources.size() >= maxResources)) {
				throw new JtxException("Transaction already has attached max. number of resources.");
			}
			JtxResourceManager<E> resourceManager = txManager.lookupResourceManager(resourceType);
			resource = resourceManager.beginTransaction(mode, isActive());
			resources.add(new JtxResource<E>(this, resourceManager, resource));
		}
		return resource;
	}

	/**
	 * Lookups for open resource. Returns <code>null</code> if resource not found.
	 * Only open resources can be found.
	 */
	protected <E> E lookupResource(Class<E> resourceType) {
		for (JtxResource jtxResource : resources) {
			if (jtxResource.isSameTypeAsResource(resourceType)) {
				//noinspection unchecked
				return (E) jtxResource.getResource();
			}
		}
		return null;
	}
}
