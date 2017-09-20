package jodd.db.oom;

import jodd.db.DbSession;
import jodd.db.oom.fixtures.Status33;
import jodd.db.oom.fixtures.Status33SqlType;
import jodd.db.oom.fixtures.Tester3;
import jodd.db.oom.fixtures.Tester33;
import jodd.db.oom.sqlgen.DbEntitySql;
import jodd.db.type.SqlTypeManager;
import org.junit.jupiter.api.Test;

public class Kotlin399Test extends DbBaseTest {

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
	public void test399() {
		DbAccess db = new PostgreSql();

		System.out.println("\t" + db.getClass().getSimpleName());
		init();
		db.initDb();
		connect();

		db.createTables();
		SqlTypeManager.register(Tester3.Status.class, StatusSqlType.class);

		try {
			workoutJavaEntity();
			workoutKotlinEntity();
		} finally {
			db.close();
		}
	}

	private void workoutJavaEntity() {
		DbSession session = new DbSession();

		Tester3 tester = new Tester3();
		tester.setName(Tester3.Status.SELECTED);
		tester.setValue(7);

		DbOomQuery dbOomQuery = DbOomQuery.query(session, DbEntitySql.insert(tester));
		dbOomQuery.setGeneratedKey();
		dbOomQuery.executeUpdate();

		session.closeSession();
	}

	private void workoutKotlinEntity() {
		SqlTypeManager.register(Status33.class, Status33SqlType.class);
		DbSession session = new DbSession();

		Tester33 tester = new Tester33(9, Status33.SELECTED, 3);

		DbOomQuery dbOomQuery = DbOomQuery.query(session, DbEntitySql.insert(tester));
		dbOomQuery.setGeneratedKey();
		dbOomQuery.executeUpdate();

		session.closeSession();
	}

}
