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

package jodd.db.oom.sqlgen;

/**
 * Configurations of SQL generation.
 */
public class SqlGenConfig {

	/**
	 * Returns {@code true} if SQL syntax allow usage of table alias in the update query.
	 */
	public boolean isUpdateAcceptsTableAlias() {
		return updateAcceptsTableAlias;
	}

	public void setUpdateAcceptsTableAlias(boolean updateAcceptsTableAlias) {
		this.updateAcceptsTableAlias = updateAcceptsTableAlias;
	}

	/**
	 * Returns {@code true} if database supports update of the primary key.
	 */
	public boolean isUpdateablePrimaryKey() {
		return updateablePrimaryKey;
	}

	public void setUpdateablePrimaryKey(boolean updateablePrimaryKey) {
		this.updateablePrimaryKey = updateablePrimaryKey;
	}

	private boolean updateAcceptsTableAlias = true;
	private boolean updateablePrimaryKey = true;

}
