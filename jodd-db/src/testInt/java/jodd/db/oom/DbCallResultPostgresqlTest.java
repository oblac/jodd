package jodd.db.oom;

import jodd.db.DbCallResult;
import jodd.db.DbOom;
import jodd.db.DbQuery;
import jodd.db.DbSession;
import jodd.db.pool.CoreConnectionPool;
import org.junit.jupiter.api.*;

import java.sql.Date;

import static jodd.db.oom.DbBaseTest.DB_NAME;
import static jodd.db.oom.DbBaseTest.dbhost;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DbCallResultPostgresqlTest {

	private static CoreConnectionPool connectionPool;

	private DbSession session;
	private DbOom dbOom;

	@BeforeAll
	static void createConnectionPool() {
		connectionPool = new CoreConnectionPool();
		connectionPool.setDriver("org.postgresql.Driver");
		connectionPool.setUrl("jdbc:postgresql://" + dbhost() + "/" + DB_NAME);
		connectionPool.setUser("postgres");
		connectionPool.setPassword("root!");

		connectionPool.init();

		try (final DbSession dbSession = new DbSession(connectionPool)) {
			final DbOom dbOom = DbOom.create().withConnectionProvider(connectionPool).get(); // not shutdown after creating function!
			new DbQuery(dbOom, dbSession, "CREATE OR REPLACE function test_float (r_value real) returns real as $$ begin  return r_value ; end;  $$ LANGUAGE 'plpgsql' VOLATILE;").executeUpdate();
		}

	}

	@AfterAll
	static void closeConnectionPool() {
		if (connectionPool != null) {
			connectionPool.close();
		}
	}

	@BeforeEach
	void setup() {
		session = new DbSession(connectionPool);
		dbOom = DbOom.create().withConnectionProvider(connectionPool).get();
	}

	@AfterEach
	void teardown() {
		if (session != null) {
			session.closeSession();
		}
		session = null;
		if (dbOom != null) {
			dbOom.shutdown();
		}
		dbOom = null;
	}

	@Test
	void testGetInteger() {
		DbQuery dbQuery = new DbQuery(dbOom, session, "{ :value = call abs( :input ) }");
		dbQuery.setDebug(true);

		dbQuery.setInteger("input", 5656);
		dbQuery.outInteger("value");

		DbCallResult r = dbQuery.executeCall();

		assertEquals(5656, r.getInteger("value"));
		assertEquals(5656, r.getInteger(1));
		assertEquals(1, r.getAllInteger("value").length);
		assertEquals(5656, r.getAllInteger("value")[0]);
	}

	@Test
	void testGetBoolean() {
		DbQuery dbQuery = new DbQuery(dbOom, session, "{ :value = call date_eq( :input1, :input2 ) }");
		dbQuery.setDebug(true);

		final Date now = new Date(System.currentTimeMillis());
		dbQuery.setDate("input1", now);
		dbQuery.setDate("input2", now);
		dbQuery.outBoolean("value");

		DbCallResult r = dbQuery.executeCall();

		assertEquals(true, r.getBoolean("value"));
		assertEquals(true, r.getBoolean(1));
		assertEquals(1, r.getAllBoolean("value").length);
		assertEquals(true, r.getAllBoolean("value")[0]);
	}

	@Test
	void testGetByte() {
		DbQuery dbQuery = new DbQuery(dbOom, session, "{ :value = call abs( :input ) }");
		dbQuery.setDebug(true);

		dbQuery.setByte("input", 22);
		dbQuery.outByte("value");

		DbCallResult r = dbQuery.executeCall();

		assertEquals(22, r.getByte("value"));
		assertEquals(22, r.getByte(1));
		assertEquals(1, r.getAllByte("value").length);
		assertEquals(22, r.getAllByte("value")[0]);
	}

	@Test
	void testGetDouble() {
		DbQuery dbQuery = new DbQuery(dbOom, session, "{ :value = call abs( :input ) }");
		dbQuery.setDebug(true);

		final double expected = 65433.77844D;

		dbQuery.setDouble("input", expected);
		dbQuery.outDouble("value");

		DbCallResult r = dbQuery.executeCall();

		assertEquals(expected, r.getDouble("value"));
		assertEquals(expected, r.getDouble(1));
		assertEquals(1, r.getAllDouble("value").length);
		assertEquals(expected, r.getAllDouble("value")[0]);
	}

	@Disabled(value = "A CallableStatement function was executed and the out parameter 1 was of type java.sql.Types=7 however type java.sql.Types=8 was registered.")
	@Test
	void testGetFloat() {
		DbQuery dbQuery = new DbQuery(dbOom, session, "{ :value = call test_float( :input ) }");
		dbQuery.setDebug(true);

		final float expected = 55.12347F;

		dbQuery.setFloat("input", expected);
		dbQuery.outFloat("value");

		DbCallResult r = dbQuery.executeCall();

		assertEquals(expected, r.getFloat("value"));
		assertEquals(expected, r.getFloat(1));
		assertEquals(1, r.getAllFloat("value").length);
		assertEquals(expected, r.getAllFloat("value")[0]);
	}

	@Test
	void testGetString() {
		DbQuery dbQuery = new DbQuery(dbOom, session, "{ :value = call upper( :input ) }");
		dbQuery.setDebug(true);

		final String expected = "JODD MAKES FUN!";

		dbQuery.setString("input", expected.toLowerCase());
		dbQuery.outString("value");

		DbCallResult r = dbQuery.executeCall();

		assertEquals(expected, r.getString("value"));
		assertEquals(expected, r.getString(1));
		assertEquals(1, r.getAllString("value").length);
		assertEquals(expected, r.getAllString("value")[0]);
	}

	@Test
	void testGetLong() {
		DbQuery dbQuery = new DbQuery(dbOom, session, "{ :value = call abs( :input ) }");
		dbQuery.setDebug(true);

		final long expected = 54655454L;

		dbQuery.setLong("input", expected);
		dbQuery.outLong("value");

		DbCallResult r = dbQuery.executeCall();

		assertEquals(expected, r.getLong("value"));
		assertEquals(expected, r.getLong(1));
		assertEquals(1, r.getAllLong("value").length);
		assertEquals(expected, r.getAllLong("value")[0]);
	}

}
