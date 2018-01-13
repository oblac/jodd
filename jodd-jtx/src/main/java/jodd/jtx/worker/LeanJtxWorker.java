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

package jodd.jtx.worker;

import jodd.jtx.JtxTransaction;
import jodd.jtx.JtxTransactionManager;
import jodd.jtx.JtxTransactionMode;
import jodd.log.Logger;
import jodd.log.LoggerFactory;

/**
 * Lean transaction worker helps dealing transactions when they were requested
 * in several places, usually in separated methods. This worker knows when requested transaction is
 * the same as current one, or completely new. It might be useful for aspects.
 */
public class LeanJtxWorker {

	private static final Logger log = LoggerFactory.getLogger(LeanJtxWorker.class);

	protected final JtxTransactionManager txManager;

	public LeanJtxWorker(final JtxTransactionManager txManager) {
		this.txManager = txManager;
	}

	/**
	 * Returns transaction manager.
	 */
	public JtxTransactionManager getTransactionManager() {
		return txManager;
	}


	/**
	 * Returns current transaction or <code>null</code> if there is no transaction at the moment.
	 */
	public JtxTransaction getCurrentTransaction() {
		return txManager.getTransaction();
	}

	/**
	 * Requests for transaction and returns non-null value <b>only</b> when new transaction
	 * is created! When <code>null</code> is returned, transaction may be get by
	 * {@link #getCurrentTransaction()}.
	 *
	 * @see jodd.jtx.JtxTransactionManager#requestTransaction(jodd.jtx.JtxTransactionMode)
	 */
	public JtxTransaction maybeRequestTransaction(final JtxTransactionMode txMode, final Object scope) {
		if (txMode == null) {
			return null;
		}
		JtxTransaction currentTx = txManager.getTransaction();
		JtxTransaction requestedTx = txManager.requestTransaction(txMode, scope);
		if (currentTx == requestedTx) {
			return null;
		}
		return requestedTx;
	}


	/**
	 * Commits transaction if created in the same level where this method is invoked.
	 * Returns <code>true</code> if transaction was actually committed or <code>false</code>
	 * if transaction was not created on this level. 
	 */
	public boolean maybeCommitTransaction(final JtxTransaction tx) {
		if (tx == null) {
			return false;
		}
		log.debug("commit tx");

		tx.commit();
		return true;
	}

	/**
	 * Rollbacks transaction if created in the same scope where this method is invoked.
	 * If not, current transaction is marked for rollback.
	 * Returns <code>true</code> if transaction was actually roll backed.
	 */
	public boolean markOrRollbackTransaction(JtxTransaction tx, final Throwable cause) {
		if (tx == null) {
			tx = getCurrentTransaction();
			if (tx == null) {
				return false;
			}
			log.debug("set rollback only tx");

			tx.setRollbackOnly(cause);
			return false;
		}
		log.debug("rollback tx");

		tx.rollback();
		return true;
	}
}
