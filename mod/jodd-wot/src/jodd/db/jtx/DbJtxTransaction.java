// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.jtx;

import jodd.jtx.JtxTransaction;
import jodd.jtx.JtxTransactionManager;
import jodd.jtx.JtxTransactionMode;
import jodd.db.DbSession;

/**
 * {@link JtxTransaction} extension that simplifies beginning of the transaction since
 * related {@link jodd.db.jtx.DbJtxTransactionManager} allows only one resource type. 
 */
public class DbJtxTransaction extends JtxTransaction {

	protected DbJtxTransaction(JtxTransactionManager txManager, JtxTransactionMode mode, Object context) {
		super(txManager, mode, context);
	}

	public DbSession requestResource() {
		return requestResource(DbSession.class);
	}

}
