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

import jodd.db.type.SqlType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class DbUtilTest {

	@Nested
	@DisplayName("tests for close(Statement statement)")
	class CloseStatement {

		@Test
		void close_with_null_statement() {
			DbUtil.close((Statement) null);
		}

		@Test
		void close_with_thrown_exception() throws SQLException {
			Statement mock = Mockito.mock(Statement.class);
			Mockito.doThrow(SQLException.class).when(mock).close();
			DbUtil.close(mock);

			// Mock verify
			Mockito.verify(mock, Mockito.times(1)).close();			
		}

		@Test
		void close_without_exception() throws SQLException {
			Statement mock = Mockito.mock(Statement.class);
			DbUtil.close(mock);

			// Mock verify
			Mockito.verify(mock, Mockito.times(1)).close();
		}

	}

	@Nested
	@DisplayName("tests for close(ResultSet resultSet)")
	class CloseResultSet {

		@Test
		void close_with_null_resultSet() {
			DbUtil.close((ResultSet) null);
		}

		@Test
		void close_with_thrown_exception() throws SQLException {
			ResultSet mock = Mockito.mock(ResultSet.class);
			Mockito.doThrow(SQLException.class).when(mock).close();
			DbUtil.close(mock);

			// Mock verify
			Mockito.verify(mock, Mockito.times(1)).close();
		}

		@Test
		void close_without_exception() throws SQLException {
			ResultSet mock = Mockito.mock(ResultSet.class);
			DbUtil.close(mock);

			// Mock verify
			Mockito.verify(mock, Mockito.times(1)).close();
		}

	}

	@Nested
	@DisplayName("tests for getFirstLong(ResultSet resultSet)")
	class GetFirstLong {

		@Test
		void getFirstLong_with_empty_resultset() throws SQLException {
			final long expected = -1L;

			ResultSet mock = Mockito.mock(ResultSet.class);
			Mockito.when(mock.next()).thenReturn(Boolean.FALSE);

			final long actual = DbUtil.getFirstLong(mock);

			// asserts
			assertEquals(expected, actual);

			// Mock verify
			Mockito.verify(mock, Mockito.times(1)).next();
			Mockito.verify(mock, Mockito.times(0)).getLong(0);
		}

		@Test
		void getFirstLong_with_filled_resultset() throws SQLException {
			final long expected = 23L;

			ResultSet mock = Mockito.mock(ResultSet.class);
			Mockito.when(mock.next()).thenReturn(Boolean.TRUE);
			Mockito.when(mock.getLong(1)).thenReturn(expected);

			final long actual = DbUtil.getFirstLong(mock);

			// asserts
			assertEquals(expected, actual);

			// Mock verify
			Mockito.verify(mock, Mockito.times(1)).next();
			Mockito.verify(mock, Mockito.times(1)).getLong(1);
		}

	}


	@Nested
	@DisplayName("tests for getFirstInt(ResultSet resultSet)")
	class GetFirstInt {

		@Test
		void getFirstLong_with_empty_resultset() throws SQLException {
			final int expected = -1;

			ResultSet mock = Mockito.mock(ResultSet.class);
			Mockito.when(mock.next()).thenReturn(Boolean.FALSE);

			final long actual = DbUtil.getFirstInt(mock);

			// asserts
			assertEquals(expected, actual);

			// Mock verify
			Mockito.verify(mock, Mockito.times(1)).next();
			Mockito.verify(mock, Mockito.times(0)).getLong(0);
		}

		@Test
		void getFirstLong_with_filled_resultset() throws SQLException {
			final int expected = 23;

			ResultSet mock = Mockito.mock(ResultSet.class);
			Mockito.when(mock.next()).thenReturn(Boolean.TRUE);
			Mockito.when(mock.getInt(1)).thenReturn(expected);

			final long actual = DbUtil.getFirstInt(mock);

			// asserts
			assertEquals(expected, actual);

			// Mock verify
			Mockito.verify(mock, Mockito.times(1)).next();
			Mockito.verify(mock, Mockito.times(1)).getInt(1);
		}

	}

	@Nested
	@DisplayName("tests for getFirstObject(ResultSet resultSet)")
	class GetFirstObject {

		@Test
		void getFirstObject_with_empty_resultset() throws SQLException {
			final Object expected = null;

			ResultSet mock = Mockito.mock(ResultSet.class);
			Mockito.when(mock.next()).thenReturn(Boolean.FALSE);

			final Object actual = DbUtil.getFirstObject(mock);

			// asserts
			assertEquals(expected, actual);

			// Mock verify
			Mockito.verify(mock, Mockito.times(1)).next();
			Mockito.verify(mock, Mockito.times(0)).getLong(0);
		}

		@Test
		void getFirstObject_with_filled_resultset() throws SQLException {
			final Object expected = "Hi Jodd!";

			ResultSet mock = Mockito.mock(ResultSet.class);
			Mockito.when(mock.next()).thenReturn(Boolean.TRUE);
			Mockito.when(mock.getObject(1)).thenReturn(expected);

			final Object actual = DbUtil.getFirstObject(mock);

			// asserts
			assertEquals(expected, actual);

			// Mock verify
			Mockito.verify(mock, Mockito.times(1)).next();
			Mockito.verify(mock, Mockito.times(1)).getObject(1);
		}

	}

	@Nested
	@DisplayName("tests for setPreparedStatementObject(PreparedStatement preparedStatement, int index, Object value, int targetSqlType)")
	class SetPreparedStatementObject {

		private PreparedStatement mock = null;
		private final int index = 1;

		@BeforeEach
		void before() {
			mock = Mockito.mock(PreparedStatement.class);
		}

		@Test
		void value_is_null() throws SQLException {
			DbUtil.setPreparedStatementObject(mock, index, null, Types.INTEGER);

			Mockito.verify(mock, Mockito.times(1)).setNull(index, Types.NULL);
		}

		@ParameterizedTest
		@CsvSource(value = {"Jodd, 12", "Jodd, -1", "Jodd, 1"})
		void call_setString(String value, int targetSqlType) throws SQLException {
			DbUtil.setPreparedStatementObject(mock, index, value, targetSqlType);

			Mockito.verify(mock, Mockito.times(1)).setString(index, value);
		}

		@ParameterizedTest
		@CsvSource(value = {"23, 4", "23, 5", "23, -6"})
		void call_setInt(Integer value, int targetSqlType) throws SQLException {
			DbUtil.setPreparedStatementObject(mock, index, value, targetSqlType);

			Mockito.verify(mock, Mockito.times(1)).setInt(index, value);
		}

		@ParameterizedTest
		@CsvSource(value = {"77, -5"})
		void call_setLong(Long value, int targetSqlType) throws SQLException {
			DbUtil.setPreparedStatementObject(mock, index, value, targetSqlType);

			Mockito.verify(mock, Mockito.times(1)).setLong(index, value);
		}

		@ParameterizedTest
		@CsvSource(value = {"true, 16", "false, -7"})
		void call_setBoolean(Boolean value, int targetSqlType) throws SQLException {
			DbUtil.setPreparedStatementObject(mock, index, value, targetSqlType);

			Mockito.verify(mock, Mockito.times(1)).setBoolean(index, value);
		}

		@ParameterizedTest
		@CsvSource(value = {"1511517069757, 91"})
		void call_setDate(@ConvertWith(ToSqlDateArgumentConverter.class) Date value, int targetSqlType) throws SQLException {
			DbUtil.setPreparedStatementObject(mock, index, value, targetSqlType);

			Mockito.verify(mock, Mockito.times(1)).setDate(index, value);
		}

		@ParameterizedTest
		@CsvSource(value = {"33.11, 2", "111.11, 3"})
		void call_setBigDecimal(@ConvertWith(ToBigDecimalArgumentConverter.class) BigDecimal value, int targetSqlType) throws SQLException {
			DbUtil.setPreparedStatementObject(mock, index, value, targetSqlType);

			Mockito.verify(mock, Mockito.times(1)).setBigDecimal(index, value);
		}

		@ParameterizedTest
		@CsvSource(value = {"45.66, 8"})
		void call_setDouble(Double value, int targetSqlType) throws SQLException {
			DbUtil.setPreparedStatementObject(mock, index, value, targetSqlType);

			Mockito.verify(mock, Mockito.times(1)).setDouble(index, value);
		}

		@ParameterizedTest
		@CsvSource(value = {"45.66, 7", "88.55, 6"})
		void call_setFloat(Float value, int targetSqlType) throws SQLException {
			DbUtil.setPreparedStatementObject(mock, index, value, targetSqlType);

			Mockito.verify(mock, Mockito.times(1)).setFloat(index, value);
		}

		@ParameterizedTest
		@CsvSource(value = {"21:44:06, 92"})
		void call_setTime(@ConvertWith(ToSqlTimeArgumentConverter.class) Time value, int targetSqlType) throws SQLException {
			DbUtil.setPreparedStatementObject(mock, index, value, targetSqlType);

			Mockito.verify(mock, Mockito.times(1)).setTime(index, value);
		}

		@ParameterizedTest
		@CsvSource(value = {"1511517069757, 93"})
		void call_setTimestamp(@ConvertWith(ToSqlTimestampArgumentConverter.class) Timestamp value, int targetSqlType) throws SQLException {
			DbUtil.setPreparedStatementObject(mock, index, value, targetSqlType);

			Mockito.verify(mock, Mockito.times(1)).setTimestamp(index, value);
		}

		@ParameterizedTest
		@CsvSource(value = {"65, -2", "123,-3"})
		void call_setBytes(@ConvertWith(ToByteArrayArgumentConverter.class) byte[] value, int targetSqlType) throws SQLException {
			DbUtil.setPreparedStatementObject(mock, index, value, targetSqlType);

			Mockito.verify(mock, Mockito.times(1)).setBytes(index, value);
		}

		@Test
		void call_setObject() throws SQLException {
			final Object value = new Object();
			final int targetSqlType = Types.BLOB; // Blob not used before
			DbUtil.setPreparedStatementObject(mock, index, value, targetSqlType);

			Mockito.verify(mock, Mockito.times(1)).setObject(index, value, targetSqlType);
		}

		@Test
		void call_setObject_with_DB_SQLTYPE_NOT_AVAILABLE() throws SQLException {
			final Object value = new Object();
			final int targetSqlType = SqlType.DB_SQLTYPE_NOT_AVAILABLE;
			DbUtil.setPreparedStatementObject(mock, index, value, targetSqlType);

			Mockito.verify(mock, Mockito.times(1)).setObject(index, value);
		}
	}

	// all converters are simple - no special handling is available - e.g. support different input data / types

	static class ToSqlDateArgumentConverter extends SimpleArgumentConverter {

		@Override
		protected Object convert(Object source, Class<?> targetType) {
			assertEquals(java.sql.Date.class, targetType, "Can only convert to " + Date.class.getCanonicalName());
			Long value = null;
			try {
				value = Long.parseLong(source.toString());
			} catch (Exception e) {
				fail("failure while converting " + source + " into an instance of " + targetType.getCanonicalName() );
			}
			return new Date(value);
		}

	}

	static class ToBigDecimalArgumentConverter extends SimpleArgumentConverter {

		@Override
		protected Object convert(Object source, Class<?> targetType) {
			assertEquals(BigDecimal.class, targetType, "Can only convert to " + BigDecimal.class.getCanonicalName());
			try {
				return BigDecimal.valueOf(Double.parseDouble(source.toString()));
			} catch (Exception e) {
				fail("failure while converting " + source + " into an instance of " + targetType.getCanonicalName());
			}
			return null;
		}

	}

	static class ToSqlTimeArgumentConverter extends SimpleArgumentConverter {

		@Override
		protected Object convert(Object source, Class<?> targetType) {
			assertEquals(Time.class, targetType, "Can only convert to " + Time.class.getCanonicalName());
			try {
				return Time.valueOf(source.toString());
			} catch (Exception e) {
				fail("failure while converting " + source + " into an instance of " + targetType.getCanonicalName());
			}
			return null;
		}

	}

	static class ToSqlTimestampArgumentConverter extends SimpleArgumentConverter {

		@Override
		protected Object convert(Object source, Class<?> targetType) {
			assertEquals(Timestamp.class, targetType, "Can only convert to " + Timestamp.class.getCanonicalName());
			try {
				return new Timestamp(Long.parseLong(source.toString()));
			} catch (Exception e) {
				fail("failure while converting " + source + " into an instance of " + targetType.getCanonicalName());
			}
			return null;
		}

	}

	static class ToByteArrayArgumentConverter extends SimpleArgumentConverter {

		@Override
		protected Object convert(Object source, Class<?> targetType) {
			assertEquals(byte[].class, targetType, "Can only convert to " + byte[].class.getCanonicalName());
			try {
				return new byte[] {Byte.parseByte(source.toString())};
			} catch (Exception e) {
				fail("failure while converting " + source + " into an instance of " + targetType.getCanonicalName());
			}
			return null;
		}

	}
}
