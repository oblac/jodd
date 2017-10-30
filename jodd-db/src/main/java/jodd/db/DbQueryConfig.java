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

/**
 * Default configuration of a query.
 */
public class DbQueryConfig {

	protected boolean forcePreparedStatement = false;
	protected int type = DbQuery.TYPE_FORWARD_ONLY;
	protected int concurrencyType = DbQuery.CONCUR_READ_ONLY;
	protected int holdability = DbQuery.DEFAULT_HOLDABILITY;
	protected int fetchSize = 0;
	protected int maxRows = 0;

	public boolean isForcePreparedStatement() {
		return forcePreparedStatement;
	}

	/**
	 * Enables creation of prepared statements for all queries.
	 */
	public void setForcePreparedStatement(boolean forcePreparedStatement) {
		this.forcePreparedStatement = forcePreparedStatement;
	}

	public int getType() {
		return type;
	}

	/**
	 * Sets default type.
	 * @see DbQuery#setType(int)
	 */
	public void setType(int type) {
		this.type = type;
	}

	public int getConcurrencyType() {
		return concurrencyType;
	}

	/**
	 * Sets default concurrency type.
	 * @see DbQuery#setConcurrencyType(int)
	 */
	public void setConcurrencyType(int concurrencyType) {
		this.concurrencyType = concurrencyType;
	}

	public int getHoldability() {
		return holdability;
	}

	/**
	 * Sets default holdability.
	 * @see DbQuery#setHoldability(int)
	 */
	public void setHoldability(int holdability) {
		this.holdability = holdability;
	}

	public int getFetchSize() {
		return fetchSize;
	}

	/**
	 * Sets default value for fetch size.
	 * @see DbQuery#setFetchSize(int)
	 */
	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}

	/**
	 * Returns default value for max rows.
	 */
	public int getMaxRows() {
		return maxRows;
	}

	/**
	 * Sets default value for max rows.
	 * @see DbQuery#setMaxRows(int)
	 */
	public void setMaxRows(int maxRows) {
		this.maxRows = maxRows;
	}

}