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

import jodd.time.JulianDate;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

/**
 * JDateTime sql type stores JDateTime data as number of milliseconds passed from 1970.
 */
public class JulianDateSqlType extends SqlType<JulianDate> {

	@Override
	public void set(final PreparedStatement st, final int index, final JulianDate value, final int dbSqlType) throws SQLException {
		if (value == null) {
			st.setNull(index, dbSqlType);
			return;
		}
		if (dbSqlType == Types.TIMESTAMP) {
			st.setTimestamp(index, new Timestamp(value.toMilliseconds()));
			return;
		}
		st.setLong(index, value.toMilliseconds());
	}

	@Override
	public JulianDate get(final ResultSet rs, final int index, final int dbSqlType) throws SQLException {
		if (dbSqlType == Types.TIMESTAMP) {
			Timestamp timestamp = rs.getTimestamp(index);
			if (timestamp == null) {
				return null;
			}
			return JulianDate.of(timestamp.getTime());
		}
		long time = rs.getLong(index);

		if (time == 0 && rs.wasNull()) {
			return null;
		}
		return new JulianDate(time);
	}
}
