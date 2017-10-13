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

package jodd.db.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Null-aware {@link SqlType sql types}.
 */
public abstract class NullAwareSqlType<T> extends SqlType<T> {

	/**
	 * Detects if there was a <code>null</code> reading and returns <code>null</code> if it was.
	 * Result set returns default value (e.g. 0) for many getters, therefore it detects if it was
	 * a null reading or it is a real value.
	 */
	@Override
	public <E> E readValue(ResultSet rs, int index, Class<E> destinationType, int dbSqlType) throws SQLException {
		T t = get(rs, index, dbSqlType);
		if ((t == null) || (rs.wasNull())) {
			return null;
		}
		return prepareGetValue(t, destinationType);
	}

	/**
	 * Detects <code>null</code> before storing the value into the database.
	 */
	@Override
	public void storeValue(PreparedStatement st, int index, Object value, int dbSqlType) throws SQLException {
		if (value == null) {
			st.setNull(index, dbSqlType);
			return;
		}
		super.storeValue(st, index, value, dbSqlType);
	}

}
