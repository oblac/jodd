package jodd.db;

import jodd.db.connection.ConnectionProvider;
import jodd.db.oom.DbOomManager;
import jodd.log.Logger;
import jodd.log.LoggerFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * Database detector.
 */
public class DbDetector {

	private static final Logger log = LoggerFactory.getLogger(DbDetector.class);

	@FunctionalInterface
	public static interface DbOomConfigurator {
		public void configure();
	}

	public enum Db implements DbOomConfigurator {
		UNKNOWN {
			@Override
			public void configure() {
				// default settings
			}
		},
		DERBY {
			@Override
			public void configure() {
				// default settings
			}
		},
		DB2 {
			@Override
			public void configure() {
				// default settings
			}
		},
		HSQL {
			@Override
			public void configure() {
				DbOomManager dboom = DbOomManager.getInstance();
				dboom.getTableNames().setLowercase(true);
				dboom.getColumnNames().setLowercase(true);
			}
		},
		INFORMIX {
			@Override
			public void configure() {
				// default settings
			}
		},
		SQL_SERVER {
			@Override
			public void configure() {
				// default settings
			}
		},
		MYSQL {
			@Override
			public void configure() {
				// default settings
			}
		},
		ORACLE {
			@Override
			public void configure() {
				// default settings
			}
		},
		POSTGRESQL {
			@Override
			public void configure() {
				DbOomManager dboom = DbOomManager.getInstance();
				dboom.getTableNames().setLowercase(true);
				dboom.getColumnNames().setLowercase(true);
			}
		},
		SYBASE {
			@Override
			public void configure() {
				// default settings
			}
		}
	}

	/**
	 * Detects database and configure DbOom engine.
	 */
	public static Db detectDatabaseAndConfigureDbOom(ConnectionProvider cp) {
		cp.init();

		Connection connection = cp.getConnection();

		Db db = detectDatabase(connection);

		cp.closeConnection(connection);

		db.configure();

		return db;
	}

	/**
	 * Detects database and returns {@link Db}.
	 */
	public static Db detectDatabase(Connection connection) {
		final String dbName;
		final int dbMajorVersion;

		try {
			log.info("Detecting database...");

			DatabaseMetaData databaseMetaData = connection.getMetaData();
			dbName = databaseMetaData.getDatabaseProductName();
			dbMajorVersion = databaseMetaData.getDatabaseMajorVersion();
			int dbMinorVersion = databaseMetaData.getDatabaseMinorVersion();

			log.info("Database: " + dbName + " v" + dbMajorVersion + "." + dbMinorVersion);
		}
		catch (SQLException sex) {
			String msg = sex.getMessage();

			if (msg.contains("explicitly set for database: DB2")) {
				return Db.DB2;
			}

			return Db.UNKNOWN;
		}

		if (dbName.equals("Apache Derby")) {
			return Db.DERBY;
		}
		if (dbName.startsWith("DB2/")) {
			return Db.DB2;
		}
		if (dbName.equals("HSQL Database Engine")) {
			return Db.HSQL;
		}
		if (dbName.equals("Informix Dynamic Server")) {
			return Db.INFORMIX;
		}
		if (dbName.startsWith("Microsoft SQL Server")) {
			return Db.SQL_SERVER;
		}
		if (dbName.equals("MySQL")) {
			return Db.MYSQL;
		}
		if (dbName.equals("Oracle")) {
			return Db.ORACLE;
		}
		if (dbName.equals("PostgreSQL")) {
			return Db.POSTGRESQL;
		}
		if (dbName.equals("Sybase SQL Server")) {
			return Db.SYBASE;
		}
		if (dbName.equals("ASE") && (dbMajorVersion == 15)) {
			return Db.SYBASE;
		}

		return Db.UNKNOWN;
	}
}
