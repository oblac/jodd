package jodd.db.oom;

import jodd.db.DbHsqldbTestCase;
import jodd.db.DbSession;
import jodd.db.DbThreadSession;
import jodd.db.oom.meta.DbColumn;
import jodd.db.oom.meta.DbTable;
import jodd.db.oom.sqlgen.DbEntitySql;
import jodd.db.oom.sqlgen.DbSqlBuilder;
import jodd.db.oom.test.Boy;
import jodd.db.oom.test.Girl;

import static jodd.db.oom.ColumnAliasType.TABLE_NAME;

public class DbNoTableTest extends DbHsqldbTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		DbOomManager dbOom = DbOomManager.getInstance();
		dbOom.registerEntity(Bean1.class);
	}

	public void testMappingNoTable() {
		DbSession session = new DbThreadSession(cp);

		assertEquals(1, DbEntitySql.insert(new Girl(1, "Anna", "seduction")).query().executeUpdateAndClose());
		assertEquals(0, session.getTotalQueries());

		// one
		DbOomQuery q = new DbOomQuery(DbSqlBuilder.sql("select $C{g.id} + 10, UCASE($C{g.name}) from $T{Girl g}"));
		Object[] row = (Object[]) q.find(Integer.class, String.class);

		assertEquals(Integer.valueOf(11), row[0]);
		assertEquals("ANNA", row[1]);


		// two
		DbSqlBuilder dbSqlBuilder = DbSqlBuilder.sql("select $g.id + 10 as $C{Bean1.sum}, UCASE($g.name) as $C{Bean1.bigName}, $C{g.*} from $T{Girl g} where $g.id=1").
				aliasColumnsAs(ColumnAliasType.COLUMN_CODE);
		assertEquals("select g.ID + 10 as col_0_, UCASE(g.NAME) as col_1_, g.ID as col_2_, g.NAME as col_3_, g.SPECIALITY as col_4_ from GIRL g where g.ID=1", dbSqlBuilder.generateQuery());

		dbSqlBuilder.reset();

		q = new DbOomQuery(dbSqlBuilder);
		row = (Object[]) q.find(Bean1.class, Girl.class);

		Bean1 bean1 = (Bean1) row[0];
		Girl girl = (Girl) row[1];

		assertNotNull(bean1);
		assertEquals(Integer.valueOf(11), bean1.getSum());
		assertEquals("ANNA", bean1.getBigName());

		assertNotNull(girl);
		assertEquals("Anna", girl.name);
		assertEquals("seduction", girl.speciality);
		assertEquals(1, girl.id);



		// three
		dbSqlBuilder = DbSqlBuilder.sql("select $g.id + 10 as $C{Bean1.sum}, UCASE($g.name) as $C{Bean1.bigName}, $C{g.*} from $T{Girl g} where $g.id=1");
		assertEquals("select g.ID + 10 as Bean1$SUM, UCASE(g.NAME) as Bean1$BIG_NAME, g.ID, g.NAME, g.SPECIALITY from GIRL g where g.ID=1", dbSqlBuilder.generateQuery());

		dbSqlBuilder.reset();

		q = new DbOomQuery(dbSqlBuilder);
		row = (Object[]) q.find(Bean1.class, Girl.class);

		bean1 = (Bean1) row[0];
		girl = (Girl) row[1];

		assertNotNull(bean1);
		assertEquals(Integer.valueOf(11), bean1.getSum());
		assertEquals("ANNA", bean1.getBigName());

		assertNotNull(girl);
		assertEquals("Anna", girl.name);
		assertEquals("seduction", girl.speciality);
		assertEquals(1, girl.id);

		session.closeSession();
	}

	@DbTable
	public static class Bean1 {

		@DbColumn
		private Integer sum;

		@DbColumn
		private String bigName;

		public Integer getSum() {
			return sum;
		}

		public void setSum(Integer sum) {
			this.sum = sum;
		}

		public String getBigName() {
			return bigName;
		}

		public void setBigName(String bigName) {
			this.bigName = bigName;
		}
	}

}
