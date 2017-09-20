package jodd.db;

import jodd.db.fixtures.DbH2TestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

public class DbStatementTest extends DbH2TestCase {

	@Test
	public void testStatementVsPreparedStatement() throws Exception {
		DbSession dbSession = createDbSession();

		DbQuery q = new DbQuery("select 1;");

		try {
			q.setString(1, "value");
			fail("error");
		}
		catch (DbSqlException ignore) {
		}

		dbSession.close();
	}
}
