// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom;

import jodd.db.DbManager;
import jodd.db.DbQuery;
import jodd.db.DbSession;
import jodd.db.oom.sqlgen.DbEntitySql;
import jodd.db.oom.tst.Tester;
import jodd.db.pool.CoreConnectionPool;
import junit.framework.TestCase;

import java.util.List;

/**
 * Live database test. Requires database services to be started.
 * There must exist the database: "jodd-test".
 */
public class LiveDatabaseTest extends TestCase {

	public static final String DB_NAME = "jodd-test";

	CoreConnectionPool cp;
	DbOomManager dboom;

	// ---------------------------------------------------------------- common

	protected LiveDatabaseTest init(boolean strictCompare) {
		DbOomManager.resetAll();

		dboom = DbOomManager.getInstance();
		dboom.setStrictCompare(strictCompare);

		cp = new CoreConnectionPool();
		return this;
	}

	protected void connect() {
		cp.init();

		DbManager.getInstance().setConnectionProvider(cp);
		dboom.registerEntity(Tester.class);
	}

	interface DbAccess {
		void initDb();
		void createTables();
	}

	/**
	 * DATABASES TO TEST!
	 */
	DbAccess[] databases = new DbAccess[] {
			new MySql(),
			new PostgreSql(),
			// new HsqlDb(), using old hsqldb, need to test with the latest one
			// but hsqldb 2 is on java 6
	};

	/**
	 * MySql.
	 */
	public class MySql implements DbAccess {

		public void initDb() {
			cp.setDriver("com.mysql.jdbc.Driver");
			cp.setUrl("jdbc:mysql://localhost:3306/" + DB_NAME);
			cp.setUser("root");
			cp.setPassword("root!");

			if (dboom.isStrictCompare()) {
				dboom.getTableNames().setLowercase(true);
				dboom.getColumnNames().setUppercase(true);
			}
		}

		public void createTables() {
			DbSession session = new DbSession();

			String sql = "create table TESTER (" +
								"ID			INT UNSIGNED NOT NULL AUTO_INCREMENT," +
								"NAME		VARCHAR(20)	not null," +
								"VALUE		INT NULL," +
								"primary key (ID)" +
								')';

			DbQuery query = new DbQuery(session, sql);
			query.executeUpdateAndClose();

			session.closeSession();
			assertTrue(query.isClosed());
		}
	}

	/**
	 * PostgreSql.
	 */
	public class PostgreSql implements DbAccess {

		public void initDb() {
			cp.setDriver("org.postgresql.Driver");
			cp.setUrl("jdbc:postgresql://localhost/" + DB_NAME);
			cp.setUser("postgres");
			cp.setPassword("root!");

			if (dboom.isStrictCompare()) {
				dboom.getTableNames().setLowercase(true);
				dboom.getColumnNames().setLowercase(true);
			}
		}

		public void createTables() {
			DbSession session = new DbSession();

			String sql = "create table TESTER (" +
								"ID			SERIAL," +
								"NAME		varchar(20)	NOT NULL," +
								"VALUE		integer NULL," +
								"primary key (ID)" +
								')';

			DbQuery query = new DbQuery(session, sql);
			query.executeUpdateAndClose();

			session.closeSession();
			assertTrue(query.isClosed());
		}
	}

	public class HsqlDb implements DbAccess {

		public void initDb() {
			cp = new CoreConnectionPool();
			cp.setDriver("org.hsqldb.jdbcDriver");
			cp.setUrl("jdbc:hsqldb:mem:test");
			cp.setUser("sa");
			cp.setPassword("");

			if (dboom.isStrictCompare()) {
				dboom.getTableNames().setUppercase(true);
				dboom.getColumnNames().setUppercase(true);
			}
		}

		public void createTables() {
			DbSession session = new DbSession();

			String sql = "create table TESTER (" +
								"ID			IDENTITY," +
								"NAME		varchar(20)	NOT NULL," +
								"VALUE		integer NULL," +
								"primary key (ID)" +
								')';

			DbQuery query = new DbQuery(session, sql);
			query.executeUpdateAndClose();

			session.closeSession();
			assertTrue(query.isClosed());
		}
	}

	// ---------------------------------------------------------------- drop

	protected void close() {
		DbSession session = new DbSession();

		DbQuery query = new DbQuery(session, "drop table TESTER");
		query.executeUpdateAndClose();

		session.closeSession();
		assertTrue(query.isClosed());

		cp.close();
	}

	// ---------------------------------------------------------------- test

	public void testDb() {

		for (int i = 0; i < 2; i++) {
			boolean strict = i == 0;

			for (DbAccess db : databases) {
				init(strict);
				db.initDb();
				connect();
				db.createTables();

				try {
					workoutEntity();
				} catch (Exception ex) {
					ex.printStackTrace();
					fail(ex.toString());
				} finally {
					close();
				}
			}
		}
	}

	// ---------------------------------------------------------------- workout

	protected void workoutEntity() {
		DbSession session = new DbSession();

		Tester tester = new Tester();
		tester.setName("one");
		tester.setValue(Integer.valueOf(7));

		DbOomQuery dbOomQuery = DbOomQuery.query(session, DbEntitySql.insert(tester));
		dbOomQuery.setGeneratedKey();
		dbOomQuery.executeUpdate();
		assertDb(session, "{1,one,7}");

		long key = dbOomQuery.getGeneratedKey();
		tester.setId(Long.valueOf(key));
		dbOomQuery.close();

		assertEquals(1, tester.getId().longValue());

		tester.setName("seven");
		DbOomQuery.query(session, DbEntitySql.updateAll(tester)).executeUpdateAndClose();
		assertDb(session, "{1,seven,7}");

		tester.setName("SEVEN");
		DbOomQuery.query(session, DbEntitySql.update(tester)).executeUpdateAndClose();
		assertDb(session, "{1,SEVEN,7}");

		tester.setName("seven");
		DbOomQuery.query(session, DbEntitySql.updateColumn(tester, "name")).executeUpdateAndClose();
		assertDb(session, "{1,seven,7}");

		tester = new Tester();
		tester.setId(Long.valueOf(2));
		tester.setName("two");
		tester.setValue(Integer.valueOf(2));
		DbOomQuery.query(session, DbEntitySql.insert(tester)).executeUpdateAndClose();
		assertDb(session, "{1,seven,7}{2,two,2}");

		long count = DbOomQuery.query(session, DbEntitySql.count(Tester.class)).executeCountAndClose();
		assertEquals(2, count);

		tester = DbOomQuery.query(session, DbEntitySql.findById(Tester.class, Integer.valueOf(2))).findOneAndClose(Tester.class);
		assertNotNull(tester);
		assertEquals(2, tester.getId().longValue());
		assertEquals("two", tester.getName());
		assertEquals(2, tester.getValue().intValue());

		tester = DbOomQuery
				.query(session, DbEntitySql
						.findById(Tester.class, Integer.valueOf(2))
						.aliasColumnsAs(ColumnAliasType.COLUMN_CODE))
				.findOneAndClose(Tester.class);
		assertNotNull(tester);
		assertEquals(2, tester.getId().longValue());

		tester = DbOomQuery
				.query(session, DbEntitySql
						.findById(Tester.class, Integer.valueOf(2))
						.aliasColumnsAs(ColumnAliasType.TABLE_REFERENCE))
				.findOneAndClose(Tester.class);
		assertNotNull(tester);
		assertEquals(2, tester.getId().longValue());

		tester = DbOomQuery
				.query(session, DbEntitySql
						.findById(Tester.class, Integer.valueOf(2))
						.aliasColumnsAs(ColumnAliasType.TABLE_NAME))
				.findOneAndClose(Tester.class);
		assertNotNull(tester);
		assertEquals(2, tester.getId().longValue());

		tester = (Tester) DbOomQuery
				.query(session, DbEntitySql
						.findById(Tester.class, Integer.valueOf(2))
						.aliasColumnsAs(ColumnAliasType.COLUMN_CODE))	// fixes POSTGRESQL
				.findOneAndClose();
		assertNotNull(tester);

		session.closeSession();
	}

	// ---------------------------------------------------------------- util

	protected void assertDb(DbSession dbSession, String expected) {
		DbOomQuery query = new DbOomQuery(dbSession, "select * from TESTER order by ID");
		List<Tester> testerList = query.list(Tester.class);

		StringBuilder sb = new StringBuilder();
		for (Tester tester : testerList) {
			sb.append(tester.toString());
		}

		assertEquals(expected, sb.toString());
	}

}
