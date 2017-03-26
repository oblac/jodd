package jodd.db.fixtures;

import jodd.db.DbManager;
import jodd.db.DbQuery;
import jodd.db.DbSession;
import jodd.db.jtx.DbJtxTransactionManager;
import jodd.db.pool.CoreConnectionPool;
import jodd.db.querymap.DbPropsQueryMap;
import jodd.log.LoggerFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;

/**
 * Abstract base test class for working with databases.
 */
public abstract class DbTestBase {

	protected DbJtxTransactionManager dbtxm;
	protected static CoreConnectionPool cp;

	@Before
	public void setUp() throws Exception {
		DbManager.getInstance().setQueryMap(new DbPropsQueryMap());

		LoggerFactory.setLoggerFactory(new TestLoggerFactory());
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

	@After
	public void tearDown() throws Exception {
		dbtxm.close();
		dbtxm = null;
	}

	@AfterClass
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