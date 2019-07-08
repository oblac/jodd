// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.db;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.stream.Stream;

import jodd.db.servers.Db2DbServer;
import jodd.db.servers.DbServer;
import jodd.db.servers.DerbyDbServer;
import jodd.db.servers.GenericDbServer;
import jodd.db.servers.HsqlDbServer;
import jodd.db.servers.InformixDbServer;
import jodd.db.servers.MySqlDbServer;
import jodd.db.servers.OracleDbServer;
import jodd.db.servers.PostgreSqlDbServer;
import jodd.db.servers.SQLiteDbServer;
import jodd.db.servers.SqlServerDbServer;
import jodd.db.servers.SybaseDbServer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

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