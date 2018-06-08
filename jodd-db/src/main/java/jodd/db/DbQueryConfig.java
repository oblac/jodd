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
 * Db query default configuration.
 */
public class DbQueryConfig {

	/**
	 * Default debug mode.
	 */
	private boolean debug = false;
	/**
	 * Enables creation of prepared statements for all queries.
	 */
	private boolean forcePreparedStatement = false;
	/**
	 * Default type.
	 */
	private QueryScrollType type = QueryScrollType.FORWARD_ONLY;
	/**
	 * Default concurrency type.
	 */
	private QueryConcurrencyType concurrencyType = QueryConcurrencyType.READ_ONLY;
	/**
	 * Default holdability.
	 */
	private QueryHoldability holdability = QueryHoldability.DEFAULT;
	/**
	 * Default value for fetch size.
	 */
	private int fetchSize = 0;
	/**
	 * Default value for max rows.
	 */
	private int maxRows = 0;


	public boolean isDebug() {
		return debug;
	}

	public void setDebug(final boolean debug) {
		this.debug = debug;
	}

	public boolean isForcePreparedStatement() {
		return forcePreparedStatement;
	}

	public void setForcePreparedStatement(final boolean forcePreparedStatement) {
		this.forcePreparedStatement = forcePreparedStatement;
	}

	public QueryScrollType getType() {
		return type;
	}

	public void setType(final QueryScrollType type) {
		this.type = type;
	}

	public QueryConcurrencyType getConcurrencyType() {
		return concurrencyType;
	}

	public void setConcurrencyType(final QueryConcurrencyType concurrencyType) {
		this.concurrencyType = concurrencyType;
	}

	public QueryHoldability getHoldability() {
		return holdability;
	}

	public void setHoldability(final QueryHoldability holdability) {
		this.holdability = holdability;
	}

	public int getFetchSize() {
		return fetchSize;
	}

	public void setFetchSize(final int fetchSize) {
		this.fetchSize = fetchSize;
	}

	public int getMaxRows() {
		return maxRows;
	}

	public void setMaxRows(final int maxRows) {
		this.maxRows = maxRows;
	}
}
