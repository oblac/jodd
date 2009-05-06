// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db;

import jodd.util.HashCode;
import static jodd.util.HashCode.hash;
import jodd.jtx.JtxTransactionMode;

import java.sql.Connection;

/**
 * Native SQL transaction mode for {@link DbSession} transactions.
 */
public class DbTransactionMode {

	public DbTransactionMode() {
		this.isolation = ISOLATION_DEFAULT;
		this.readOnlyMode = READ_ONLY;
	}


	/**
	 * Created db transaction mode from general {@link jodd.jtx.JtxTransactionMode}.
	 */
	public DbTransactionMode(JtxTransactionMode txMode) {
		switch (txMode.getIsolationLevel()) {
			case ISOLATION_DEFAULT: isolation = ISOLATION_DEFAULT; break;
			case ISOLATION_NONE: isolation = ISOLATION_NONE; break;
			case ISOLATION_READ_COMMITTED: isolation = ISOLATION_READ_COMMITTED; break;
			case ISOLATION_READ_UNCOMMITTED: isolation = ISOLATION_READ_UNCOMMITTED; break;
			case ISOLATION_REPEATABLE_READ: isolation = ISOLATION_REPEATABLE_READ; break;
			case ISOLATION_SERIALIZABLE: isolation = ISOLATION_SERIALIZABLE; break;
		}
		readOnlyMode = txMode.isReadOnly();
	}

	// ---------------------------------------------------------------- isolation

	/**
	 * Default isolation.
	 */
	public static final int ISOLATION_DEFAULT 				= -1;
	/**
	 *  @see Connection#TRANSACTION_NONE
	 */
	public static final int ISOLATION_NONE 					= Connection.TRANSACTION_NONE;
	/**
	 *  @see Connection#TRANSACTION_READ_UNCOMMITTED
	 */
	public static final int ISOLATION_READ_UNCOMMITTED 		= Connection.TRANSACTION_READ_UNCOMMITTED;
	/**
	 *  @see Connection#TRANSACTION_READ_COMMITTED
	 */
	public static final int ISOLATION_READ_COMMITTED 		= Connection.TRANSACTION_READ_COMMITTED;
	/**
	 *  @see Connection#TRANSACTION_REPEATABLE_READ
	 */
	public static final int ISOLATION_REPEATABLE_READ 		= Connection.TRANSACTION_REPEATABLE_READ;
	/**
	 *  @see Connection#TRANSACTION_SERIALIZABLE
	 */
	public static final int ISOLATION_SERIALIZABLE 			= Connection.TRANSACTION_SERIALIZABLE;

	private int isolation;

	public int getIsolation() {
		return isolation;
	}

	public void setIsolation(int isolation) {
		this.isolation = isolation;
	}

	public DbTransactionMode isolationNone() {
		this.isolation = ISOLATION_NONE;
		return this;
	}
	public DbTransactionMode isolationReadUncommitted() {
		this.isolation = ISOLATION_READ_UNCOMMITTED;
		return this;
	}
	public DbTransactionMode isolationReadCommited() {
		this.isolation = ISOLATION_READ_COMMITTED;
		return this;
	}
	public DbTransactionMode isolationRepeatableRead() {
		this.isolation = ISOLATION_REPEATABLE_READ;
		return this;
	}
	public DbTransactionMode isolationSerializable() {
		this.isolation = ISOLATION_SERIALIZABLE;
		return this;
	}



	// ---------------------------------------------------------------- read-only

	public static final boolean READ_ONLY		= true;
	public static final boolean READ_WRITE		= false;

	private boolean readOnlyMode = READ_ONLY;

	public boolean isReadOnly() {
		return readOnlyMode;
	}

	public DbTransactionMode setReadOnly(boolean readOnly) {
		this.readOnlyMode = readOnly;
		return this;
	}


	// ---------------------------------------------------------------- equals & hashCode

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof DbTransactionMode)) {
			return false;
		}
		DbTransactionMode mode = (DbTransactionMode) object;
		return  (mode.getIsolation() == this.isolation) &&
				(mode.isReadOnly() == this.readOnlyMode);
	}

	@Override
	public int hashCode() {
		int result = HashCode.SEED;
		result = hash(result, readOnlyMode);
		result = hash(result, isolation);
		return result;
	}


}
