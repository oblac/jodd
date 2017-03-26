package jodd.db.oom;

import jodd.db.DbCallResult;
import jodd.db.DbQuery;
import jodd.db.DbSession;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CallableTest extends DbBaseTest {

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
	public void testCallableStatement() {
		DbBaseTest.DbAccess db = new PostgreSql();
		init();
		db.initDb();
		connect();

		db.createTables();
		try {
			test();
		} finally {
			db.close();
		}
	}

	private void test() {
		DbSession session = new DbSession();

		DbQuery dbQuery = new DbQuery(session, "{ :upp = call upper( :str ) }");

		dbQuery.setString("str", "some lowercase value");
		dbQuery.outString("upp");

		DbCallResult r = dbQuery.executeCall();

		assertEquals("SOME LOWERCASE VALUE", r.getString("upp"));

		session.closeSession();
	}

}
