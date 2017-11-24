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