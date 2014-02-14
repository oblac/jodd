// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.page;

import jodd.joy.page.db.HsqlDbPager;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DbPagerTest {

	public class MyHsqlDbPager extends HsqlDbPager {
		public String buildCountSql2(String sql) {
			return super.buildCountSql(sql);
		}
	}

	@Test
	public void testHsqlDbPager() {
		MyHsqlDbPager hsqlDbPager = new MyHsqlDbPager();

		String sql = hsqlDbPager.buildCountSql2("select * from User u where u.id > 10");
		assertEquals("select count(*) from User u where u.id > 10", sql);

		sql = hsqlDbPager.buildCountSql2("select u.id, (select name from Club where...) as cname from User u where u.id > 10");
		assertEquals("select count(*) from User u where u.id > 10", sql);

		sql = hsqlDbPager.buildCountSql2(
				"select u.id, (select name from Club where...) as cname," +
				" (select id from Town...) as townId from User u where u.id > 10");
		assertEquals("select count(*) from User u where u.id > 10", sql);
	}
}
