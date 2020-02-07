package jodd.db.oom.fixtures

import jodd.db.oom.meta.DbColumn
import jodd.db.oom.meta.DbId
import jodd.db.oom.meta.DbTable
import jodd.db.type.SqlType
import java.sql.PreparedStatement
import java.sql.ResultSet

enum class Status33 {
	NEW, PARTIAL, FAILED, SELECTED, UPLOADED
}

class Status33SqlType : SqlType<Status33>() {
	override fun get(rs: ResultSet, index: Int, dbSqlType: Int) = Status33.valueOf(rs.getString(index))
	override fun set(st: PreparedStatement, index: Int, value: Status33?, dbSqlType: Int) = st.setString(index, value.toString())
}

@DbTable("tester")
data class Tester33(@DbId val id: Long, @DbColumn val name: Status33, @DbColumn val value: Int)