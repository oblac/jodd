package jodd.db;

import jodd.db.servers.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class DbDetectorTest {

	@DisplayName(value = "should detect the correct DbServer implementation with Connection instance")
	@ParameterizedTest()
	@MethodSource("createTestData")
	void testDetectDatabase(final DbServer expected, final Connection input) {
		final DbServer actual = new DbDetector().detectDatabase(input);

		// asserts
		assertNotNull(actual);
		assertTrue(expected.getClass().isAssignableFrom(actual.getClass()));
	}

	@Test
	void testDetectDatabaseForSpecialDB2Case() throws SQLException {
		final Connection connection = createMockConnection("DB2/", 55, 77);
		Mockito.when(connection.getMetaData().getDatabaseProductName()).thenThrow(new SQLException("Mock Exception .. explicitly set for database: DB2 ... for special DB2 case"));

		final DbServer actual = new DbDetector().detectDatabase(connection);

		// asserts
		assertNotNull(actual);
		assertTrue(Db2DbServer.class.isAssignableFrom(actual.getClass()));
	}

	private static Stream<Arguments> createTestData() throws SQLException {
		return Stream.of(
				Arguments.of(new DerbyDbServer(Integer.toString(22)), createMockConnection("Apache Derby", 22, 44)) , //
				Arguments.of(new Db2DbServer(Integer.toString(22)), createMockConnection("DB2/ is from IBM", 22, 44)),  //
				Arguments.of(new HsqlDbServer(Integer.toString(22)), createMockConnection("HSQL Database Engine", 22, 44)),  //
				Arguments.of(new InformixDbServer(Integer.toString(22)), createMockConnection("Informix Dynamic Server", 22, 44)),  //
				Arguments.of(new SqlServerDbServer(Integer.toString(22)), createMockConnection("Microsoft SQL Server : Jodd makes fun", 22, 44)),  //
				Arguments.of(new MySqlDbServer(Integer.toString(22)), createMockConnection("MySQL", 22, 44)),  //
				Arguments.of(new OracleDbServer(Integer.toString(22)), createMockConnection("Oracle", 22, 44)),  //
				Arguments.of(new PostgreSqlDbServer(Integer.toString(22)), createMockConnection("PostgreSQL", 22, 44)),  //
				Arguments.of(new SybaseDbServer(Integer.toString(22)), createMockConnection("Sybase SQL Server", 22, 44)),  //
				Arguments.of(new SybaseDbServer(Integer.toString(22)), createMockConnection("ASE", 15, 3)),  //
				Arguments.of(new SQLiteDbServer(Integer.toString(22)), createMockConnection("SQLite", 22, 44)),  //
				Arguments.of(new GenericDbServer(), createMockConnection("JoddDbPrductName", 22, 44))  //
		);
	}

	private static Connection createMockConnection(final String dbProductName, final int dbMajorVersion, final int dbMinorVersion) throws SQLException {

		final Connection connection = Mockito.mock(Connection.class);

		final DatabaseMetaData databaseMetaData = Mockito.mock(DatabaseMetaData.class);
		Mockito.when(databaseMetaData.getDatabaseProductName()).thenReturn(dbProductName);
		Mockito.when(databaseMetaData.getDatabaseMajorVersion()).thenReturn(dbMajorVersion);
		Mockito.when(databaseMetaData.getDatabaseMinorVersion()).thenReturn(dbMajorVersion);

		Mockito.when(connection.getMetaData()).thenReturn(databaseMetaData);

		return connection;
	}

}