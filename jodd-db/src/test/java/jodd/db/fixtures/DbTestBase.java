package jodd.db.fixtures;

import jodd.db.DbManager;
import jodd.db.DbQuery;
import jodd.db.DbSession;
import jodd.db.DbThreadSession;
import jodd.db.jtx.DbJtxTransactionManager;
import jodd.db.pool.CoreConnectionPool;
import jodd.db.querymap.DbPropsQueryMap;
import jodd.log.LoggerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;

/**
 * Abstract base test class for working with databases.
 */
public abstract class DbTestBase {

	protected DbJtxTransactionManager dbtxm;
	protected static CoreConnectionPool cp;

	@BeforeEach
	public void setUp() throws Exception {
		DbManager.getInstance().setQueryMap(new DbPropsQueryMap());

		LoggerFactory.setLoggerProvider(new TestLoggerProvider());
		if (dbtxm != null) {
			return;
		}

		// create connection pool if not created
		if (cp == null) {
			cp = new CoreConnectionPool();
			setupPool(cp);
			cp.init();
		}

		dbtxm = new DbJtxTransactionManager(cp);

		// initial data
		DbSession session = new DbSession(cp);
		initDb(session);
		session.closeSession();
	}

	@AfterEach
	public void tearDown() throws Exception {
		dbtxm.close();
		dbtxm = null;
	}

	@AfterAll
	public static void tearDownAfterClass()  throws Exception {
		cp.close();
		cp = null;
	}

	/**
	 * Configures connection pool for connectivity.
	 */
	protected abstract void setupPool(CoreConnectionPool cp);

	/**
	 * Initializes database before every test.
	 * It <b>MUST</b> cleanup any existing data or tables first!
	 */
	protected void initDb(DbSession dbSession) {
	}

	// ---------------------------------------------------------------- helpers

	/**
	 * Creates DB Session.
	 */
	protected DbSession createDbSession() {
		return new DbThreadSession(cp);
	}

	protected int executeUpdate(DbSession session, String sql) {
		return new DbQuery(session, sql).autoClose().executeUpdate();
	}

	protected void executeUpdate(String sql) {
		new DbQuery(sql).autoClose().executeUpdate();
	}

	protected long executeCount(DbSession session, String sql) {
		return new DbQuery(session, sql).autoClose().executeCount();
	}

}
