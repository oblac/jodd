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

package jodd.db;

import jodd.util.HashCode;
import static jodd.util.HashCode.hash;

import java.sql.Connection;

/**
 * Native SQL transaction mode for {@link DbSession} transactions.
 */
public class DbTransactionMode {

	public DbTransactionMode() {
		this.isolation = ISOLATION_DEFAULT;
		this.readOnlyMode = READ_ONLY;
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

	/**
	 * Returns isolation level.
	 */
	public int getIsolation() {
		return isolation;
	}

	/**
	 * Sets isolation level.
	 */
	public DbTransactionMode setIsolation(int isolation) {
		this.isolation = isolation;
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
