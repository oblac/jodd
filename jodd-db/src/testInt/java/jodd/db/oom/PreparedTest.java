package jodd.db.oom;

import jodd.db.DbQuery;
import jodd.db.DbSession;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PreparedTest extends DbBaseTest {

	public class PostgreSql extends PostgreSqlDbAccess {
		@Override
		public String getCreateTableSql() {
			return "create table TESTER (" +
				"ID			SERIAL," +
				"NAME		varchar(20)	NOT NULL," +
				"VALUE		integer NULL," +
				"primary key (ID)" +
				')';
		}

		@Override
		public String getTableName() {
			return "TESTER";
		}
	}

	@Test
	public void testPreparedStatementDebugFalse() {
		DbBaseTest.DbAccess db = new PreparedTest.PostgreSql();
		init();
		db.initDb();
		connect();

		db.createTables();
		try {
			test(false);
		} finally {
			db.close();
		}
	}

	@Test
	public void testPrepredStatementDebugTrue() {
		DbBaseTest.DbAccess db = new PreparedTest.PostgreSql();
		init();
		db.initDb();
		connect();

		db.createTables();
		try {
			test(true);
		} finally {
			db.close();
		}
	}

	private void test(final boolean debug) {
		DbSession session = new DbSession();

		DbQuery dbQuery = new DbQuery(session, "select * from TESTER where id=:id and name=:name");
		dbQuery.setDebug(debug);

		dbQuery.setInteger("id", 3);
		dbQuery.setString("name", "John");

		if (debug) {
			assertEquals("select * from TESTER where id=3 and name='John'", dbQuery.toString());
		} else {
			assertEquals("select * from TESTER where id=? and name=?", dbQuery.toString());
		}

		session.closeSession();
	}

}
