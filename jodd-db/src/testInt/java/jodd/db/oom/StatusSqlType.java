package jodd.db.oom;

import jodd.db.oom.fixtures.Tester3;
import jodd.db.type.SqlType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StatusSqlType extends SqlType<Tester3.Status> {
	@Override
	public void set(PreparedStatement st, int index, Tester3.Status value, int dbSqlType) throws SQLException {
		st.setString(index, value.toString());
	}

	@Override
	public Tester3.Status get(ResultSet rs, int index, int dbSqlType) throws SQLException {
		return Tester3.Status.valueOf(rs.getString(index));
	}
}
