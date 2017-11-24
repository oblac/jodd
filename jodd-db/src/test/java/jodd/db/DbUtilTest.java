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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
		}

		@Test
		void close_without_exception() throws SQLException {
			Statement mock = Mockito.mock(Statement.class);
			DbUtil.close(mock);
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

}