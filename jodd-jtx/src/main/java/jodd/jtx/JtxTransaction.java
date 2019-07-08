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

import jodd.log.Logger;
import jodd.log.LoggerFactory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static jodd.jtx.JtxStatus.STATUS_ACTIVE;
import static jodd.jtx.JtxStatus.STATUS_COMMITTED;
import static jodd.jtx.JtxStatus.STATUS_COMMITTING;
import static jodd.jtx.JtxStatus.STATUS_MARKED_ROLLBACK;
import static jodd.jtx.JtxStatus.STATUS_NO_TRANSACTION;
import static jodd.jtx.JtxStatus.STATUS_ROLLEDBACK;
import static jodd.jtx.JtxStatus.STATUS_ROLLING_BACK;
import static jodd.jtx.JtxStatus.STATUS_UNKNOWN;
import static jodd.jtx.JtxTransactionMode.DEFAULT_TIMEOUT;

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

	private static final Logger log = LoggerFactory.getLogger(JtxTransaction.class);

	// ---------------------------------------------------------------- init

	protected final JtxTransactionManager txManager;
	protected final JtxTransactionMode mode;
	protected final Set<JtxResource> resources;
	protected final Object scope;
	protected final long deadline;
	protected final boolean startAsActive;
	protected Throwable rollbackCause;
	protected JtxStatus status;

	/**
	 * Creates new transaction. Should be invoked by {@link jodd.jtx.JtxTransactionManager}.
	 * If transaction is set as <code>active</code>, it will be actually created, meaning
	 * that it is the first transaction on this connection i.e. in this session.
	 * If transaction is not <code>active</code>, transaction object will be created,
	 * but the real transaction not, and it is expected that one is already created before.
	 *
	 * @param txManager jtx manager
	 * @param mode transaction mode
	 * @param scope transaction live scope within the other transaction requests are ignored
	 * @param active if <code>true</code> it is an active transaction, otherwise it's not
	 */
	public JtxTransaction(final JtxTransactionManager txManager, final JtxTransactionMode mode, final Object scope, final boolean active) {
		this.txManager = txManager;
		this.mode = mode;
		this.scope = scope;
		this.resources = new HashSet<>();
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
	 * Returns transaction scope if exist, or <code>null</code>.
	 */
	public Object getScope() {
		return scope;
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
	public void setRollbackOnly(final Throwable th) {
		if (!isNoTransaction()) {
			if ((status != STATUS_MARKED_ROLLBACK) && (status != STATUS_ACTIVE)) {
				throw new JtxException("TNo active TX that can be marked as rollback only");
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
			throw new JtxException("TX timed out, marked as rollback only");
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
		if (!isNoTransaction()) {
			if (isRollbackOnly()) {
				if (doCommit) {
					doCommit = false;
					forcedRollback = true;
				}
			} else if (!isActive()) {
				if (isCompleted()) {
					throw new JtxException("TX is already completed, commit or rollback should be called once per TX");
				}
				throw new JtxException("No active TX to " + (doCommit ? "commit" : "rollback"));
			}
		}
		if (doCommit) {
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
			throw new JtxException("Commit failed: one or more TX resources couldn't commit a TX", lastException);
		}
		txManager.removeTransaction(this);
		status = STATUS_COMMITTED;
	}

	/**
	 * Rollbacks all attached resources. Resource will be closed. and detached from this transaction.
	 * If exception occurs, it will be rethrown at the end.
	 */
	protected void rollbackAllResources(final boolean wasForced) {
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
			throw new JtxException("Rollback failed: one or more TX resources couldn't rollback a TX", lastException);
		}
		if (wasForced) {
			throw new JtxException("TX rolled back because it has been marked as rollback-only", rollbackCause);
		}
	}


	// ---------------------------------------------------------------- resources

	/**
	 * Requests a resource. If resource is not found, it will be created and new transaction will be started on it.
	 */
	public <E> E requestResource(final Class<E> resourceType) {
		if (isCompleted()) {
			throw new JtxException("TX is already completed, resource are not available after commit or rollback");
		}
		if (isRollbackOnly()) {
			throw new JtxException("TX is marked as rollback only, resource are not available", rollbackCause);
		}
		if (!isNoTransaction() && !isActive()) {
			throw new JtxException("Resources are not available since TX is not active");
		}
		checkTimeout();
		E resource = lookupResource(resourceType);
		if (resource == null) {
			int maxResources = txManager.getMaxResourcesPerTransaction();
			if ((maxResources != -1) && (resources.size() >= maxResources)) {
				throw new JtxException("TX already has attached max. number of resources");
			}
			JtxResourceManager<E> resourceManager = txManager.lookupResourceManager(resourceType);
			resource = resourceManager.beginTransaction(mode, isActive());
			resources.add(new JtxResource<>(this, resourceManager, resource));
		}
		return resource;
	}

	/**
	 * Lookups for open resource. Returns <code>null</code> if resource not found.
	 * Only open resources can be found.
	 */
	protected <E> E lookupResource(final Class<E> resourceType) {
		for (JtxResource jtxResource : resources) {
			if (jtxResource.isSameTypeAsResource(resourceType)) {
				//noinspection unchecked
				return (E) jtxResource.getResource();
			}
		}
		return null;
	}
}
